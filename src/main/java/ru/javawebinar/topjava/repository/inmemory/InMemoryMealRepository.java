package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
        MealsUtil.mealsAnotherUser.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> userMeals = repository.computeIfAbsent(userId, ConcurrentHashMap::new);
        if (meal.isNew()) {
            setMealId(meal);
            userMeals.put(meal.getId(), meal);
            return meal;
        }
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    private int setMealId(Meal meal) {
        int id = counter.incrementAndGet();
        meal.setId(id);
        return id;
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
        return getALLFilter(userId, meal -> true);
    }

    @Override
    public List<Meal> getBetweenDatesTimes(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return getALLFilter(userId, meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime));
    }

    private List<Meal> getALLFilter(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> mealMap = repository.get(userId);
        return CollectionUtils.isEmpty(mealMap) ? Collections.emptyList() :
                mealMap.values().stream()
                        .filter(filter)
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }
}
