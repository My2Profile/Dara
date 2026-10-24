package kz.ruzgaliyev.Daras.mapper;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import kz.ruzgaliyev.Daras.dto.UserResponse;
import kz.ruzgaliyev.Daras.repository.UserRepository;

@Component 
public class UserMapper {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserMapper(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse toResponse(User user) {
        
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setActive(user.isActive());
        return response;
    }
    
}
