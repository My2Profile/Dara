package kz.ruzgaliyev.Daras.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;

    private String tokenType = "Bearer";

    private Long expiresIn;
    

}
