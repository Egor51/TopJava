package ru.javawebinar.topjava.repository;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.slf4j.LoggerFactory.getLogger;

public class MealRepositoryImpl implements MealRepository {
    private static final Logger log = getLogger(MealRepositoryImpl.class);
    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    public MealRepositoryImpl() {
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
    public synchronized Meal update(Meal meal) {
        if (meals.containsKey(meal.getId())) {
            meals.put(meal.getId(), meal);
            log.debug("Meal with id " + meal.getId() + " updated");
            return meal;
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        meals.remove(id);
        log.debug("Meal with id " + id + " removed");
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }
}

