package com.example.colortasks;


import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.repository.UserRepository;
import com.example.colortasks.validator.UserValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserValidatorTest {

    private static final String PASSWORD = "Password12345";

    private static final String USERNAME = "username";


    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    public void userLoginValidateFailMustContainExceptionNotEnough() {
        User user = new User();
        user.setUsername(USERNAME.substring(0, 4));

        assertThrows(MustContainException.class, () -> userValidator.userLoginValidate(user.getUsername()));
    }

    @Test
    public void userLoginValidateFailMustContainExceptionTooMuchContained() {
        User user = new User();
        user.setUsername(USERNAME + USERNAME);

        assertThrows(MustContainException.class, () -> userValidator.userLoginValidate(user.getUsername()));
    }

    @Test
    public void userLoginValidateFailAlreadyExistsExceptionLogin() {
        User user = new User();
        user.setUsername(USERNAME);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistsException.class, () -> userValidator.userLoginValidate(user.getUsername()));
    }

    @Test
    public void userPasswordValidateFailMustContainExceptionLength() {
        User user = new User();
        user.setPassword(PASSWORD.substring(1, 6));

        assertThrows(MustContainException.class, () -> userValidator.userPasswordValidate(user.getPassword()));
    }

    @Test
    public void updateUserByIdFailMustContainExceptionDoNotMatch() {
        User user = new User();
        user.setPassword(PASSWORD);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword(PASSWORD);
        dto.setReEnterPassword(PASSWORD + PASSWORD);

        assertThrows(MustContainException.class, () -> userValidator.userNewPasswordDTOValidate(dto, user));
    }

    @Test
    public void userNewPasswordDTOValidateFailMustContainExceptionCurrentPassword() {
        User user = new User();
        user.setPassword(PASSWORD);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD);
        dto.setNewPassword(PASSWORD);
        dto.setReEnterPassword(PASSWORD);

        assertThrows(MustContainException.class, () -> userValidator.userNewPasswordDTOValidate(dto, user));
    }

    @Test
    public void userNewPasswordDTOValidateFailMustContainExceptionOldPassword() {
        User user = new User();
        user.setPassword(PASSWORD);

        UserNewPasswordDTO dto = new UserNewPasswordDTO();
        dto.setOldPassword(PASSWORD + "123");
        dto.setNewPassword(PASSWORD + PASSWORD);
        dto.setReEnterPassword(PASSWORD + PASSWORD);

        when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(MustContainException.class, () -> userValidator.userNewPasswordDTOValidate(dto, user));
    }

}
