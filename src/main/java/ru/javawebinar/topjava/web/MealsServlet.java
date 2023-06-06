package ru.javawebinar.topjava.web;


import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.MealRepositoryImpl;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealsServlet extends HttpServlet {
    private static final Logger log = getLogger(MealsServlet.class);
    private MealRepository mealRepository;

    @Override
    public void init() {
        this.mealRepository = new MealRepositoryImpl();
    }

    /**
     * Handles HTTP GET requests and displays meals or retrieves information about a specific meal based on its ID.
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mealIdStr = req.getParameter("id");

        if (mealIdStr == null || mealIdStr.trim().isEmpty()) {
            log.debug("Redirect to meals");
            displayMeals(req, resp);
            return;
        }

        try {
            int mealId = Integer.parseInt(mealIdStr);
            Meal meal = mealRepository.getById(mealId);

            if (meal == null) {
                log.error("Meal with ID " + mealId + " does not exist");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            String mealJson = objectMapper.writeValueAsString(meal);

            PrintWriter out = resp.getWriter();
            out.print(mealJson);
            out.flush();
        } catch (NumberFormatException e) {
            log.error("Failed to parse meal ID", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Retrieves all meals, filters them and forwards to JSP for display.
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    private void displayMeals(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int caloriesPerDay = 2000;
        List<Meal> meals = mealRepository.getAll();
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(meals, LocalTime.MIN, LocalTime.MAX, caloriesPerDay);
        req.setAttribute("meals", mealsTo);
        req.getRequestDispatcher("meals.jsp").forward(req, resp);
    }

    /**
     * Handles HTTP POST requests. Creates or updates a meal.
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!isValid(req, "dateTime", "description", "calories")) {
            log.error("Required parameters are null or empty");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String mealIdStr = req.getParameter("id");
        String dateTimeStr = req.getParameter("dateTime");
        String description = req.getParameter("description");
        String caloriesStr = req.getParameter("calories");

        LocalDateTime dateTime;
        int calories;

        try {
            dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            calories = Integer.parseInt(caloriesStr);
        } catch (DateTimeParseException | NumberFormatException e) {
            log.error("Failed to parse date time or calories", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        processMeal(mealIdStr, dateTime, description, calories);
        resp.sendRedirect(req.getContextPath() + "/meals");
    }

    /**
     * Processes a meal. Updates if an id is provided, creates a new one otherwise.
     *
     * @param mealIdStr   meal id as string
     * @param dateTime    meal date and time
     * @param description meal description
     * @param calories    meal calories
     */
    private void processMeal(String mealIdStr, LocalDateTime dateTime, String description, int calories) {
        if (mealIdStr != null && !mealIdStr.trim().isEmpty()) {
            Meal updatedMeal = new Meal(Integer.parseInt(mealIdStr), dateTime, description, calories);
            mealRepository.update(updatedMeal);
        } else {
            Meal meal = new Meal(dateTime, description, calories);
            mealRepository.create(meal);
            log.debug("create meal");
        }
    }

    /**
     * Checks if all the specified request parameters are valid (not null and not empty).
     *
     * @param req    HttpServletRequest
     * @param params parameters to be checked
     * @return boolean true if all parameters are valid, false otherwise
     */
    private boolean isValid(HttpServletRequest req, String... params) {
        for (String param : params) {
            String value = req.getParameter(param);
            if (value == null || value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles HTTP DELETE requests. Deletes a meal.
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = Integer.parseInt(req.getParameter("id"));
        mealRepository.deleteById(id);
    }
}

