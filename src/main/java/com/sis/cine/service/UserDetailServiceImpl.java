package com.sis.cine.service;

import com.sis.cine.exception.InvalidCredentialsException;
import com.sis.cine.model.Rol;
import com.sis.cine.model.Usuario;
import com.sis.cine.repository.RolRepository;
import com.sis.cine.repository.UsuarioRepository;
import com.sis.cine.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con email " + email + " no encontrado"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().toUpperCase()));

        return new User(usuario.getEmail(), usuario.getContrasena(), authorities);
    }

    public Authentication authenticate(String email, String password) {
        try {
            UserDetails userDetails = this.loadUserByUsername(email);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Email y/o Contraseña incorrecta");
            }
            return new UsernamePasswordAuthenticationToken(email, userDetails.getPassword(), userDetails.getAuthorities());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Email y/o Contraseña incorrecta");
        }
    }

    public String loginUser(String email, String password) {
        try {
            Authentication authentication = this.authenticate(email, password);
            return jwtUtils.createToken(authentication);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(e.getMessage());
        }
    }

    public Usuario registerUser(String nombre, String apellido, String email, String password, Long tlfn) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setContrasena(passwordEncoder.encode(password));
        usuario.setTlfn(tlfn);

        // Asumiendo que tienes un RolRepository y un rol "CLIENTE" predefinido
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no encontrado"));
        usuario.setRol(rolCliente);

        return usuarioRepository.save(usuario);
    }
}