package ru.javawebinar.topjava;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
public class SummaryRule extends TestWatcher {

    @Override
    protected void finished(Description description) {
        System.out.println("Test results:");
        System.out.println("|-----------------------|------------|");
        System.out.println("| Test method           | Time       |");
        System.out.println("|-----------------------|------------|");
        TimingRules.getResults().forEach(System.out::println);
        System.out.println("|-----------------------|------------|");
    }
}