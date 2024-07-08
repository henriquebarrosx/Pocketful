package com.pocketful.config;

import com.pocketful.entity.Account;
import com.pocketful.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@AllArgsConstructor
@Component
public class SecurityFilterConfig extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (Objects.nonNull(token)) {
            Account account = tokenService.decodeToken(token);

            if (tokenService.validateToken(account.getEmail(), token)) {
                var authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("authorization");
        if (Objects.isNull(authHeader)) return null;
        return authHeader.replace("Bearer ", "");
    }
}
