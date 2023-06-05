package ru.javawebinar.topjava.web;

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
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealsServlet extends HttpServlet {
    private static final Logger log = getLogger(MealsServlet.class);
    private final MealRepository mealRepository;

    public MealsServlet() {
        this.mealRepository = new MealRepositoryImpl();
    }

    /*
    Handles HTTP GET requests. Calls displayMeals method to display the list of meals.
    */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("redirect to meals");
        displayMeals(req, resp);
    }

    /*
        Handles HTTP POST requests. Responsible for creating or updating a meal based on request parameters.
    */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        if (!isValid(req, "dateTime", "description", "calories")) {
            log.error("Required parameters are null or empty");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String mealIdStr = req.getParameter("id");
        String dateTimeStr = req.getParameter("dateTime");
        String description = req.getParameter("description");
        String caloriesStr = req.getParameter("calories");
        LocalDateTime dateTime = null;
        int calories = 0;
        try {
            dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            calories = Integer.parseInt(caloriesStr);
        } catch (DateTimeParseException | NumberFormatException e) {
            log.error("Failed to parse date time or calories", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        processMeal(mealIdStr, dateTime, description, calories);
        try {
            resp.sendRedirect(req.getContextPath() + "/meals");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
        Handles HTTP DELETE requests. Deletes a meal with a given id.
    */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Integer id = Integer.parseInt(req.getParameter("id"));
            mealRepository.deleteById(id);
        } catch (NumberFormatException e) {
            log.error("Failed to parse id", e);
        }
    }

    /*
        Gathers all meals from the repository, filters them, sets the filtered meals as a request attribute and forwards the request to the JSP page for display.
    */
    private void displayMeals(HttpServletRequest req, HttpServletResponse resp) {
        final int caloriesPerDay = 2000;
        try {
            List<Meal> meals = mealRepository.getAll();
            List<MealTo> mealsTo = MealsUtil.filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(23, 0), caloriesPerDay);
            req.setAttribute("meals", mealsTo);
            req.getRequestDispatcher("meals.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
        Helper method to process a meal. If an id is provided, updates the corresponding meal. If no id is provided, creates a new meal.
    */
    private void processMeal(String mealIdStr, LocalDateTime dateTime, String description, int calories) {
        if (mealIdStr != null && !mealIdStr.trim().isEmpty()) {
            Meal updatedMeal = new Meal(Integer.parseInt(mealIdStr), dateTime, description, calories);
            mealRepository.updateById(updatedMeal);
        } else {
            Meal meal = new Meal(dateTime, description, calories);
            mealRepository.save(meal);
            log.debug("create meal");
        }
    }

    /*
        Helper method to check if all the specified request parameters are valid (not null and not empty).
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
}
