package com.example.ms_usuarios.service;

import com.example.ms_usuarios.dto.UserRequest;
import com.example.ms_usuarios.dto.UserResponse;
import com.example.ms_usuarios.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponse register(UserRequest request);
    String authenticate(String email, String password);
    UserResponse getById(Long id);
    List<UserResponse> listAll();
    UserResponse update(Long id, UserRequest request);
    void delete(Long id);
    User findByEmail(String email);
}
