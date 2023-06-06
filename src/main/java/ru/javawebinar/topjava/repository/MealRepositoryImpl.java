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

    /**
     * Constructor for the MealRepositoryImpl class.
     * Initializes the repository with a set of meals obtained from the MealsUtil.getMeals() method.
     */
    public MealRepositoryImpl() {
        List<Meal> initialMeals = MealsUtil.getMeals();
        initialMeals.forEach(this::create);
    }

    /**
     * Saves a Meal object to the repository.
     * If the meal's ID is null, it generates a new ID and assigns it to the meal before saving.
     * If the meal's ID exists, it updates the meal.
     *
     * @param meal Meal to be saved
     * @return saved Meal
     */
    @Override
    public Meal create(Meal meal) {
        Integer id = idGenerator.incrementAndGet();
        Meal newMeal = new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        meals.put(id, newMeal);
        return newMeal;
    }

    /**
     * Fetches a Meal object from the repository based on its ID.
     *
     * @param id id of the meal
     * @return Meal object or null if no meal with the given ID exists
     */
    @Override
    public Meal getById(int id) {
        return meals.get(id);
    }

    /**
     * Updates the details of a Meal object in the repository based on its ID.
     *
     * @param meal Meal to be updated
     * @return updated Meal or null if no meal with the given ID exists
     */
    @Override
    public Meal update(Meal meal) {
        if (meals.containsKey(meal.getId())) {
            meals.put(meal.getId(), meal);
            log.debug("Meal with id " + meal.getId() + " updated");
            return meal;
        }
        return null;
    }

    /**
     * Removes a Meal object from the repository based on its ID.
     *
     * @param id id of the meal
     */
    @Override
    public void deleteById(int id) {
        meals.remove(id);
        log.debug("Meal with id " + id + " removed");
    }

    /**
     * Retrieves a list of all Meal objects stored in the repository.
     * The meals are returned as a new list of values from the repository's internal map.
     *
     * @return List of Meal
     */
    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }
}

