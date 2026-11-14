package kz.ruzgaliyev.Daras.config;

import java.beans.BeanProperty;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration 
public class OutboundConfig {
    
    @Bean 
    public RestClient restClient(RestClient.Builder builder, OutboundProperties properties) {
        HttpClientSettings settings = HttpClientSettings.defaults()
            .withTimeouts(properties.connectionTimeout(), properties.readTimeout());
        
        ClientHttpRequestFactoryBuilder requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings);

        return builder
              .baseUrl(properties.baseUrl())
              .requestFactory(requestFactory)
              .build();
    }

}
