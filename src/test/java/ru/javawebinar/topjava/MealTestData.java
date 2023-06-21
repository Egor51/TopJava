package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;


public class MealTestData {

    public static final AssertMatch<Meal> IGNORE_FIELD = new AssertMatch<>("id");
    public static final int MEAL_ID_USER = START_SEQ + 4;

    public static final int MEAL_ID_ADMIN = START_SEQ + 5;

    public static final int MEAL_ID_DELL = START_SEQ + 12;

    public static final int MEAL_ID_UPDATE = START_SEQ + 9;

    public static final List<Meal> mealsUserBetweenInclusive = Arrays.asList(
            new Meal(100000, LocalDateTime.of(2023, Month.JUNE, 16, 13, 30), "LunchUser", 800),
            new Meal(100000, LocalDateTime.of(2023, Month.JUNE, 16, 12, 0), "BreakfastUser", 500));
    public static final List<Meal> allMealsUser = Arrays.asList(
            new Meal(100000, LocalDateTime.of(2023, Month.JUNE, 18, 13, 30), "LunchUser", 800),
            new Meal(100000, LocalDateTime.of(2023, Month.JUNE, 18, 12, 0), "BreakfastUser", 500),
            new Meal(100000, LocalDateTime.of(2023, Month.JUNE, 16, 13, 30), "LunchUser", 800),
            new Meal(100000, LocalDateTime.of(2023, Month.JUNE, 16, 12, 0), "BreakfastUser", 500));
    public static final Meal userMeal = new Meal(MEAL_ID_USER, LocalDateTime.of(2023, Month.JUNE, 16, 13, 30), "LunchUser", 800);

    public static final Meal adminMeal = new Meal(MEAL_ID_ADMIN, LocalDateTime.of(2023, Month.JUNE, 16, 18, 0), "DinnerAdmin", 700);

    public static Meal getNewMeal() {
        return new Meal(LocalDateTime.of(2023, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    }

    public static Meal getUpdate() {
        Meal updated = new Meal();
        updated.setDateTime(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0));
        updated.setDescription("Тест");
        updated.setCalories(1000);
        return updated;
    }
}
