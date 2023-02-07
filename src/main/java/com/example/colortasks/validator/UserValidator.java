package com.example.colortasks.validator;


import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class UserValidator {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserValidator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void userLoginValidate(String username) throws AlreadyExistsException, MustContainException {
        int length = username.replaceAll("[^a-zA-Z]", "").length();
        if (length < 5) {
            throw new MustContainException("Login mush contain 5 letters");
        }
        if (length > 12) {
            throw new MustContainException("Login mush contain between 5 and 12 characters");
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new AlreadyExistsException("The login is taken");
        }
    }

    public void userPasswordValidate(String password) throws MustContainException {
        String toLowerCase = password.toLowerCase();
        if (toLowerCase.equals(password) || password.length() < 6) {
            throw new MustContainException("Password must contain 1 capital letter and include 6 characters");
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

}
