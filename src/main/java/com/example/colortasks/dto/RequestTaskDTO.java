package com.example.colortasks.dto;

import com.example.colortasks.util.TaskColor;

public class RequestTaskDTO {

    private String description;

    private String taskName;

    private TaskColor color;

    public String getDescription() {
        return description;
    }

    public String getTaskName() {
        return taskName;
    }

    public TaskColor getColor() {
        return color;
    }
}
