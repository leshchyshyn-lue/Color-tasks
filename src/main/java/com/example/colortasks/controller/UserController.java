package com.example.colortasks.controller;

import com.example.colortasks.dto.SessionResponseDTO;
import com.example.colortasks.dto.UserDTO;
import com.example.colortasks.dto.UserNewPassForgetDTO;
import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;




@RestController
@RequestMapping("/sch")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<User> findUser() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUser());
    }

    @PostMapping("/login")
    public SessionResponseDTO login(@RequestBody UserDTO user) throws NotFoundException {
        return userService.login(user);
    }

    @PostMapping("/registration")
    public ResponseEntity<User> createNewUser (@RequestBody User user) throws AlreadyExistsException, MustContainException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.createNewUser(user));
    }

    @PutMapping("/pass")
    public ResponseEntity<User> updateUser(@RequestBody UserNewPasswordDTO dto) throws MustContainException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updatePasswordUserById(dto));
    }

    @DeleteMapping("/delete")
    public void deleteUserById(HttpServletRequest request) {
        userService.deleteUserById(request);
    }


    @PostMapping("/forgot")
    public User forgotPassword(@RequestBody String email) throws NotFoundException {
        return userService.sendUserPasswordToEmail(email);
    }

    @PostMapping("/verify")
        public void checkUserCode(@RequestBody String code) throws NotFoundException {
        userService.checkUserCode(code);
    }

    @PutMapping("/forgot/pass")
    public SessionResponseDTO setNewUserPasswordAfterForgotten(@RequestBody UserNewPassForgetDTO dto) throws MustContainException, NotFoundException{
        return userService.setNewUserPasswordAfterForgotten(dto);
    }


}


