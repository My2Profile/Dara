package kz.ruzgaliyev.Daras.mapper;

// import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import kz.ruzgaliyev.Daras.dto.UserResponse;

import kz.ruzgaliyev.Daras.entity.User;

//UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt(), user.isActive());

@Component 
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt(), user.isActive());
    }
    
}
