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
     * Processes the given list of UserMeals and calculates the total calories consumed on each date.
     * For each meal consumed within the specified time range, the method creates a UserMealWithExcess object,
     * and determines whether the total calories consumed on the meal's date exceeds the daily calorie limit.
     *
     * @param meals          a list of UserMeal objects to be processed
     * @param startTime      the start time of the time range for filtering meals
     * @param endTime        the end time of the time range for filtering meals
     * @param caloriesPerDay the daily calorie limit for determining meal excess
     * @return a list of UserMealWithExcess objects representing the meals consumed within the specified time range,
     *         with an indication of whether the total calories consumed on the meal's date exceeds the daily calorie limit.
     */
    public static List<UserMealWithExcess> filterByStreamsOptionalTwo(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(
                        meal -> meal.getDateTime().toLocalDate(),
                        Collector.of(
                                () -> new int[2],
                                (a, meal) -> {
                                    a[0] += meal.getCalories();
                                    if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                                        a[1] += meal.getCalories();
                                    }
                                },
                                (a, b) -> {
                                    a[0] += b[0];
                                    a[1] += b[1];
                                    return a;
                                }
                        )
                )).entrySet().stream()
                .flatMap(entry -> entry.getValue()[1] > 0 ?
                        Stream.of(new UserMealWithExcess(entry.getKey().atTime(startTime), "", entry.getValue()[1], entry.getValue()[0] > caloriesPerDay)) :
                        Stream.empty())
                .collect(Collectors.toList());
    }
}


