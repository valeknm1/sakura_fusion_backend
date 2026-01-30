package com.example.ms_usuarios.service.impl;

import com.example.ms_usuarios.dto.UserRequest;
import com.example.ms_usuarios.dto.UserResponse;
import com.example.ms_usuarios.model.Role;
import com.example.ms_usuarios.model.User;
import com.example.ms_usuarios.repository.RoleRepository;
import com.example.ms_usuarios.repository.UserRepository;
import com.example.ms_usuarios.service.UserService;
import com.example.ms_usuarios.security.JwtUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponse register(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User u = new User();
        u.setNombre(request.getNombre());
        u.setEmail(request.getEmail());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        Role r = null;
        if (request.getRolId() != null) r = roleRepository.findById(request.getRolId()).orElse(null);
        u.setRol(r);
        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Override
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return jwtUtil.generateToken(user);
    }

    @Override
    public UserResponse getById(Long id) {
        return userRepository.findById(id).map(this::toDto).orElseThrow();
    }

    @Override
    public List<UserResponse> listAll() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User u = userRepository.findById(id).orElseThrow();
        u.setNombre(request.getNombre());
        u.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRolId() != null) {
            roleRepository.findById(request.getRolId()).ifPresent(u::setRol);
        }
        return toDto(userRepository.save(u));
    }

    @Override
    public void delete(Long id) { userRepository.deleteById(id); }

    @Override
    public User findByEmail(String email) { return userRepository.findByEmail(email).orElse(null); }

    private UserResponse toDto(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setNombre(u.getNombre());
        r.setEmail(u.getEmail());
        r.setRolNombre(u.getRol() != null ? u.getRol().getNombre() : null);
        return r;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(u.getRol() != null ? u.getRol().getNombre() : "ROLE_USER");
        return new org.springframework.security.core.userdetails.User(u.getEmail(), u.getPassword(), Collections.singleton(auth));
    }
}
