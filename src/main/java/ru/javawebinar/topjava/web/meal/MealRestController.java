package ru.javawebinar.topjava.web.meal;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {
    static final String REST_URL = "/rest/profile/meals";
    @GetMapping
    public Collection<Meal> getAll() {
        return super.getAll();
    }
    @GetMapping("/{id}")
    public Meal get(@RequestParam int id) {
        return super.get(id);
    }
    @PostMapping()
    public Meal create(@RequestBody Meal meal) {
        return super.create(meal);
    }
    @DeleteMapping("/{id}")
    public void delete(int id) {

        super.delete(id);
    }
    @PutMapping(value = "/{id}")
    public void update(@RequestBody Meal meal) {
        super.update(meal,meal.getId());
    }
}