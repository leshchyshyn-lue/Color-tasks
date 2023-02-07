package com.example.colortasks.service;

import com.example.colortasks.entity.Task;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.repository.TaskRepository;
import com.example.colortasks.util.TaskColor;
import com.example.colortasks.validator.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskValidator taskValidator;

    private final UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskValidator taskValidator, UserService userService) {
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
        this.userService = userService;
    }

    public Task findTaskById(int id) throws NotFoundException {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("This task wasn't found"));
    }

    public Task createNewTask(Task task) throws MustContainException, AlreadyExistsException {
        taskValidator.taskFieldValidate(task);
        String dateTime = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm")
                .format(LocalDateTime.now());
        task.setCreatedAt(dateTime);
        task.setUser(userService.findUser());
        return taskRepository.save(task);
    }

    public Task updateTask(Task newTask, int id) throws NotFoundException, MustContainException, AlreadyExistsException {
        taskValidator.taskFieldValidate(newTask);
        Task task = findTaskById(id);
        task.setTaskName(newTask.getTaskName());
        task.setDescription(newTask.getDescription());
        task.setColor(newTask.getColor());
        return taskRepository.save(task);
    }

    public void deleteTaskById(int id) throws NotFoundException {
        findTaskById(id);
        taskRepository.deleteById(id);
    }

    public void deleteAllTasksByUserId() {
        taskRepository.deleteAllTasksByUserId(userService.findUser().getId());
    }

    public void deleteAllTasksByColor(TaskColor color) {
        taskRepository.deleteTasksByColor(String.valueOf(color), userService.findUser().getId());
    }

    public List<Task> findAllTasksByColor(TaskColor color) {
        return taskRepository.findAllTasksByColor(userService.findUser().getId(), String.valueOf(color));
    }
}
