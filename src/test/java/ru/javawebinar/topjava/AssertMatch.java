package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.AbstractBaseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertMatch {
    private static String [] ignoredFields;

    public AssertMatch(String... ignoredFields) {
        AssertMatch.ignoredFields = ignoredFields;
    }

    public static <T extends AbstractBaseEntity> void assertMatch(T actual, T expected) {
        assertThat(actual).usingRecursiveComparison().ignoringFields(ignoredFields).isEqualTo(expected);
    }

    public static <T extends AbstractBaseEntity> void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static <T extends AbstractBaseEntity> void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields(ignoredFields).isEqualTo(expected);
    }
}
