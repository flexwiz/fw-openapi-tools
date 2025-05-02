package io.flexwiz.openapi.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

/**
 * OpenAPI Merger
 */
@ApplicationScoped
public class OpenApiMerger {
    private static final Logger LOG = LoggerFactory.getLogger(OpenApiMerger.class);
    
    public OpenAPI mergeSpecs(List<String> specs) {
        if (specs == null || specs.isEmpty()) {
            throw new IllegalArgumentException("No OpenAPI specs provided for merging");
        }
        
        // Parse the first spec to use as a base
        OpenAPI mergedSpec = parseSpec(specs.get(0));
        if (mergedSpec == null) {
            throw new RuntimeException("Failed to parse the first OpenAPI spec");
        }
        
        // Set merged spec info
        Info mergedInfo = new Info()
                .title("Merged API Documentation")
                .version("1.0.0")
                .description("This is a merged API documentation from multiple services");
        mergedSpec.setInfo(mergedInfo);
        
        List<Tag> allTags = new ArrayList<>(mergedSpec.getTags() != null ? mergedSpec.getTags() : new ArrayList<>());
        
        // Merge the rest of the specs
        for (int i = 1; i < specs.size(); i++) {
            OpenAPI currentSpec = parseSpec(specs.get(i));
            if (currentSpec == null) {
                LOG.warn("Skipping invalid OpenAPI spec at index {}", i);
                continue;
            }
            
            // Merge paths
            mergePaths(mergedSpec, currentSpec);
            
            // Merge tags
            if (currentSpec.getTags() != null) {
                for (Tag tag : currentSpec.getTags()) {
                    if (allTags.stream().noneMatch(t -> t.getName().equals(tag.getName()))) {
                        allTags.add(tag);
                    }
                }
            }
            
            // Merge components
            if (currentSpec.getComponents() != null) {
                if (mergedSpec.getComponents() == null) {
                    mergedSpec.setComponents(currentSpec.getComponents());
                } else {
                    // Merge schemas
                    if (currentSpec.getComponents().getSchemas() != null) {
                        mergedSpec.getComponents().getSchemas().putAll(currentSpec.getComponents().getSchemas());
                    }
                    // Merge security schemes
                    if (currentSpec.getComponents().getSecuritySchemes() != null) {
                        mergedSpec.getComponents().getSecuritySchemes().putAll(currentSpec.getComponents().getSecuritySchemes());
                    }
                    // Merge other component objects as needed
                }
            }
        }
        
        mergedSpec.setTags(allTags);
        return mergedSpec;
    }
    
    private OpenAPI parseSpec(String specContent) {
        try {
            SwaggerParseResult result = new OpenAPIParser().readContents(specContent, null, null);
            if (result.getMessages() != null && !result.getMessages().isEmpty()) {
                LOG.warn("OpenAPI parsing warnings: {}", result.getMessages());
            }
            return result.getOpenAPI();
        } catch (Exception e) {
            LOG.error("Failed to parse OpenAPI spec", e);
            return null;
        }
    }
    
    private void mergePaths(OpenAPI targetSpec, OpenAPI sourceSpec) {
        if (sourceSpec.getPaths() == null || sourceSpec.getPaths().isEmpty()) {
            return;
        }
        
        if (targetSpec.getPaths() == null) {
            targetSpec.setPaths(new Paths());
        }
        
        // Add a prefix to the source paths to avoid conflicts
        String prefix = "/" + sourceSpec.getInfo().getTitle().toLowerCase().replace(" ", "-");
        
        for (Map.Entry<String, PathItem> entry : sourceSpec.getPaths().entrySet()) {
            String path = prefix + entry.getKey();
            if (targetSpec.getPaths().containsKey(path)) {
                LOG.warn("Path {} already exists in the merged spec, adding service prefix", path);
                // If there's still a conflict, we could add more uniqueness here
            }
            targetSpec.getPaths().addPathItem(path, entry.getValue());
        }
    }
}