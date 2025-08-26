package com.example.E_tech.config;

import com.example.E_tech.Entity.User;
import com.example.E_tech.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@Component
public class UserValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String email = request.getHeader("email");
        User user=userService.findByEmailWithoutOptional(email);
        if (user == null || !userService.existsById(user.getId())) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid or missing userId");
            return false;
        }
        return true;
    }
}
