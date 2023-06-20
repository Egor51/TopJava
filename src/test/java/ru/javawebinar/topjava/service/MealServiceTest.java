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
import ru.javawebinar.topjava.AssertMatch;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.AssertMatch.assertMatch;
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
        new AssertMatch("id");
    }

    @Autowired
    MealService mealService;

    @Test
    public void get() {
        Meal expected = mealService.get(MEAL_ID_ADMIN, ADMIN_ID);
        log.info("expected meal = " + expected);
        log.info("actualAdmin meal = " + actualAdmin);
        assertMatch(actualAdmin, expected);
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2023, 6, 16);
        LocalDate endDate = LocalDate.of(2023, 6, 17);
        List<Meal> meals = mealService.getBetweenInclusive(startDate, endDate, USER_ID);
        Optional<Meal> expected = meals.stream()
                .filter(x -> x.getId() == MEAL_ID_USER)
                .findAny();
        assertFalse(meals.isEmpty());
        assertMatch(expected.get(), actualUser);
        log.info("expected meal = " + expected.get());
        log.info("actualAdmin meal = " + actualUser);
    }

    @Test
    public void getAll() {
        List<Meal> meals = mealService.getAll(USER_ID);
        Optional<Meal> expected = meals.stream()
                .filter(x -> x.getId() == MEAL_ID_USER)
                .findAny();
        assertFalse(meals.isEmpty());
        AssertMatch.assertMatch(expected.get(), actualUser);
    }

    @Test
    public void update() {
        Meal expected = getUpdate();
        expected.setId(MEAL_ID_UPDATE);
        Meal updated = mealService.update(expected, USER_ID);
        assertMatch(mealService.get(MEAL_ID_UPDATE, USER_ID), updated);
    }

    @Test
    public void create() {
        Meal created = mealService.create(getNewMeal(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNewMeal();
        newMeal.setId(newId);
        AssertMatch.assertMatch(created, newMeal);
        assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    public void delete() {
        mealService.delete(MEAL_ID_DELL, ADMIN_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL_ID_DELL, ADMIN_ID));
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
        assertThrows(DataAccessException.class, () ->
                mealService.create(new Meal(null, LocalDateTime.of(2023, Month.JUNE, 16, 13, 30), "Duplicate", 800), USER_ID));
    }


}

