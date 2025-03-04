package com.example.restapijwt.config;

import com.example.restapijwt.model.Product;
import com.example.restapijwt.model.Role;
import com.example.restapijwt.model.User;
import com.example.restapijwt.repository.ProductRepository;
import com.example.restapijwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder encoder;

    // Continuing from src/main/java/com/example/restapijwt/config/DataInitializer.java

    @Override
    public void run(String... args) throws Exception {
        // Create users
        if (userRepository.count() == 0) {
            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(Role.ADMIN);
            admin.setRoles(adminRoles);
            userRepository.save(admin);

            // Create regular user
            User user = new User();
            user.setUsername("user");
            user.setPassword(encoder.encode("user123"));
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(Role.USER);
            user.setRoles(userRoles);
            userRepository.save(user);
        }

        // Create sample products
        if (productRepository.count() == 0) {
            productRepository.save(new Product(null, "Laptop", "High-performance laptop", 1299.99));
            productRepository.save(new Product(null, "Smartphone", "Latest model smartphone", 899.99));
            productRepository.save(new Product(null, "Headphones", "Noise-cancelling headphones", 249.99));
            productRepository.save(new Product(null, "Tablet", "10-inch tablet with stylus", 599.99));
            productRepository.save(new Product(null, "Smartwatch", "Fitness tracking smartwatch", 199.99));
        }
    }
}