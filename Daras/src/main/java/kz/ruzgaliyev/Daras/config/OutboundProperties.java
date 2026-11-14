package kz.ruzgaliyev.Daras.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "external")
public record OutboundProperties(
    @NotBlank 
    String baseUrl,

    @NotNull 
    Duration connectionTimeout,

    @NotNull 
    Duration readTimeout
) { }
