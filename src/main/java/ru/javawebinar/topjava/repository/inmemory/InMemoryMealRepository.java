package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;

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
        Map<Integer, Meal> userMeals = repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        if (meal.isNew() || userMeals.containsKey(meal.getId())) {
            userMeals.put(meal.isNew() ? setMealId(meal) : meal.getId(), meal);
        } else {
            return null;
        }
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

    @Override
    public List<Meal> getBetweenDatesTimes(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return repository.getOrDefault(userId, new HashMap<>()).values().stream()
                .filter(meal -> {
                    LocalDateTime dateTime = meal.getDateTime();
                    LocalDate mealDate = dateTime.toLocalDate();
                    LocalTime mealTime = dateTime.toLocalTime();
                    return (startDate == null || isBetweenHalfOpen(mealDate, startDate, endDate)) &&
                            (startTime == null || isBetweenHalfOpen(mealTime, startTime, endTime));
                })
                .collect(Collectors.toList());
    }

    private int setMealId(Meal meal) {
        int id = counter.incrementAndGet();
        meal.setId(id);
        return id;
    }
}

