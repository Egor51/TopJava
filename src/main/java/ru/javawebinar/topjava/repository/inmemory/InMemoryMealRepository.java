package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, ConcurrentHashMap<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, int userId) {
            ConcurrentHashMap<Integer, Meal> userMeals = repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
            if (!meal.isNew() && !userMeals.containsKey(meal.getId())) {
                return null;
            }
            userMeals.compute(meal.isNew() ? setMealId(meal) : meal.getId(), (id, oldMeal) -> meal);
            return meal;
        }

        @Override
    public boolean delete(int id, int userId) {
        return Optional.ofNullable(repository.get(userId))
                .map(meals -> meals.remove(id) != null)
                .orElse(false);
    }

    @Override
    public Meal get(int id, int userId) {
        return Optional.ofNullable(repository.get(userId))
                .map(meals -> meals.get(id))
                .orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return Optional.ofNullable(repository.get(userId))
                .map(meals -> meals.values().stream()
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private Integer setMealId(Meal meal) {
        Integer id = counter.incrementAndGet();
        meal.setId(id);
        return id;
    }
}

