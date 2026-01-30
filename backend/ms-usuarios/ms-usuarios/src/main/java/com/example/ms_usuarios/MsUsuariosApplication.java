package com.example.ms_usuarios;

import com.example.ms_usuarios.model.Role;
import com.example.ms_usuarios.model.User;
import com.example.ms_usuarios.repository.RoleRepository;
import com.example.ms_usuarios.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MsUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUsuariosApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeRoles(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Crear roles si no existen
			Role userRole = null;
			if (roleRepository.findByNombre("ROLE_USER").isEmpty()) {
				userRole = new Role();
				userRole.setNombre("ROLE_USER");
				userRole = roleRepository.save(userRole);
			} else {
				userRole = roleRepository.findByNombre("ROLE_USER").get();
			}
			
			Role adminRole = null;
			if (roleRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
				adminRole = new Role();
				adminRole.setNombre("ROLE_ADMIN");
				adminRole = roleRepository.save(adminRole);
			} else {
				adminRole = roleRepository.findByNombre("ROLE_ADMIN").get();
			}
			
			Role meseroRole = null;
			if (roleRepository.findByNombre("ROLE_MESERO").isEmpty()) {
				meseroRole = new Role();
				meseroRole.setNombre("ROLE_MESERO");
				meseroRole = roleRepository.save(meseroRole);
			} else {
				meseroRole = roleRepository.findByNombre("ROLE_MESERO").get();
			}
			
			// Crear usuarios de prueba si no existen
			if (userRepository.findByEmail("juan@example.com").isEmpty()) {
				User userNormal = new User();
				userNormal.setNombre("Juan Pérez");
				userNormal.setEmail("juan@example.com");
				userNormal.setPassword(passwordEncoder.encode("password123"));
				userNormal.setRol(userRole);
				userRepository.save(userNormal);
			}
			
			if (userRepository.findByEmail("admin@example.com").isEmpty()) {
				User userAdmin = new User();
				userAdmin.setNombre("Admin Sistema");
				userAdmin.setEmail("admin@example.com");
				userAdmin.setPassword(passwordEncoder.encode("password123"));
				userAdmin.setRol(adminRole);
				userRepository.save(userAdmin);
			}
			
			if (userRepository.findByEmail("mesero@example.com").isEmpty()) {
				User userMesero = new User();
				userMesero.setNombre("Mesero Principal");
				userMesero.setEmail("mesero@example.com");
				userMesero.setPassword(passwordEncoder.encode("password123"));
				userMesero.setRol(meseroRole);
				userRepository.save(userMesero);
			}
		};
	}

}
