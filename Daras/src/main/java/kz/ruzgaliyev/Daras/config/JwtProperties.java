package kz.ruzgaliyev.Daras.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ConfigurationProperties(prefix = "jwt")
@Validated 
public record JwtProperties(
    @NotBlank
    @Size(min = 36)
    String secret,
    @NotNull
    Access access,
    @NotNull 
    Refresh refresh,
    @NotBlank 
    String tokenPrefix,
    @NotBlank 
    String header
) {
    public record Access(long expiration) {}
    public record Refresh(long expiration) {}
}
