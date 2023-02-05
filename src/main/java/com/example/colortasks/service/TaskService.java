package com.example.colortasks.service;

import com.example.colortasks.entity.Task;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.repository.TaskRepository;
import com.example.colortasks.util.TaskColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.taskRepository = taskRepository;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    public Task findTaskById(int id) throws NotFoundException {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("This task wasn't found"));
    }

    public Task createNewTask(Task task) throws MustContainException, AlreadyExistsException {
        verifyData(task);
        String timeNow = String.valueOf(LocalDateTime.now());
        String builder = timeNow.substring(0, 10) + " " +
                timeNow.substring(11, 16);
        task.setCreatedAt(builder);
        task.setUser(userDetailsServiceImpl.findUserBySession());
        return taskRepository.save(task);
    }

    public Task updateTask(Task newTask, int id) throws NotFoundException, MustContainException, AlreadyExistsException {
        verifyData(newTask);
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
        taskRepository.deleteAllTasksByUserId(userDetailsServiceImpl.findUserBySession().getId());
    }

    public void deleteAllTasksByColor(TaskColor color) {
        taskRepository.deleteTasksByColor(String.valueOf(color), userDetailsServiceImpl.findUserBySession().getId());
    }

    public List<Task> findAllTasksByColor(TaskColor color) {
        return taskRepository.findAllTasksByColor(userDetailsServiceImpl.findUserBySession().getId(), String.valueOf(color));
    }

    private void verifyData(Task task) throws MustContainException, AlreadyExistsException {
        Optional<Task> byName = taskRepository.findByTaskNameAndUser(task.getTaskName(), userDetailsServiceImpl.findUserBySession());
        if (byName.isPresent() && byName.get().getId() != task.getId()) {
                throw new AlreadyExistsException("Task with this name already exists");
        }
        if (task.getTaskName() == null || task.getTaskName().equals("")) {
            throw new MustContainException("Enter a task name");
        }
        if (task.getDescription() == null || task.getDescription().length() <= 5) {
            throw new MustContainException("The description is too short");
        }
        if (task.getColor() == null) {
            throw new MustContainException("Choose a color");
        }
    }


}
