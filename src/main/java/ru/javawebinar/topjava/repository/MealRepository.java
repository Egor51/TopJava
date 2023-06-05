package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    void save(Meal meal);
    Meal getById(Integer id);
    void updateById(Meal meal);
    void deleteById(Integer id);
    List<Meal> getAll();
}
