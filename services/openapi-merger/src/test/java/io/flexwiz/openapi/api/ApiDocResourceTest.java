
package io.flexwiz.openapi.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.ws.rs.core.MediaType;

import io.flexwiz.openapi.merger.MergerService;

@QuarkusTest
public class ApiDocResourceTest {

    @InjectMock
    MergerService mergerService;
    
    @Test
    public void testGetMergedSpecAsJson() {
        // Mock the merger service response
        when(mergerService.getMergedSpecAsJson()).thenReturn("{\"openapi\":\"3.0.0\",\"info\":{\"title\":\"Test API\"}}");
        
        // Test the endpoint
        given()
            .when()
            .get("/api-docs")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(containsString("Test API"));
    }
    
    @Test
    public void testGetMergedSpecAsYaml() {
        // Mock the merger service response
        when(mergerService.getMergedSpecAsYaml()).thenReturn("openapi: 3.0.0\ninfo:\n  title: Test API");
        
        // Test the endpoint
        given()
            .when()
            .get("/api-docs/yaml")
            .then()
            .statusCode(200)
            .contentType("application/yaml")
            .body(containsString("Test API"));
    }
    
    @Test
    public void testRefreshSpecs() {
        // Test the refresh endpoint
        given()
            .when()
            .get("/api-docs/refresh")
            .then()
            .statusCode(200)
            .body(containsString("Cache invalidated"));
    }
}