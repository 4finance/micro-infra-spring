package io.fourfinance.activity_tracker.activity;

import java.util.Arrays;
import java.util.List;

public class ActivityParameters {

    private final List<String> parameters;

    public ActivityParameters(String... parameters) {
        this.parameters = Arrays.asList(parameters);
    }

    public static ActivityParameters emptyActivityParameters(){
        return new ActivityParameters();
    }

    public List<String> getParameters() {
        return parameters;
    }
}
