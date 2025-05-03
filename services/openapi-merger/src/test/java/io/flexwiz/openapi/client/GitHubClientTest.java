package io.flexwiz.openapi.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GitHubClientTest {

    @Mock
    private HttpClient httpClient;
    
    @InjectMocks
    private GitHubClient gitHubClient;
    
    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        // Create a mock HttpResponse
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("Test OpenAPI content");
        
        // Set up the HttpClient mock to return our mock response
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
    }
    
    @Test
    public void testFetchOpenApiSpec() {
        String result = gitHubClient.fetchOpenApiSpec(
            "https://github.com/mycompany/test-repo",
            "main",
            "openapi.yaml",
            "test-token"
        );
        
        assertEquals("Test OpenAPI content", result);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchOpenApiSpecFailure() throws IOException, InterruptedException {
        // Mock a failed response
        HttpResponse<String> mockFailedResponse = mock(HttpResponse.class);
        when(mockFailedResponse.statusCode()).thenReturn(404);
        
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockFailedResponse);
        
        // Test that the client throws an exception for a non-200 response
        assertThrows(RuntimeException.class, () -> {
            gitHubClient.fetchOpenApiSpec(
                "https://github.com/mycompany/test-repo",
                "main",
                "openapi.yaml",
                "test-token"
            );
        });
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchOpenApiSpecNetworkError() throws IOException, InterruptedException {
        // Mock a network error
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Network error"));
        
        // Test that the client propagates the exception
        assertThrows(RuntimeException.class, () -> {
            gitHubClient.fetchOpenApiSpec(
                "https://github.com/mycompany/test-repo",
                "main",
                "openapi.yaml",
                "test-token"
            );
        });
    }
}