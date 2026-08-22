package io.flexwiz.openapi.merger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;

@QuarkusTest
public class OpenApiMergerTest {

    private OpenApiMerger merger;
    private String userServiceSpec;
    private String orderServiceSpec;
    
    @BeforeEach
    public void setup() throws IOException {
        merger = new OpenApiMerger();
        
        // Load test OpenAPI specs from test resources
        userServiceSpec = loadResourceAsString("/openapi/user-service.yaml");
        orderServiceSpec = loadResourceAsString("/openapi/order-service.json");
    }
    
    @Test
    public void testMergeSpecs() {
        // Test with two valid specs
        List<String> specs = Arrays.asList(userServiceSpec, orderServiceSpec);
        OpenAPI mergedSpec = merger.mergeSpecs(specs);
        
        // Verify merged spec
        assertNotNull(mergedSpec);
        assertEquals("Merged API Documentation", mergedSpec.getInfo().getTitle());
        
        // Verify paths from both specs are included
        Paths paths = mergedSpec.getPaths();
        assertTrue(paths.containsKey("/user-service/users"));
        assertTrue(paths.containsKey("/order-service/orders"));
        
        // Verify components are merged
        assertNotNull(mergedSpec.getComponents());
        assertNotNull(mergedSpec.getComponents().getSchemas());
        assertTrue(mergedSpec.getComponents().getSchemas().containsKey("User"));
        assertTrue(mergedSpec.getComponents().getSchemas().containsKey("Order"));
    }
    
    @Test
    public void testMergeSpecsWithEmptyList() {
        // Test with empty list
        assertThrows(IllegalArgumentException.class, () -> {
            merger.mergeSpecs(Collections.emptyList());
        });
    }
    
    @Test
    public void testMergeSpecsWithNullList() {
        // Test with null list
        assertThrows(IllegalArgumentException.class, () -> {
            merger.mergeSpecs(null);
        });
    }
    
    @Test
    public void testMergeSpecsWithInvalidSpec() {
        // Test with one valid and one invalid spec
        List<String> specs = Arrays.asList(userServiceSpec, "Invalid OpenAPI spec");
        OpenAPI mergedSpec = merger.mergeSpecs(specs);
        
        // Should still return a valid spec based on the valid one
        assertNotNull(mergedSpec);
        assertEquals("Merged API Documentation", mergedSpec.getInfo().getTitle());
        
        // Should only contain paths from valid spec
        Paths paths = mergedSpec.getPaths();
        assertTrue(paths.containsKey("/user-service/users"));
    }
    
    private String loadResourceAsString(String resourcePath) throws IOException {
        try (var inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}