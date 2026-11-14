package kz.ruzgaliyev.Daras.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service 
public class OutboundService {

    private static final Logger log = LoggerFactory.getLogger(OutboundService.class);
    private final RestClient restClient;

    public OutboundService(RestClient restClient) {
        this.restClient = restClient;
    }
    
    public String getExternalData() {
        try {
            String raw = restClient.get()
                .uri("/get")
                .retrieve()
                .body(String.class)
            return new getExternalData(raw)
        } catch (RestClientException e) {
            log.error("Error while calling external service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch external data", e);
        }
    }

}
