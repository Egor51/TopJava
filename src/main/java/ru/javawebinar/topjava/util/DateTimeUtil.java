package ru.javawebinar.topjava.util;

import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final LocalDateTime MIN_DATE = LocalDateTime.of(1,1,1,0,0);
    private static final LocalDateTime MAX_DATE = LocalDateTime.of(3000,1,1,0,0);

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @Nullable T lowerBound, @Nullable T upperBound) {
        return (lowerBound ==null||value.compareTo(lowerBound) >= 0) && (upperBound == null || value.compareTo(upperBound) < 0);
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime getStartInclusive(LocalDate localDate){
        return localDate!=null? localDate.atStartOfDay():MIN_DATE;
    }

    public static LocalDateTime getEndExclusive(LocalDate localDate){
        return localDate !=null? localDate.plus(1, ChronoUnit.DAYS).atStartOfDay():MAX_DATE;
    }


}

