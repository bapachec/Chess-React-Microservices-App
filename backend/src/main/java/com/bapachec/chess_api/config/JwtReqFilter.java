package com.bapachec.chess_api.config;

import com.bapachec.chess_api.exceptions.UserNotFoundException;
import com.bapachec.chess_api.user.entity.User;
import com.bapachec.chess_api.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class JwtReqFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;


    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("your_jwt_secret_your_jwt_secret_1234".getBytes());
    Jws<Claims> jws;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                jws = Jwts.parser()
                        .verifyWith(SECRET_KEY)
                        .build()
                        .parseSignedClaims(token);

                String userId = jws.getPayload().get("id").toString();
                Optional<User> user_opt = userRepository.findUserByUser_id(userId);

                if (user_opt.isEmpty()) {
                    throw new UserNotFoundException("Not real id");
                }

                User user = user_opt.get();

                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getId(), null, Collections.emptyList());
                 SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            } catch (UserNotFoundException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid User");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}