package com.example.colortasks;


import com.example.colortasks.entity.Task;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.repository.TaskRepository;
import com.example.colortasks.service.TaskService;
import com.example.colortasks.service.UserDetailsServiceImpl;
import com.example.colortasks.util.TaskColor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    private static final int USER_ID = 1;
    private static final int TASK_ID = 2;
    private static final String TASK_NAME = "name";
    private static final String TASK_DESCRIPTION = "description";

    private static final String INVALID_TASK_DESCRIPTION = "short";

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserDetailsServiceImpl userDetailsService;


    @Test
    public void findTaskByIdSuccess() throws NotFoundException {
        Task task = new Task();
        task.setId(TASK_ID);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        Task result = taskService.findTaskById(task.getId());

        assertEquals(task, result);
    }

    @Test
    public void findTaskByIdFailNotFoundException() {
        Task task = new Task();

        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taskService.findTaskById(task.getId()));
    }

    @Test
    public void createNewTaskSuccess() throws MustContainException, AlreadyExistsException {
        User user = new User();
        user.setId(USER_ID);

        Task task = new Task();
        task.setTaskName(TASK_NAME);
        task.setUser(user);
        task.setDescription(TASK_DESCRIPTION);
        task.setColor(TaskColor.RED);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        user.setTasks(tasks);

        Task afterSave = new Task();
        afterSave.setTaskName(TASK_NAME);
        afterSave.setUser(user);
        afterSave.setDescription(TASK_DESCRIPTION);
        afterSave.setColor(TaskColor.RED);
        afterSave.setId(TASK_ID);

        when(taskRepository.findByTaskNameAndUser(task.getTaskName(), user)).thenReturn(Optional.empty());
        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(taskRepository.save(task)).thenReturn(afterSave);

        Task result = taskService.createNewTask(task);

        assertEquals(afterSave, result);
        assertEquals(afterSave.getTaskName(), result.getTaskName());
        assertEquals(afterSave.getDescription(), result.getDescription());
        assertEquals(afterSave.getUser(), result.getUser());
        assertEquals(afterSave.getColor(), result.getColor());
    }

    @Test
    public void createNewTaskFailAlreadyExistsException() {
        User user = new User();
        user.setId(USER_ID);

        Task task = new Task();
        task.setTaskName(TASK_NAME);
        task.setId(TASK_ID);

        Task exists = new Task();
        exists.setTaskName(TASK_NAME);
        exists.setId(255);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(taskRepository.findByTaskNameAndUser(task.getTaskName(), user)).thenReturn(Optional.of(exists));

        assertThrows(AlreadyExistsException.class, () -> taskService.createNewTask(task));
    }

    @Test
    public void createNewTaskFailNameMustContainException() {
        User user = new User();
        Task task = new Task();
        task.setTaskName(null);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(taskRepository.findByTaskNameAndUser(task.getTaskName(), user)).thenReturn(Optional.empty());
        assertThrows(MustContainException.class, () -> taskService.createNewTask(task));
    }

    @Test
    public void createNewTaskFailDescriptionMustContainException() {
        User user = new User();
        Task task = new Task();
        task.setTaskName(TASK_NAME);
        task.setDescription(INVALID_TASK_DESCRIPTION);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(taskRepository.findByTaskNameAndUser(task.getTaskName(), user)).thenReturn(Optional.empty());
        assertThrows(MustContainException.class, () -> taskService.createNewTask(task));
    }

    @Test
    public void createNewTaskFailColorMustContainException() {
        User user = new User();
        Task task = new Task();
        task.setTaskName(TASK_NAME);
        task.setDescription(TASK_DESCRIPTION);


        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(taskRepository.findByTaskNameAndUser(task.getTaskName(), user)).thenReturn(Optional.empty());
        assertThrows(MustContainException.class, () -> taskService.createNewTask(task));
    }

    @Test
    public void updateTaskSuccess() throws MustContainException, AlreadyExistsException, NotFoundException {
        User user = new User();
        user.setId(USER_ID);

        Task task = new Task();
        task.setId(TASK_ID);
        task.setTaskName(TASK_NAME);
        task.setUser(user);
        task.setDescription(TASK_DESCRIPTION);
        task.setColor(TaskColor.RED);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        user.setTasks(tasks);

        Task afterUpdate = new Task();
        afterUpdate.setId(TASK_ID);
        afterUpdate.setTaskName(TASK_NAME + TASK_NAME);
        afterUpdate.setUser(user);
        afterUpdate.setDescription(TASK_DESCRIPTION + TASK_DESCRIPTION);
        afterUpdate.setColor(TaskColor.YELLOW);

        tasks.clear();
        tasks.add(afterUpdate);
        user.setTasks(tasks);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(afterUpdate);

        Task result = taskService.updateTask(task, task.getId());

        assertEquals(afterUpdate, result);
        assertEquals(afterUpdate.getTaskName(), result.getTaskName());
        assertEquals(afterUpdate.getDescription(), result.getDescription());
        assertEquals(afterUpdate.getUser(), result.getUser());
        assertEquals(afterUpdate.getColor(), result.getColor());
    }

    @Test
    public void deleteTaskByIdSuccess() {
        Task task = new Task();
        task.setId(TASK_ID);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(task.getId());

        assertDoesNotThrow(() -> taskService.deleteTaskById(task.getId()));
    }

    @Test
    public void deleteAllTasksByUserIdSuccess() {
        User user = new User();
        user.setId(USER_ID);

        when(userDetailsService.findUserBySession()).thenReturn(user);

        doNothing().when(taskRepository).deleteAllTasksByUserId(user.getId());

        assertDoesNotThrow(() -> taskService.deleteAllTasksByUserId());
    }

    @Test
    public void deleteAllTasksByColorSuccess() {
        User user = new User();
        user.setId(USER_ID);

        Task task = new Task();
        task.setColor(TaskColor.RED);
        task.setUser(user);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        user.setTasks(tasks);

        doNothing().when(taskRepository).deleteTasksByColor(String.valueOf(task.getColor()), user.getId());
        when(userDetailsService.findUserBySession()).thenReturn(user);

        assertDoesNotThrow(() -> taskService.deleteAllTasksByColor(task.getColor()));
    }

    @Test
    public void findAllTasksByColor() {
        Task task = new Task();
        task.setColor(TaskColor.RED);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        User user = new User();
        user.setTasks(tasks);

        when(taskRepository.findAllTasksByColor(user.getId(), String.valueOf(task.getColor()))).thenReturn(user.getTasks());
        when(userDetailsService.findUserBySession()).thenReturn(user);

        List<Task> result = taskService.findAllTasksByColor(task.getColor());

        assertEquals(user.getTasks(), result);
    }
}
