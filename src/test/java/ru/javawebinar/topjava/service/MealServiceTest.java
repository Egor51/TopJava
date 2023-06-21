package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.MealTestData.IGNORE_FIELD;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    private static final Logger log = LoggerFactory.getLogger(MealServiceTest.class);

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    MealService mealService;

    @Test
    public void get() {
        Meal expected = mealService.get(MEAL_ID_ADMIN, ADMIN_ID);
        log.info("expected meal = " + expected);
        log.info("actualAdmin meal = " + adminMeal);
        IGNORE_FIELD.assertMatch(adminMeal, expected);
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2023, 6, 16);
        LocalDate endDate = LocalDate.of(2023, 6, 17);
        List<Meal> expected = mealService.getBetweenInclusive(startDate, endDate, USER_ID);
        IGNORE_FIELD.assertMatch(expected, mealsUserBetweenInclusive);
    }

    @Test
    public void getAll() {
        List<Meal> expected = mealService.getAll(USER_ID);
        assertFalse(allMealsUser.isEmpty());
        IGNORE_FIELD.assertMatch(expected, allMealsUser);
    }

    @Test
    public void update() {
        Meal expected = mealService.get(MEAL_ID_UPDATE,USER_ID);
        expected.setDescription("Update");
        mealService.update(expected, USER_ID);
        IGNORE_FIELD.assertMatch(expected, mealService.get(MEAL_ID_UPDATE, USER_ID));
    }

    @Test
    public void updateAnotherMeal() {
        mealService.update(adminMeal, USER_ID);
        IGNORE_FIELD.assertMatch(adminMeal,mealService.get(MEAL_ID_ADMIN,ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = mealService.create(getNewMeal(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNewMeal();
        newMeal.setId(newId);
        IGNORE_FIELD.assertMatch(created, newMeal);
        IGNORE_FIELD.assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    public void delete() {
        Meal meal = mealService.get(MEAL_ID_DELL, ADMIN_ID);
        mealService.delete(meal.getId(), ADMIN_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(meal.getId(), ADMIN_ID));
    }

    @Test
    public void getNotFound() {

        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void duplicateMealCreate() {
        Meal meal = new Meal(null, null, "Duplicate", 800);
        meal.setDateTime(userMeal.getDateTime());
        assertThrows(DataAccessException.class, () ->
                mealService.create(meal,USER_ID));
    }
}

