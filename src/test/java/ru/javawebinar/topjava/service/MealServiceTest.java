package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.AssertMatch.assertMatch;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
//w
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    MealService mealService;

    @Test
    public void get() {
        Meal expected = mealService.get(MEAL_ID,USER_ID);
        assertMatch(actual2,expected);
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2023, 6, 16);
        LocalDate endDate = LocalDate.of(2023, 6, 17);
        List<Meal> meals = mealService.getBetweenInclusive(startDate, endDate, USER_ID);
        assertMatch(meals,actual,actual2);
    }

    @Test
    public void getAll() {
        List<Meal> expected = mealService.getAll(USER_ID);
        assertMatch(expected,actual,actual2);
    }

    @Test
    public void testUpdate() {
        Meal expected = getUpdate();
        expected.setId(MEAL_ID);
        mealService.update(expected,USER_ID);
        assertMatch(mealService.get(MEAL_ID, USER_ID), expected);
    }

    @Test
    public void testCreate()  {
        Meal created = mealService.create(getNewMeal(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNewMeal();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(mealService.get(newId,USER_ID), newMeal);
    }

    @Test
    public void delete() {
        mealService.delete(100006, 100001);
        assertThrows(NotFoundException.class, () -> mealService.get(100006, 100001));
    }
}

