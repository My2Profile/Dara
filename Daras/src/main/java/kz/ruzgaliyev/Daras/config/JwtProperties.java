package kz.ruzgaliyev.Daras.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    Access access,
    Refresh refresh,
    String tokenPrefix,
    String header
) {
    public record Access(long expiration) {}
    public record Refresh(long expiration) {}
}
