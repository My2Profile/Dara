package kz.ruzgaliyev.Daras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor 
public class UpdateUserStatusRequest {

    private Boolean state = true;
    
}
