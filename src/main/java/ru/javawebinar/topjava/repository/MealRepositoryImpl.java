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
    /*
        This is the constructor for the MealRepositoryImpl class. It initializes the repository with a set of meals obtained from the MealsUtil.getMeals() method.
    */
    public MealRepositoryImpl() {
        List<Meal> initialMeals = MealsUtil.getMeals();
        initialMeals.forEach(meal -> {
            meals.put(meal.getId(), meal);
            idGenerator.set(meal.getId());
        });
    }
    /*
        This method is used to save a Meal object to the repository. If the meal's ID is null, it generates a new ID and assigns it to the meal before saving.
    */
    @Override
    public void save(Meal meal) {
        if (meal.getId() == null) {
            Integer id = idGenerator.incrementAndGet();
            meal.setId(id);
            meals.put(id, meal);
        }
        meals.put(meal.getId(), meal);
    }
    /*
        This method is used to fetch a Meal object from the repository based on its ID. If no meal with the given ID exists, it throws a NoSuchElementException.
    */
    public Meal getById(Integer id) {
        if (!meals.containsKey(id)) {
            throw new NoSuchElementException("Meal with id " + id + " does not exist");
        }
        return meals.get(id);
    }
    /*
        This method is used to update the details of a Meal object in the repository based on its ID. If no meal with the given ID exists, it throws a NoSuchElementException.
    */
    @Override
    public void updateById(Meal meal) {
        if (!meals.containsKey(meal.getId())) {
            throw new NoSuchElementException("Meal with id " + meal.getId() + " does not exist");
        }
        meals.put(meal.getId(), meal);
        log.debug("Meal with id " + meal.getId() + " updated");
    }
    /*
        This method is used to remove a Meal object from the repository based on its ID. If no meal with the given ID exists, it throws a NoSuchElementException.
    */
    @Override
    public void deleteById(Integer id) {
        if (!meals.containsKey(id)) {
            throw new NoSuchElementException("Meal with id " + id + " does not exist");
        }
        meals.remove(id);
        log.debug("Meal with id " + id + " removed");
    }
    /*
        This method is used to retrieve a list of all Meal objects stored in the repository. The meals are returned as a new list of values from the repository's internal map.
    */
    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }
}
