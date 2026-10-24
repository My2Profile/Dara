package kz.ruzgaliyev.Daras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class UpdateUserRoleRequest {
    
    @NotBlank 

    private String role;

}
