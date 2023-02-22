package com.example.colortasks.service;

import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.repository.UserRepository;
import com.example.colortasks.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserValidator userValidator;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsServiceImpl, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.userValidator = userValidator;
    }

    public User findUser() {
        return userDetailsServiceImpl.findUserBySession();
    }

    public User updateUserById(UserNewPasswordDTO dto) throws MustContainException {
        User user = userDetailsServiceImpl.findUserBySession();
        userValidator.userNewPasswordDTOValidate(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return userRepository.save(user);
    }

    public User createNewUser(User user) throws AlreadyExistsException, MustContainException {
        userValidator.userLoginValidate(user.getUsername());
        userValidator.userPasswordValidate(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


}
