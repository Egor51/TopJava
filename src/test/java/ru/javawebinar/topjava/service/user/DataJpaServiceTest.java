package ru.javawebinar.topjava.service.user;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.User;

import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(DATAJPA)
public class DataJpaServiceTest extends AbstractUserServiceTest {
    @Test
    public void getUserWithMeal() {
        User expected = service.getUserWithMeal(USER_ID);
        USER_MATCHER.assertMatch(user, expected);
    }
}
