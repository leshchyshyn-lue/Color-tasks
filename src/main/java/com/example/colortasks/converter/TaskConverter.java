package com.example.colortasks.converter;

import com.example.colortasks.dto.RequestTaskDTO;
import com.example.colortasks.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskConverter {

    public Task convertToTask(RequestTaskDTO requestTaskDTO){
        Task task = new Task();
        task.setTaskName(requestTaskDTO.getTaskName());
        task.setDescription(requestTaskDTO.getDescription());
        task.setColor(requestTaskDTO.getColor());
        return task;
    }

}
