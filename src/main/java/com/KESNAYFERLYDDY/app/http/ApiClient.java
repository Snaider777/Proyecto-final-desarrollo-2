package com.KESNAYFERLYDDY.app.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class ApiClient {
    private static final HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    public static HttpClient get() { return client; }

    public static HttpRequest.Builder jsonRequest(String url) {
        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(20));
    }
}
