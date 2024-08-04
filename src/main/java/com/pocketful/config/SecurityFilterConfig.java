package com.pocketful.config;

import com.pocketful.entity.Account;
import com.pocketful.service.AccountService;
import com.pocketful.service.SessionManagerService;
import com.pocketful.util.JsonWebToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SecurityFilterConfig extends OncePerRequestFilter {
    private final AccountService accountService;
    private final SessionManagerService sessionManagerService;

    public static final String AUTHORIZATION = "authorization";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        Optional<String> token = this.getTokenFromHeaders(request);

        if (token.isPresent()) {
            String sessionEmail = JsonWebToken.decode(token.get());

            if (sessionManagerService.validate(sessionEmail, token.get())) {
                Account account = accountService.findByEmail(sessionEmail);
                var authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            else {
                logger.error("Invalid provided access token");
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromHeaders(HttpServletRequest request) {
        var authHeader = request.getHeader(AUTHORIZATION);
        if (Objects.isNull(authHeader)) return Optional.empty();
        return Optional.of(JsonWebToken.sanitize(authHeader));
    }
}
