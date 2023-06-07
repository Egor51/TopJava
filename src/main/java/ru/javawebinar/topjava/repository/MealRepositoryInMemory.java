package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealRepositoryInMemory implements MealRepository {
    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    public MealRepositoryInMemory() {
        List<Meal> initialMeals = MealsUtil.getMeals();
        initialMeals.forEach(this::create);
    }

    @Override
    public Meal create(Meal meal) {
        Integer id = idGenerator.incrementAndGet();
        meal.setId(id);
        meals.put(id, meal);
        return meal;
    }

    @Override
    public Meal getById(int id) {
        return meals.get(id);
    }

    @Override
    public Meal update(Meal meal) {
        if (meals.replace(meal.getId(), meal) != null) {
            return meal;
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        meals.remove(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }
}

