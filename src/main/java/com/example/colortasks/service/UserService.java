package com.example.colortasks.service;

import com.example.colortasks.dto.SessionResponseDTO;
import com.example.colortasks.dto.UserDTO;
import com.example.colortasks.dto.UserNewPassForgetDTO;
import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.repository.TaskRepository;
import com.example.colortasks.repository.UserRepository;
import com.example.colortasks.session.SessionRegistry;
import com.example.colortasks.util.NumberGenerator;
import com.example.colortasks.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserValidator userValidator;
    private final TaskRepository taskRepository;
    private final RedisService redisService;
    private final MailSenderService mailSenderService;
    private final NumberGenerator numberGenerator;
    private final SessionRegistry sessionRegistry;
    private final AuthenticationManager manager;



    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsServiceImpl userDetailsServiceImpl,
                       UserValidator userValidator,
                       TaskRepository taskRepository, RedisService redisService,
                       MailSenderService mailSenderService,
                       NumberGenerator numberGenerator,
                       SessionRegistry sessionRegistry, AuthenticationManager manager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.userValidator = userValidator;
        this.taskRepository = taskRepository;
        this.redisService = redisService;
        this.mailSenderService = mailSenderService;
        this.numberGenerator = numberGenerator;
        this.sessionRegistry = sessionRegistry;
        this.manager = manager;
    }

    public User findUser() {
        return userDetailsServiceImpl.findUserBySession();
    }

    public SessionResponseDTO login(UserDTO user) throws NotFoundException {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        return registerSessionByUsername(user.getUsername());
    }

    public User updatePasswordUserById(UserNewPasswordDTO dto) throws MustContainException {
        User user = findUser();
        userValidator.userNewPasswordDTOValidate(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return userRepository.save(user);
    }

    public User createNewUser(User user) throws AlreadyExistsException, MustContainException {
        userValidator.userLoginValidate(user.getUsername());
        userValidator.userPasswordValidate(user.getPassword());
        userValidator.userPhoneNumberValidate(user.getPhoneNumber());
        userValidator.userEmailValidate(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUserById(HttpServletRequest request) {
        final String sessionId = request.getHeader(HttpHeaders.AUTHORIZATION);
        redisService.deleteKeyBySessionId(sessionId);
        int userId = findUser().getId();
        taskRepository.deleteAllTasksByUserId(userId);
        userRepository.deleteById(userId);
    }

    public User sendUserPasswordToEmail(String email) throws NotFoundException {
        User user = findUserByEmail(email);
        String code = numberGenerator.generateRandomNumber();
        mailSenderService.sendMailMessage(user.getEmail(), code, user.getUsername(), code);
        user.setCodeFromEmail(code);
        return userRepository.save(user);
    }

    public void checkUserCode(String code) throws NotFoundException {
        userRepository.findByCodeFromEmail(code.replace(" ", "")).
                orElseThrow(() -> new NotFoundException("Invalid code"));
    }

    public SessionResponseDTO setNewUserPasswordAfterForgotten(UserNewPassForgetDTO dto) throws NotFoundException, MustContainException{
        User user = findUserByEmail(dto.getEmail());
        userValidator.userNewPassForgotValidate(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return registerSessionByUsername(user.getUsername());
    }

    public User findUserByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email).
                orElseThrow(() -> new NotFoundException("The user with this email is not registered"));
    }

    public SessionResponseDTO registerSessionByUsername(String username) throws NotFoundException {
        final String sessionId = sessionRegistry.registerSession(username);
        SessionResponseDTO response = new SessionResponseDTO();
        response.setSessionId(sessionId);
        return response;
    }




}



