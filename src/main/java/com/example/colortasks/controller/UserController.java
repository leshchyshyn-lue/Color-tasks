package com.example.colortasks.controller;

import com.example.colortasks.dto.UserNewPasswordDTO;
import com.example.colortasks.entity.User;
import com.example.colortasks.exception.MustContainException;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sch")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<User> findUser() throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserById());
    }

    @PutMapping("/pass")
    public ResponseEntity<User> updateUser(@RequestBody UserNewPasswordDTO dto) throws MustContainException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserById(dto));
    }
}


