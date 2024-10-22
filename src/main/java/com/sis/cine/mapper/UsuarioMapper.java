package com.sis.cine.mapper;


import com.sis.cine.dto.AuthDTOs;
import com.sis.cine.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public AuthDTOs.UsuarioDTO toDTO(Usuario usuario) {
        AuthDTOs.UsuarioDTO dto = new AuthDTOs.UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRolNombre(usuario.getRol().getNombre());
        dto.setTlfn(usuario.getTlfn());
        dto.setDepartamento(usuario.getDepartamento());
        return dto;
    }

    public Usuario toEntity(AuthDTOs.RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setApellido(registerRequest.getApellido());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setTlfn(registerRequest.getTlfn());
        // La contraseña se debe encriptar en el servicio antes de guardar
        return usuario;
    }
}