package com.example.colortasks.controller;


import com.example.colortasks.entity.User;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.AlreadyExistsException;
import com.example.colortasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sch")
public class RegistrationController {
    private final UserService userService;


    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<User> createNewUser (@RequestBody User user) throws AlreadyExistsException, MustContainException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.createNewUser(user));
    }
}
