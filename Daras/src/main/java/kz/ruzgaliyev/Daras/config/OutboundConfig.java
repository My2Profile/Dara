package kz.ruzgaliyev.Daras.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "external")
public record OutboundConfig(
    @NotBlank 
    String baseUrl,

    @NotNull 
    long connectionTimeout,

    @NotNull 
    long readTimeout
) {

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }


    
}
