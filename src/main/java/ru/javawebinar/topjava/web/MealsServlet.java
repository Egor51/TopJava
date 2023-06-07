package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.InMemoryMealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealsServlet extends HttpServlet {
    private static final Logger log = getLogger(MealsServlet.class);
    private MealRepository mealRepository;
    static final int CALORIES_PER_DAY = 2000;

    @Override
    public void init() {
        this.mealRepository = new InMemoryMealRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mealIdStr = req.getParameter("id");
        if (mealIdStr == null || mealIdStr.trim().isEmpty()) {
            log.debug("Redirect to meals");
            displayMeals(req, resp);
            return;
        }
        int mealId = Integer.parseInt(mealIdStr);
        Meal meal = mealRepository.getById(mealId);
        if (meal == null) {
            log.debug("Meal with ID " + mealId + " does not exist");
            return;
        }
        req.setAttribute("meal", meal);
        req.getRequestDispatcher("editmeal.jsp").forward(req, resp);
    }

    private void displayMeals(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Meal> meals = mealRepository.getAll();
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(meals, LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
        req.setAttribute("meals", mealsTo);
        req.getRequestDispatcher("meals.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        if (!isValid(req, "dateTime", "description", "calories")) {
            log.debug("Required parameters are null or empty");
            return;
        }
        String mealIdStr = req.getParameter("id");
        String dateTimeStr = req.getParameter("dateTime");
        String description = req.getParameter("description");
        String caloriesStr = req.getParameter("calories");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
        int calories = Integer.parseInt(caloriesStr);
        Integer id = isNotEmpty(mealIdStr) ? Integer.parseInt(mealIdStr) : null;
        processMeal(id, dateTime, description, calories);
        resp.sendRedirect(req.getContextPath() + "/meals");
    }

    private void processMeal(Integer mealIdStr, LocalDateTime dateTime, String description, int calories) {
        if (mealIdStr != null) {
            Meal updatedMeal = new Meal(mealIdStr, dateTime, description, calories);
            mealRepository.update(updatedMeal);
            log.debug("Meal with id " + updatedMeal.getId() + " updated");
        } else {
            Meal meal = new Meal(dateTime, description, calories);
            mealRepository.create(meal);
            log.debug("created meal");
        }
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private boolean isValid(HttpServletRequest req, String... params) {
        for (String param : params) {
            String value = req.getParameter(param);
            if (!isNotEmpty(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        int id = Integer.parseInt(req.getParameter("id"));
        mealRepository.deleteById(id);
        log.debug("Meal with id " + id + " removed");
    }
}

