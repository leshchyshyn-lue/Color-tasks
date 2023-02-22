package com.example.colortasks;

import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.Task;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.repository.UserRepository;
import com.example.colortasks.service.UserDetailsServiceImpl;
import com.example.colortasks.service.UserService;
import com.example.colortasks.util.TaskColor;
import com.example.colortasks.validator.UserValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    private static final int USER_ID = 1;
    private static final String PASSWORD = "Password12345";
    private static final String USERNAME = "username";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    private User createUser() {
        User user = new User();
        user.setPassword(PASSWORD);
        user.setUsername(USERNAME);
        return user;
    }

    private List<Task> createTasks() {
        Task task = new Task();
        task.setTaskName("name");
        task.setDescription("description");
        task.setColor(TaskColor.RED);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        return tasks;
    }

    @Test
    public void createNewUserSuccess() throws MustContainException, AlreadyExistsException {
        User userBefore = createUser();

        User userAfter = createUser();
        userAfter.setId(USER_ID);

        when(userRepository.save(userBefore)).thenReturn(userAfter);

        User result = userService.createNewUser(userBefore);

        assertEquals(userAfter, result);
        assertEquals(userAfter.getUsername(), result.getUsername());
        assertEquals(userAfter.getPassword(), result.getPassword());
        assertEquals(userAfter.getId(), result.getId());
    }


    @Test
    public void findUserSuccess() {
        User user = createUser();
        user.setId(USER_ID);

        when(userDetailsService.findUserBySession()).thenReturn(user);

        User result = userService.findUser();

        assertEquals(user, result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getId(), result.getId());
    }

    @Test
    public void updateUserByIdSuccess() throws MustContainException {
        User userBefore = createUser();
        userBefore.setId(USER_ID);
        userBefore.setTasks(createTasks());

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword(PASSWORD + PASSWORD);
        dto.setReEnterPassword(PASSWORD + PASSWORD);

        User userAfter = createUser();
        userAfter.setTasks(createTasks());
        userAfter.setId(USER_ID);
        userAfter.setPassword(PASSWORD + PASSWORD);

        when(userDetailsService.findUserBySession()).thenReturn(userBefore);
        when(userRepository.save(userBefore)).thenReturn(userAfter);

        User result = userService.updateUserById(dto);

        assertEquals(result, userAfter);
        assertEquals(result.getTasks(), userAfter.getTasks());
        assertEquals(result.getPassword(), userAfter.getPassword());
        assertEquals(result.getUsername(), userAfter.getUsername());
    }

}
