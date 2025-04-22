package com.demo.timesamp;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class Controller {
    private final WebClient webClient = WebClient.create();

    @Value("${external.api.url}")
    private String url;

    @Value("${external.api.health}")
    private String healthUrl;




    @GetMapping("/pingpong")
    public String returnTimeStamp() {
        int pingpong = fetchIntegerFromUrl();
        System.out.println("SUP");
        System.out.println("PINGPONG FETCHED: " + pingpong );
        return "<html>" +
                "<head><title>Timestamp</title></head>" +
                "<body>" +
                "<h2>" + LocalDateTime.now() + " : " + UUID.randomUUID() + "</h2>" +
                "<p>Ping / Pongs: " + pingpong + "</p>" +
                "</body>" +
                "</html>";
    }


    @GetMapping("/health")
    public String health() {
        System.out.println("SUP AGAIN BRO");
        try {
           return webClient.get().uri(healthUrl).retrieve().bodyToMono(String.class).block();
        } catch(WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode().value()), "Error fetching data: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch data.");
        }
    }


    public int fetchIntegerFromUrl() {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Integer.class)
                .block(); // Blocking call (used in synchronous execution)
    }
}
