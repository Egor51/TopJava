package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo1 = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(11, 0), 2000);
        List<UserMealWithExcess> mealsTo2 = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(11, 0), 2000);
        List<UserMealWithExcess> mealsTo3 = filterByStreamsOptionalTwo(meals, LocalTime.of(7, 0), LocalTime.of(11, 0), 2000);

        System.out.println("for: ");
        mealsTo1.forEach(System.out::println);
        System.out.println("simple stream: ");
        mealsTo2.forEach(System.out::println);
        System.out.println("one stream: ");
        mealsTo3.forEach(System.out::println);

    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            caloriesSumByDate.merge(date, meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealWithExcessList = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                boolean excess = caloriesSumByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                mealWithExcessList.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
            }
        }
        return mealWithExcessList;
    }

    /**
     * Filters a list of UserMeals based on the given time range and creates a list of UserMealWithExcess objects
     * with calculated excess calories.
     *
     * @param meals          a list of UserMeal objects to be filtered
     * @param startTime      the start time of the desired time range
     * @param endTime        the end time of the desired time range
     * @param caloriesPerDay the daily limit of calories
     * @return a list of UserMealWithExcess objects with the excess calories calculated
     */

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = getCaloriesSumByDate(meals);
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(
                        meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        caloriesSumByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                ))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total calories consumed on each date in the given list of UserMeals.
     *
     * @param meals a list of UserMeal objects
     * @return a map with LocalDate as key and the sum of calories consumed on that date as value
     */

    private static Map<LocalDate, Integer> getCaloriesSumByDate(List<UserMeal> meals) {
        return meals.stream()
                .collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));
    }

    /**
     * This method processes a list of UserMeals, returning a list of UserMealWithExcess objects.
     * Each UserMealWithExcess object corresponds to a UserMeal object from the input list and includes an indicator
     * specifying if the total calories consumed on the date of the meal exceeded a defined daily calorie limit.
     * <p>
     * The method uses Java's Stream API for processing the input list in a single pass.
     * It groups meals by date and calculates the total calories consumed for each date.
     * <p>
     * Meals eaten within a specific time interval are included in the resulting list.
     * For each of these meals, the excess flag is set based on the total calorie intake for the meal's date.
     * <p>
     * The method is resilient against null entries in the input list, skipping over them during processing.
     *
     * @param meals          the list of UserMeal objects to be processed
     * @param startTime      the start time of the interval within which meals are considered for the result
     * @param endTime        the end time of the interval within which meals are considered for the result
     * @param caloriesPerDay the daily calorie limit for setting the excess flag in the result
     * @return a list of UserMealWithExcess objects corresponding to the meals from the input list
     * that were consumed within the defined interval, each indicating if the total calorie intake
     * for the meal's date exceeded the daily calorie limit.
     */

    public static List<UserMealWithExcess> filterByStreamsOptionalTwo(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class Accumulator {
            int totalCalories;
            final List<UserMeal> meals = new ArrayList<>();
        }
        return meals.stream()
                .collect(Collectors.groupingBy(
                        meal -> meal.getDateTime().toLocalDate(),
                        Collector.of(
                                Accumulator::new,
                                (info, meal) -> {
                                    info.totalCalories += meal.getCalories();
                                    if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                                        info.meals.add(meal);
                                    }
                                },
                                (info1, info2) -> {
                                    info1.totalCalories += info2.totalCalories;
                                    info1.meals.addAll(info2.meals);
                                    return info1;
                                }
                        )
                ))
                .values()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(entry ->
                        entry.meals.stream()
                                .map(meal -> new UserMealWithExcess(
                                        meal.getDateTime(),
                                        meal.getDescription(),
                                        meal.getCalories(),
                                        entry.totalCalories > caloriesPerDay
                                ))
                )
                .collect(Collectors.toList());
    }
}


