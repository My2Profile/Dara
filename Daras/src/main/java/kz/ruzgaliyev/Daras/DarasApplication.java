package kz.ruzgaliyev.Daras;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import kz.ruzgaliyev.Daras.config.JwtProperties;
import kz.ruzgaliyev.Daras.service.JwtService;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class DarasApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DarasApplication.class, args);

		JwtService jwtService = context.getBean(JwtService.class);

		String token = jwtService.generateToken("test@mail.com", "USER");
		System.out.println("TOKEN: " + token);
		System.out.println("EMAIL: " + jwtService.extractEmail(token));
		System.out.println("ROLE: " + jwtService.extractRole(token));
		System.out.println("VALID: " + jwtService.validateToken(token));
	}

}