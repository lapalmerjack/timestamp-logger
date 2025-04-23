package com.demo.timesamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class UrlHealthIndicator implements HealthIndicator {

    private final HttpClient client = HttpClient.newHttpClient();



    @Value("${external.api.health}")
    private String healthUrl;

    @Override
    public Health health() {
        try {
           HttpRequest request = HttpRequest.newBuilder().uri(new URI( healthUrl)).version(HttpClient.Version.HTTP_2).GET().build();
          HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            System.out.println("CHECKING");

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return Health.up().withDetail("pingpong", "Available").build();
            } else {
                return Health.down().withDetail("pingpong", "Unexpected status: " + response.statusCode()).build();
            }
        } catch(WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode().value()), "Error fetching data: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch data.");
        }
    }
}
