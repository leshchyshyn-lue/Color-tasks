package com.example.colortasks.controller;

import com.example.colortasks.dto.ResponseDTO;
import com.example.colortasks.dto.UserDTO;
import com.example.colortasks.exception.NotFoundException;
import com.example.colortasks.session.SessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sch")
public class AuthController {

    private final AuthenticationManager manager;
    private final SessionRegistry sessionRegistry;

    @Autowired
    public AuthController(AuthenticationManager manager, SessionRegistry sessionRegistry) {
        this.manager = manager;
        this.sessionRegistry = sessionRegistry;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody UserDTO user) throws NotFoundException {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        final String sessionId = sessionRegistry.registerSession(user.getUsername());
        ResponseDTO response = new ResponseDTO();
        response.setSessionId(sessionId);
        return ResponseEntity.ok(response);
    }

}
