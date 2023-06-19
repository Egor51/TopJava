package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;


import java.time.LocalDateTime;
import java.time.Month;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;


public class MealTestData {

    public static final int USER_ID = START_SEQ;
    public static final int MEAL_ID =START_SEQ+4;

    public static final Meal actual = new Meal(100003,LocalDateTime.of(2023, Month.JUNE, 16, 12, 0), "Breakfast", 500);
    public static final Meal actual2 = new Meal(MEAL_ID,LocalDateTime.of(2023, Month.JUNE, 16, 13, 30), "Lunch", 800);

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
