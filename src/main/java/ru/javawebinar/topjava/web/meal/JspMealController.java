package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @GetMapping
    public String mealList(Model model) {
        log.info("Executing mealList method");
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @Override
    @GetMapping("/{id}")
    public Meal get(@PathVariable int id) {
        log.info("Executing get method with id: {}", id);
        return super.get(id);
    }

    @GetMapping("/delete/{id}")
    public String deleteView(@PathVariable int id) {
        log.info("Executing deleteView method with id: {}", id);
        super.delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/update/{id}")
    public String updateView(Model model, @PathVariable int id) {
        log.info("Executing updateView method with id: {}", id);
        model.addAttribute("meal", super.get(id));
        return "mealForm";
    }

    @GetMapping("/create")
    public String createView(Model model) {
        log.info("Executing createView method");
        model.addAttribute("meal", new Meal(LocalDateTime.now(), " ", 0));
        return "mealForm";
    }

    @PostMapping
    public String createOrUpdate(HttpServletRequest request) {
        log.info("Executing createOrUpdate method");
        String id = request.getParameter("id");
        Meal meal = new Meal(id.isEmpty() ? null : Integer.parseInt(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );
        if (meal.isNew()) {
            super.create(meal);
        } else {
            super.update(meal, meal.getId());
        }
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String getBetween(HttpServletRequest request, Model model) {
        log.info("Executing getBetween method");
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        model.addAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }
}


