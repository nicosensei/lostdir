package com.github.nicosensei.lostdir.rename.config;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

/**
 * Created by nicos on 11/24/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Configuration {

    private int awaitTerminationInMinutes = 5;

    private ArrayList<Task> tasks = new ArrayList<>(0);

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int getAwaitTerminationInMinutes() {
        return awaitTerminationInMinutes;
    }

    public void setAwaitTerminationInMinutes(int awaitTerminationInMinutes) {
        this.awaitTerminationInMinutes = awaitTerminationInMinutes;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
