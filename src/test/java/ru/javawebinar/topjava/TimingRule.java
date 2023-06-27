package ru.javawebinar.topjava;

import org.junit.AssumptionViolatedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimingRule extends Stopwatch {

    private static final Logger log = LoggerFactory.getLogger("result");
    private static final List<String> results = new ArrayList<>();

    @Override
    protected void finished(long nanos, Description description) {
        results.add(String.format("| %-20s  | %7d ms |",
                description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos)));
        log.info(String.format("%-20s : %7d ms",
                description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos)));
    }

    @Override
    protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
        results.add(String.format("| %-20s  | %7d ms | (skipped)",
                description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos)));
        log.info(String.format("%-20s : %7d ms (skipped)",
                description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos)));
    }

    public static List<String> getResults() {
        return results;
    }
}