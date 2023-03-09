package com.example.colortasks;

import com.example.colortasks.dto.SessionResponseDTO;
import com.example.colortasks.dto.UserDTO;
import com.example.colortasks.dto.UserNewPassForgetDTO;
import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.Task;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.repository.TaskRepository;
import com.example.colortasks.repository.UserRepository;
import com.example.colortasks.service.MailSenderService;
import com.example.colortasks.service.RedisService;
import com.example.colortasks.service.UserDetailsServiceImpl;
import com.example.colortasks.service.UserService;
import com.example.colortasks.session.SessionRegistry;
import com.example.colortasks.util.NumberGenerator;
import com.example.colortasks.util.TaskColor;
import com.example.colortasks.validator.UserValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    private static final int USER_ID = 1;
    private static final String PASSWORD = "Password12345";
    private static final String USERNAME = "username";

    private static final String SESSION_ID = "12345";

    private static final String EMAIL = "kroasavbbqaz@gmail.com";

    private static final String CODE = "654321";

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

    @Mock
    private AuthenticationManager manager;

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private RedisService redisService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NumberGenerator numberGenerator;

    @Mock
    private MailSenderService mailSenderService;

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
    public void loginSuccess() throws NotFoundException {
        UserDTO dto = new UserDTO();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);

        Authentication authentication = mock(Authentication.class);

        when(manager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()))).thenReturn(authentication);
        when(sessionRegistry.registerSession(dto.getUsername())).thenReturn(SESSION_ID);

        SessionResponseDTO response = new SessionResponseDTO();
        response.setSessionId(SESSION_ID);

        SessionResponseDTO result = userService.login(dto);

        assertEquals(result.getSessionId(), response.getSessionId());
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

        User result = userService.updatePasswordUserById(dto);

        assertEquals(result, userAfter);
        assertEquals(result.getTasks(), userAfter.getTasks());
        assertEquals(result.getPassword(), userAfter.getPassword());
        assertEquals(result.getUsername(), userAfter.getUsername());
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
    public void deleteUserByIdSuccess(){
        User user = createUser();
        user.setId(USER_ID);

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(SESSION_ID);
        doNothing().when(redisService).deleteKeyBySessionId(SESSION_ID);
        when(userDetailsService.findUserBySession()).thenReturn(user);
        doNothing().when(taskRepository).deleteAllTasksByUserId(user.getId());
        doNothing().when(userRepository).deleteById(user.getId());

        userService.deleteUserById(request);
    }

    @Test
    public void sendUserPasswordToEmailSuccess() throws NotFoundException {
        User userBefore = createUser();
        userBefore.setEmail(EMAIL);

        User userAfter = createUser();
        userAfter.setEmail(EMAIL);
        userAfter.setCodeFromEmail(CODE);

        when(userRepository.findByEmail(userBefore.getEmail())).thenReturn(Optional.of(userBefore));
        when(numberGenerator.generateRandomNumber()).thenReturn(CODE);

        doNothing().when(mailSenderService).sendMailMessage(userBefore.getEmail(), CODE, userBefore.getUsername(), CODE);
        when(userRepository.save(userBefore)).thenReturn(userAfter);

        User result = userService.sendUserPasswordToEmail(userBefore.getEmail());

        assertEquals(result, userAfter);
        assertEquals(result.getCodeFromEmail(), userAfter.getCodeFromEmail());
    }

    @Test
    public void checkUserCodeSuccess() throws NotFoundException {
        User user = createUser();
        user.setCodeFromEmail(CODE);

        when(userRepository.findByCodeFromEmail(user.getCodeFromEmail())).thenReturn(Optional.of(user));
        userService.checkUserCode(user.getCodeFromEmail());
    }

    @Test
    public void checkUserCodeFailMustContainException(){
        User user = createUser();
        user.setCodeFromEmail(CODE + CODE);

        assertThrows(NotFoundException.class, () -> userService.checkUserCode(CODE));
    }

    @Test
    public void setNewUserPasswordAfterForgottenSuccess() throws NotFoundException, MustContainException {
        User userBefore = createUser();
        userBefore.setEmail(EMAIL);

        UserNewPassForgetDTO dto = new UserNewPassForgetDTO();
        dto.setEmail(EMAIL);
        dto.setNewPassword(PASSWORD);
        dto.setReEnterPassword(PASSWORD);

        User userAfter = createUser();
        userAfter.setEmail(EMAIL);
        userAfter.setPassword(dto.getNewPassword());

        SessionResponseDTO sessionResponseDTO = new SessionResponseDTO();
        sessionResponseDTO.setSessionId(SESSION_ID);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(userBefore));
        when(userRepository.save(userBefore)).thenReturn(userAfter);
        when(sessionRegistry.registerSession(userBefore.getUsername())).thenReturn(SESSION_ID);

        SessionResponseDTO result = userService.setNewUserPasswordAfterForgotten(dto);

        assertEquals(result.getSessionId(), sessionResponseDTO.getSessionId());
        assertEquals(dto.getNewPassword(), userAfter.getPassword());
    }








}
