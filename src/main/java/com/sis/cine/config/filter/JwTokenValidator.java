package com.sis.cine.config.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sis.cine.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collection;

public class JwTokenValidator extends OncePerRequestFilter{

    private final JwtUtils jwtUtils;

    public JwTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                DecodedJWT decoded = jwtUtils.validateToken(token);
                String username = jwtUtils.getUsernameFromToken(decoded);
                String stringAuthorities = jwtUtils.getClaimFromToken(decoded, "authorities").asString();
                Collection<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            } catch (JWTVerificationException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                System.out.println("Invalid or expired token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
