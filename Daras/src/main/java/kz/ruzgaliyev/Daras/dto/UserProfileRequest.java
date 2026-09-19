package kz.ruzgaliyev.Daras.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRequest {

    @NotBlank(message = "First name is required and cannot be left blank!")
    private String firstName;
    
    @NotBlank
    private String lastName;

    @NotBlank
    private String phoneNumber;
    
    @NotBlank
    private String bio;
    
}
