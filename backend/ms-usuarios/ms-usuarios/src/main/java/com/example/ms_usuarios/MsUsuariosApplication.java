package com.example.ms_usuarios;

import com.example.ms_usuarios.model.Role;
import com.example.ms_usuarios.repository.RoleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MsUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUsuariosApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeRoles(RoleRepository roleRepository) {
		return args -> {
			// Crear roles si no existen
			if (roleRepository.findByNombre("ROLE_USER").isEmpty()) {
				Role userRole = new Role();
				userRole.setNombre("ROLE_USER");
				roleRepository.save(userRole);
			}
			
			if (roleRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
				Role adminRole = new Role();
				adminRole.setNombre("ROLE_ADMIN");
				roleRepository.save(adminRole);
			}
			
			if (roleRepository.findByNombre("ROLE_MESERO").isEmpty()) {
				Role meseroRole = new Role();
				meseroRole.setNombre("ROLE_MESERO");
				roleRepository.save(meseroRole);
			}
		};
	}

}
