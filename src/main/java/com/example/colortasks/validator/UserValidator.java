package com.example.colortasks.validator;


import com.example.colortasks.dto.UserNewPassForgetDTO;
import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class UserValidator {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String PHONE_NUMBER_PATTERN = "\\d{3}-\\d{7}";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private Pattern pattern;
    private Matcher matcher;

    @Autowired
    public UserValidator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void userLoginValidate(String username) throws AlreadyExistsException, MustContainException {
        int length = username.replaceAll("[^a-zA-Z]", "").length();
        if (length < 6) {
            throw new MustContainException("Login mush contain 6 letters");
        }
        if (length > 12) {
            throw new MustContainException("Login must contain between 6 and 12 letters");
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new AlreadyExistsException("The login is taken");
        }
    }

    public void userPasswordValidate(String password) throws MustContainException {
        String toLowerCase = password.toLowerCase();
        if (toLowerCase.equals(password) || password.length() < 8) {
            throw new MustContainException("Password must contain 1 capital letter and include 8 characters");
        }
    }

    public void userNewPasswordDTOValidate(UserNewPasswordDTO dto, User user) throws MustContainException {
        userPasswordValidate(dto.getNewPassword());
        if (!dto.getNewPassword().equals(dto.getReEnterPassword())) {
            throw new MustContainException("Passwords do not match");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new MustContainException("Match current password");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new MustContainException("Old password is wrong");
        }
    }

    public void userPhoneNumberValidate(String number) throws MustContainException {
        pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
        matcher = pattern.matcher(number);
        if (!matcher.matches()) {
            throw new MustContainException("Phone Number must be in the form XXX-XXXXXXX");
        }
    }

    public void userEmailValidate(String email) throws MustContainException, AlreadyExistsException {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new MustContainException("Email must be in the form XXXXXX@gmail.com");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AlreadyExistsException("This email is already in use");
        }
    }

    public void userNewPassForgotValidate(UserNewPassForgetDTO dto, User user) throws MustContainException {
        userPasswordValidate(dto.getNewPassword());
        if (!dto.getNewPassword().equals(dto.getReEnterPassword())) {
            throw new MustContainException("Passwords do not match");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new MustContainException("It is your current password");
        }
    }


}
