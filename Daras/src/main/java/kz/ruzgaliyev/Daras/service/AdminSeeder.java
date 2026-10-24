package kz.ruzgaliyev.Daras.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kz.ruzgaliyev.Daras.entity.User;
import kz.ruzgaliyev.Daras.repository.UserRepository;

@Service 
@ConditionalOnProperty(prefix = "app.seed-admin", name = "enabled", havingValue = "true")
public class AdminSeeder implements CommandLineRunner{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override    
    public void run(String... args) throws Exception {
        if(!userRepository.existsByEmail()) {
            User user = new User();
            user.setActive(true);
            user.setEmail("admin-seeder@gmail.com");
            user.setRole("ADMIN");
            user.setPassword(passwordEncoder.encode("StrongPassword123"));
            userRepository.save(user);
        }
        
    }
    


}
