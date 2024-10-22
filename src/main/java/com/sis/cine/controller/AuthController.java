package com.sis.cine.controller;

import com.sis.cine.dto.AuthDTOs;
import com.sis.cine.exception.InvalidCredentialsException;
import com.sis.cine.model.Usuario;
import com.sis.cine.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sis.cine.dto.ErrorResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTOs.LoginRequest loginRequest) {
        try {
            String token = userDetailService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new AuthDTOs.AuthResponse(token));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Error de autenticación", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTOs.AuthResponse> register(@RequestBody AuthDTOs.RegisterRequest registerRequest) {
        Usuario usuario = userDetailService.registerUser(
                registerRequest.getNombre(),
                registerRequest.getApellido(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getTlfn()
        );
        String token = userDetailService.loginUser(usuario.getEmail(), registerRequest.getPassword());
        return ResponseEntity.ok(new AuthDTOs.AuthResponse(token));
    }
}