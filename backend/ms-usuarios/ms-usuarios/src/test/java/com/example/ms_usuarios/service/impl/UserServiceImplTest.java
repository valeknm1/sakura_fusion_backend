package com.example.ms_usuarios.service.impl;

import com.example.ms_usuarios.dto.UserRequest;
import com.example.ms_usuarios.dto.UserResponse;
import com.example.ms_usuarios.model.Role;
import com.example.ms_usuarios.model.User;
import com.example.ms_usuarios.repository.RoleRepository;
import com.example.ms_usuarios.repository.UserRepository;
import com.example.ms_usuarios.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Test Suite")
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserServiceImpl userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setNombre("ROLE_USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setNombre("Juan Perez");
        testUser.setEmail("juan@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRol(testRole);
    }

    // ===== REGISTRATION TESTS =====
    @Test
    @DisplayName("Should successfully register a new user")
    void register_success() {
        UserRequest req = new UserRequest();
        req.setNombre("Juan Perez");
        req.setEmail("juan@example.com");
        req.setPassword("secret123");
        req.setRolId(1L);

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse res = userService.register(req);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Juan Perez", res.getNombre());
        assertEquals("juan@example.com", res.getEmail());
        assertEquals("ROLE_USER", res.getRolNombre());
        verify(userRepository, times(1)).existsByEmail(req.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with duplicate email")
    void register_duplicateEmail_throws() {
        UserRequest req = new UserRequest();
        req.setNombre("Juan");
        req.setEmail("juan@example.com");
        req.setPassword("secret123");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(req));
        assertTrue(exception.getMessage().contains("Email"));
        verify(userRepository, times(1)).existsByEmail(req.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should register user without specifying role (default ROLE_USER)")
    void register_withoutRole_success() {
        UserRequest req = new UserRequest();
        req.setNombre("Maria");
        req.setEmail("maria@example.com");
        req.setPassword("secure456");
        req.setRolId(null);

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserResponse res = userService.register(req);

        assertNotNull(res);
        assertEquals(2L, res.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ===== AUTHENTICATION TESTS =====
    @Test
    @DisplayName("Should authenticate user successfully")
    void authenticate_success() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("secret123", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("valid_jwt_token_123");

        String token = userService.authenticate("juan@example.com", "secret123");

        assertEquals("valid_jwt_token_123", token);
        verify(userRepository, times(1)).findByEmail("juan@example.com");
        verify(passwordEncoder, times(1)).matches("secret123", testUser.getPassword());
        verify(jwtUtil, times(1)).generateToken(testUser);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found during authentication")
    void authenticate_userNotFound_throws() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        org.springframework.security.core.userdetails.UsernameNotFoundException exception =
                assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userService.authenticate("nonexistent@example.com", "anypassword"));
        assertTrue(exception.getMessage().contains("Usuario"));
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when password is incorrect")
    void authenticate_incorrectPassword_throws() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.authenticate("juan@example.com", "wrongpassword"));
        assertTrue(exception.getMessage().contains("Credenciales"));
        verify(jwtUtil, never()).generateToken(any());
    }

    // ===== GET BY ID TESTS =====
    @Test
    @DisplayName("Should retrieve user by valid ID")
    void getById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse res = userService.getById(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Juan Perez", res.getNombre());
        assertEquals("juan@example.com", res.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user ID not found")
    void getById_notFound_throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class,
                () -> userService.getById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    // ===== LIST ALL TESTS =====
    @Test
    @DisplayName("Should return list of all users")
    void listAll_success() {
        User user2 = new User();
        user2.setId(2L);
        user2.setNombre("Maria");
        user2.setEmail("maria@example.com");
        user2.setRol(testRole);

        when(userRepository.findAll()).thenReturn(List.of(testUser, user2));

        List<UserResponse> results = userService.listAll();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Juan Perez", results.get(0).getNombre());
        assertEquals("Maria", results.get(1).getNombre());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void listAll_empty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> results = userService.listAll();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    // ===== UPDATE TESTS =====
    @Test
    @DisplayName("Should successfully update user details")
    void update_success() {
        UserRequest req = new UserRequest();
        req.setNombre("Juan Updated");
        req.setEmail("juan.updated@example.com");
        req.setPassword("newpassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse res = userService.update(1L, req);

        assertNotNull(res);
        assertEquals("Juan Updated", res.getNombre());
        assertEquals("juan.updated@example.com", res.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user with partial information")
    void update_duplicateEmail_throws() {
        UserRequest req = new UserRequest();
        req.setNombre("Juan");
        req.setEmail("existing@example.com");
        req.setPassword("password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse res = userService.update(1L, req);

        assertNotNull(res);
        assertEquals("Juan", res.getNombre());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void update_userNotFound_throws() {
        UserRequest req = new UserRequest();
        req.setNombre("Test");
        req.setEmail("test@example.com");
        req.setPassword("password");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class,
                () -> userService.update(999L, req));
    }

    @Test
    @DisplayName("Should update user with partial information")
    void update_partialUpdate() {
        UserRequest req = new UserRequest();
        req.setNombre("Juan New Name");
        req.setEmail(null);  // Not updating email
        req.setPassword(null);  // Not updating password

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse res = userService.update(1L, req);

        assertNotNull(res);
        assertEquals("Juan New Name", res.getNombre());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ===== DELETE TESTS =====
    @Test
    @DisplayName("Should successfully delete user")
    void delete_success() {
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.delete(1L));

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void delete_userNotFound_throws() {
        doNothing().when(userRepository).deleteById(999L);

        assertDoesNotThrow(() -> userService.delete(999L));

        verify(userRepository, times(1)).deleteById(999L);
    }

    // ===== FIND BY EMAIL TESTS =====
    @Test
    @DisplayName("Should find user by email")
    void findByEmail_success() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(testUser));

        User found = userService.findByEmail("juan@example.com");

        assertNotNull(found);
        assertEquals("juan@example.com", found.getEmail());
        verify(userRepository, times(1)).findByEmail("juan@example.com");
    }

    @Test
    @DisplayName("Should return null when email not found")
    void findByEmail_notFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        User found = userService.findByEmail("notfound@example.com");

        assertNull(found);
    }

    // ===== LOAD USER BY USERNAME TESTS =====
    @Test
    @DisplayName("Should load user by username (email)")
    void loadUserByUsername_success() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(testUser));

        var userDetails = userService.loadUserByUsername("juan@example.com");

        assertNotNull(userDetails);
        assertEquals("juan@example.com", userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail("juan@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_notFound_throws() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        org.springframework.security.core.userdetails.UsernameNotFoundException exception =
                assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                        () -> userService.loadUserByUsername("unknown@example.com"));
        assertTrue(exception.getMessage().contains("Usuario"));
    }
}
