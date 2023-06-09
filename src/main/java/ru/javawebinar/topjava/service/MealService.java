package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Collection;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Service
public class MealService {
    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }


    public Collection<Meal> getAll() {
        return repository.getAll(authUserId());
    }

    public Meal create(Meal meal) {
        return (Meal) repository.save(meal, authUserId());
    }

    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id, authUserId()), id);
    }

    public Meal get(int id) {
        return checkNotFoundWithId(repository.get(id, authUserId()), id);
    }

    public void update(Meal meal) {
        checkNotFoundWithId(meal,meal.getId());
        repository.save(meal, authUserId());
    }

}