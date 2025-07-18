package com.api.apigateway.jwt.service;

import com.api.apigateway.jwt.dto.*;
import com.api.apigateway.jwt.model.Usuario;
import com.api.apigateway.jwt.repository.UsuarioRepository;
import com.api.apigateway.jwt.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getNombreUsuario(), request.getContrasena()));

        Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow();

        if (!"activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new BadCredentialsException("Usuario inactivo");
        }

        String token = jwtUtil.generateToken(usuario.getNombreUsuario(), usuario.getRol());
        return new AuthResponse(token, usuario.getNombreUsuario(), usuario.getRol());
    }
}