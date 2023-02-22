package com.example.colortasks;

import com.example.colortasks.entity.Task;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.repository.TaskRepository;
import com.example.colortasks.service.UserService;
import com.example.colortasks.util.TaskColor;
import com.example.colortasks.validator.TaskValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskValidatorTest {

    private static final int USER_ID = 1;
    private static final int TASK_ID = 2;
    private static final String TASK_NAME = "name";
    private static final String TASK_DESCRIPTION = "description";

    private static final String INVALID_TASK_DESCRIPTION = "short";

    @InjectMocks
    private TaskValidator taskValidator;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Test
    public void taskFieldValidateFailMustContainExceptionEnterName() {
        Task task = new Task();

        assertThrows(MustContainException.class, () -> taskValidator.taskFieldValidate(task));
    }

    @Test
    public void taskFieldValidateFailMustContainExceptionShortDescription() {

        Task task = new Task();
        task.setTaskName(TASK_NAME);
        task.setDescription(INVALID_TASK_DESCRIPTION);

        assertThrows(MustContainException.class, () -> taskValidator.taskFieldValidate(task));
    }

    @Test
    public void taskFieldValidateFailMustContainExceptionChooseColor() {
        Task task = new Task();
        task.setTaskName(TASK_NAME);
        task.setDescription(TASK_DESCRIPTION);

        assertThrows(MustContainException.class, () -> taskValidator.taskFieldValidate(task));
    }

    @Test
    public void taskFieldValidateFailAlreadyExistsException() {
        User user = new User();
        user.setId(USER_ID);

        Task task = new Task();
        task.setTaskName(TASK_NAME);
        task.setId(TASK_ID);
        task.setDescription(TASK_DESCRIPTION);
        task.setColor(TaskColor.RED);

        Task exists = new Task();
        exists.setTaskName(TASK_NAME);
        exists.setId(255);

        when(userService.findUser()).thenReturn(user);
        when(taskRepository.findByTaskNameAndUser(task.getTaskName(), user)).thenReturn(Optional.of(exists));

        assertThrows(AlreadyExistsException.class, () -> taskValidator.taskFieldValidate(task));
    }

}
