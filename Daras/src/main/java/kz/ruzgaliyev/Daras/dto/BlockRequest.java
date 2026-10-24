package kz.ruzgaliyev.Daras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class BlockRequest {
    @NotBlank 
    @Size(max = 255)
    private String reason;
    
}
