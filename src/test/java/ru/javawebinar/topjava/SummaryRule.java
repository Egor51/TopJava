package ru.javawebinar.topjava;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummaryRule extends TestWatcher {
    private static final Logger log = LoggerFactory.getLogger("result");


    private static final int COLUMN_WIDTH_NAME = 25;
    private static final int COLUMN_WIDTH_TIME = 17;

    @Override
    protected void finished(Description description) {
        StringBuilder sb = new StringBuilder();
        sb.append("Test results:\n");
        sb.append("+---------------------------+-------------------+\n");
        sb.append("| Test method               | Time              |\n");
        sb.append("+---------------------------+-------------------+\n");
        TimingRule.getResults().forEach(result -> {
            String[] parts = result.split("\\|");
            String methodName = parts[1].trim();
            String time = parts[2].trim();
            String formattedMethodName = formatColumn(methodName, COLUMN_WIDTH_NAME);
            String formattedTime = formatColumn(time, COLUMN_WIDTH_TIME);
            sb.append("| ").append(formattedMethodName).append(" | ").append(formattedTime).append(" |\n");
        });
        sb.append("+---------------------------+-------------------+\n");

        log.info(sb.toString());
    }

    private String formatColumn(String value, int width) {
        if (value.length() <= width) {
            return String.format("%-" + width + "s", value);
        } else {
            return value.substring(0, width - 3) + "...";
        }
    }
}