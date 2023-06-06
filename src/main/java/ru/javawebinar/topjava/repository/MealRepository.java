package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    Meal create(Meal meal);

    Meal getById(int id);

    Meal update(Meal meal);

    void deleteById(int id);

    List<Meal> getAll();
}

