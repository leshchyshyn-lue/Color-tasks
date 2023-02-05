package com.example.colortasks.service;

import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    public User findUserById() throws NotFoundException {
        return userRepository.findById(userDetailsServiceImpl.findUserBySession().getId())
                .orElseThrow(() -> new NotFoundException("Not Found"));
    }

    public User updateUserById(UserNewPasswordDTO dto) throws MustContainException {
        User user = userDetailsServiceImpl.findUserBySession();
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new MustContainException("Old password is wrong");
        }
        if (!dto.getNewPassword().equals(dto.getReEnterPassword())) {
            throw new MustContainException("Passwords do not match");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new MustContainException("Match current password");
        }
        verifyData(dto.getNewPassword());
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return userRepository.save(user);
    }


    public User createNewUser(User user) throws AlreadyExistsException, MustContainException {
        Optional<User> byUserLogin = userRepository.findByUsername(user.getUsername());
        if (byUserLogin.isPresent()) {
            throw new AlreadyExistsException("The login is taken");
        }
        int length = user.getUsername().replaceAll("[^a-zA-Z]", "").length();
        if (length < 5) {
            throw new MustContainException("Login mush contain 5 letters");
        }
        if(length > 12){
            throw new MustContainException("Login mush contain between 5 and 12 characters");
        }
        verifyData(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private void verifyData(String password) throws MustContainException {
        String toLowerCase = password.toLowerCase();
        if (toLowerCase.equals(password) || password.length() < 6) {
            throw new MustContainException("Password must contain 1 capital letter and include 6 characters");
        }
    }

}
