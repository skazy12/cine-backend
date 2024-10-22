package com.sis.cine.dto;

import lombok.Data;

public class AuthDTOs {
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String nombre;
        private String apellido;
        private String email;
        private String password;
        private Long tlfn;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String tipo = "Bearer";

        public AuthResponse(String token) {
            this.token = token;
        }
    }

    @Data
    public static class UsuarioDTO {
        private Long id;
        private String nombre;
        private String apellido;
        private String email;
        private Long tlfn;
        private String rolNombre;
        private String departamento;
    }

}
