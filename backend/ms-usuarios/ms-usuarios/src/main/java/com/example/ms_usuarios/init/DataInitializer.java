package com.example.ms_usuarios.init;

import com.example.ms_usuarios.model.Role;
import com.example.ms_usuarios.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) { this.roleRepository = roleRepository; }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByNombre("ROLE_USER").isEmpty()) roleRepository.save(new Role("ROLE_USER"));
        if (roleRepository.findByNombre("ROLE_ADMIN").isEmpty()) roleRepository.save(new Role("ROLE_ADMIN"));
    }
}
