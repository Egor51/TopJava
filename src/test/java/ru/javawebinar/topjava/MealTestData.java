package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;


import java.time.LocalDateTime;
import java.time.Month;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;


public class MealTestData {
    public static final int MEAL_ID_USER =START_SEQ+4;

    public static final int MEAL_ID_ADMIN =START_SEQ+5;

    public static final int MEAL_ID_DELL = START_SEQ + 12;

    public static final int MEAL_ID_UPDATE = START_SEQ + 9;

    public static final Meal actualUser = new Meal(MEAL_ID_USER,LocalDateTime.of(2023, Month.JUNE, 16, 13, 30), "LunchUser", 800);

    public static final Meal actualAdmin = new Meal(MEAL_ID_ADMIN,LocalDateTime.of(2023, Month.JUNE, 16, 18, 0), "DinnerAdmin", 700);

    public static Meal getNewMeal() {
        return new Meal(LocalDateTime.of(2023, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    }

    public static Meal getUpdate(){
        Meal updated = new Meal();
        updated.setDateTime(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0));
        updated.setDescription("Тест");
        updated.setCalories(1000);
        return updated;
    }
}
