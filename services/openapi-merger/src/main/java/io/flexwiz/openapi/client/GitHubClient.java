package io.flexwiz.openapi.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for GitHub API
 */
@ApplicationScoped
public class GitHubClient {
    private static final Logger LOG = LoggerFactory.getLogger(GitHubClient.class);
    private final HttpClient httpClient;
    
    public GitHubClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
    
    public String fetchOpenApiSpec(String repoUrl, String branch, String specPath, String token) {
        try {
            // Convert GitHub repo URL to API URL for raw content
            String apiUrl = convertToRawContentUrl(repoUrl, branch, specPath);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "token " + token)
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                LOG.error("Failed to fetch spec from {}: {}", apiUrl, response.statusCode());
                throw new RuntimeException("Failed to fetch OpenAPI spec: HTTP " + response.statusCode());
            }
            
            return response.body();
        } catch (IOException | InterruptedException e) {
            LOG.error("Error fetching OpenAPI spec", e);
            throw new RuntimeException("Failed to fetch OpenAPI spec", e);
        }
    }
    
    private String convertToRawContentUrl(String repoUrl, String branch, String specPath) {
        // Convert https://github.com/mycompany/user-service to
        // https://raw.githubusercontent.com/mycompany/user-service/main/src/main/resources/openapi.yaml
        String baseUrl = repoUrl.replace("github.com", "raw.githubusercontent.com");
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/" + branch + "/" + specPath;
    }
}