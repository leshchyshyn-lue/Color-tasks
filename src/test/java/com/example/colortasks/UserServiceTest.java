package com.example.colortasks;

import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.Task;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.repository.UserRepository;
import com.example.colortasks.service.UserDetailsServiceImpl;
import com.example.colortasks.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final int USER_ID = 1;

    private static final String PASSWORD = "Password12345";
    private static final String INVALID_PASSWORD = "pass";
    private static final String USERNAME = "username";
    private static final String INVALID_USERNAME = "usernameusername";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    public void findUserByIdSuccess() throws NotFoundException {
        User user = new User();
        user.setId(USER_ID);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.findUserById();

        assertEquals(user, result);
    }

    @Test
    public void findUserByIdFail() {
        User user = new User();
        user.setId(USER_ID);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById());
    }

    @Test
    public void updateUserByIdSuccess() throws MustContainException {
        User userBefore = new User();
        userBefore.setId(USER_ID);
        userBefore.setPassword(PASSWORD);
        userBefore.setUsername(USERNAME);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword(PASSWORD + PASSWORD);
        dto.setReEnterPassword(PASSWORD + PASSWORD);

        Task task = new Task();
        task.setUser(userBefore);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        userBefore.setTasks(tasks);

        User userAfter = new User();
        userAfter.setId(USER_ID);
        userAfter.setTasks(tasks);
        userAfter.setUsername(USERNAME);
        userAfter.setPassword(PASSWORD + PASSWORD);

        when(userDetailsService.findUserBySession()).thenReturn(userBefore);
        when(passwordEncoder.matches(dto.getOldPassword(), userBefore.getPassword())).thenReturn(true);
        when(userRepository.save(userBefore)).thenReturn(userAfter);

        User result = userService.updateUserById(dto);

        assertEquals(result, userAfter);
        assertEquals(result.getTasks(), userAfter.getTasks());
        assertEquals(result.getPassword(), userAfter.getPassword());
        assertEquals(result.getUsername(), userAfter.getUsername());
    }

    @Test
    public void updateUserByIdFailOldPasswordMustContainException() {
        User user = new User();
        user.setPassword(PASSWORD);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD + PASSWORD);

        when(userDetailsService.findUserBySession()).thenReturn(user);

        assertThrows(MustContainException.class, () -> userService.updateUserById(dto));
    }

    @Test
    public void updateUserByIdFailDoNotMatchMustContainException() {
        User user = new User();
        user.setPassword(PASSWORD);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword(PASSWORD);
        dto.setReEnterPassword(PASSWORD + PASSWORD);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(MustContainException.class, () -> userService.updateUserById(dto));
    }

    @Test
    public void updateUserByIdFailCurrentPasswordMustContainException() {
        User user = new User();
        user.setPassword(PASSWORD);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword(PASSWORD);
        dto.setReEnterPassword(PASSWORD);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(MustContainException.class, () -> userService.updateUserById(dto));
    }

    @Test
    public void updateUserByIdFailMustContainException() {
        User user = new User();
        user.setPassword(PASSWORD);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword(INVALID_PASSWORD);
        dto.setReEnterPassword(INVALID_PASSWORD);

        when(userDetailsService.findUserBySession()).thenReturn(user);
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(MustContainException.class, () -> userService.updateUserById(dto));
    }

    @Test
    public void createNewUserSuccess() throws MustContainException, AlreadyExistsException {
        User userBefore = new User();
        userBefore.setUsername(USERNAME);
        userBefore.setPassword(PASSWORD);

        Task task = new Task();
        task.setUser(userBefore);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        userBefore.setTasks(tasks);

        User userAfter = new User();
        userAfter.setTasks(tasks);
        userAfter.setPassword(PASSWORD);
        userAfter.setUsername(USERNAME);
        userAfter.setId(USER_ID);

        when(userRepository.findByUsername(userBefore.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(userBefore)).thenReturn(userAfter);

        User result = userService.createNewUser(userBefore);

        assertEquals(userAfter, result);
        assertEquals(userAfter.getUsername(), result.getUsername());
        assertEquals(userAfter.getPassword(), result.getPassword());
        assertEquals(userAfter.getTasks(), result.getTasks());
        assertEquals(userAfter.getId(), result.getId());
    }

    @Test
    public void createNewUserFailLoginAlreadyExistsException() {
        User user = new User();
        user.setUsername(USERNAME);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistsException.class, () -> userService.createNewUser(user));
    }

    @Test
    public void createNewUserFailContainedMoreMustContainException() {
        User user = new User();
        user.setUsername(INVALID_USERNAME.substring(0, 4));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(MustContainException.class, () -> userService.createNewUser(user));
    }

    @Test
    public void createNewUserFailContainedLessMustContainException() {
        User user = new User();
        user.setUsername(INVALID_USERNAME);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(MustContainException.class, () -> userService.createNewUser(user));
    }
}
