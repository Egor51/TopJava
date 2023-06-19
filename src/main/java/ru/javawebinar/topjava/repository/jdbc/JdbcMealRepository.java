package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);
    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", meal.getId());
        parameters.put("user_id", userId);
        parameters.put("date_time", meal.getDateTime());
        parameters.put("description", meal.getDescription());
        parameters.put("calories", meal.getCalories());

        if (meal.isNew()) {
            Number newId = insertMeal.executeAndReturnKey(parameters);
            meal.setId(newId.intValue());
        } else {
            String updateQuery = "UPDATE meals SET user_id = :user_id, date_time = :date_time, description = :description, calories = :calories WHERE id = :id AND user_id = :user_id";
            namedParameterJdbcTemplate.update(updateQuery, parameters);
        }

        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        String deleteQuery = "DELETE FROM meals WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(deleteQuery, id, userId) > 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query(
                "SELECT * FROM meals WHERE id=? AND user_id=?",
                ROW_MAPPER,
                id,
                userId
        );
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        String selectQuery = "SELECT * FROM meals WHERE user_id = ?";
        return jdbcTemplate.query(selectQuery, ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        String selectQuery = "SELECT * FROM meals WHERE user_id = ? AND date_time >= ? AND date_time < ?";
        return jdbcTemplate.query(selectQuery, ROW_MAPPER, userId, startDateTime, endDateTime);
    }
}
