package com.example.colortasks.validator;


import com.example.colortasks.entity.Task;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.repository.TaskRepository;
import com.example.colortasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskValidator {


    private final TaskRepository taskRepository;
    private final UserService userService;

    @Autowired
    public TaskValidator(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }


    public void taskFieldValidate(Task task) throws AlreadyExistsException, MustContainException {
        if (task.getTaskName() == null || task.getTaskName().equals("")) {
            throw new MustContainException("Enter a task name");
        }
        if (task.getDescription() == null || task.getDescription().length() <= 5) {
            throw new MustContainException("The description is too short");
        }
        if (task.getColor() == null) {
            throw new MustContainException("Choose a color");
        }
        Optional<Task> byName = taskRepository.findByTaskNameAndUser(task.getTaskName(),userService.findUser());
        if (byName.isPresent() && byName.get().getId() != task.getId()) {
            throw new AlreadyExistsException("Task with this name already exists");
        }
    }

}
