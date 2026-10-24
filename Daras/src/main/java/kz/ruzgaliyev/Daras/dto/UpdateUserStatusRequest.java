package kz.ruzgaliyev.Daras.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor 
public class UpdateUserStatusRequest {
    @NotNull 
    private Boolean state = true;
    
}
