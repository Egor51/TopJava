package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Query("SELECT m FROM Meal m WHERE m.user.id = :userId ORDER BY m.dateTime DESC")
    List<Meal> findAll(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Meal m WHERE m.id = :id AND m.user.id = :userId")
    int delete(@Param("id") int id, @Param("userId") int userId);

    List<Meal> findByUserIdAndDateTimeGreaterThanAndDateTimeLessThanOrderByDateTimeDesc(
            int userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT m FROM Meal m JOIN FETCH m.user WHERE m.id = ?1 and m.user.id = ?2")
    Meal getMealWithUser(int id, int userId);
}
