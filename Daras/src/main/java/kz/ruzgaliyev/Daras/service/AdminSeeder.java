package kz.ruzgaliyev.Daras.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kz.ruzgaliyev.Daras.entity.User;
import kz.ruzgaliyev.Daras.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;



@Service 
@ConditionalOnProperty(prefix = "app.seed-admin", name = "enabled", havingValue = "true")
public class AdminSeeder implements CommandLineRunner{

    @Value("${app.seed-admin.email}")
    private String adminEmail;

    @Value("${app.seed-admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

        

    }

    @Override    
    public void run(String... args) throws Exception {
        if(!userRepository.existsByEmail(adminEmail)) {
            User user = new User();
            user.setActive(true);
            user.setEmail(adminEmail);
            user.setRole("ADMIN");
            user.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(user);
        }
        
    }
    


}
