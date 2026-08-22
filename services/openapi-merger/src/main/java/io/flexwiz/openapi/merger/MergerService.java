package io.flexwiz.openapi.merger;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;

import io.flexwiz.openapi.client.GitHubClient;
import io.flexwiz.openapi.config.RepositoryConfig;
import io.flexwiz.openapi.merger.OpenApiMerger;

/**
 * OpenAPI Merger Service
 */
@ApplicationScoped
public class MergerService {
    private static final Logger LOG = LoggerFactory.getLogger(MergerService.class);
    
    @Inject
    RepositoryConfig config;
    
    @Inject
    GitHubClient gitHubClient;
    
    @Inject
    OpenApiMerger merger;
    
    private OpenAPI cachedMergedSpec;
    
    public synchronized OpenAPI getMergedSpec() {
        if (cachedMergedSpec != null) {
            return cachedMergedSpec;
        }
        
        List<String> specs = fetchAllSpecs();
        cachedMergedSpec = merger.mergeSpecs(specs);
        return cachedMergedSpec;
    }
    
    public void invalidateCache() {
        this.cachedMergedSpec = null;
    }
    
    public String getMergedSpecAsJson() {
        return Json.pretty(getMergedSpec());
    }
    
    public String getMergedSpecAsYaml() {
        return Yaml.pretty(getMergedSpec());
    }
    
    private List<String> fetchAllSpecs() {
        List<String> specs = new ArrayList<>();
        
        for (RepositoryConfig.Repository repo : config.repositories()) {
            try {
                LOG.info("Fetching OpenAPI spec from repository: {}", repo.name());
                String spec = gitHubClient.fetchOpenApiSpec(
                        repo.url(), 
                        repo.branch(), 
                        repo.specPath(), 
                        repo.token()
                );
                specs.add(spec);
            } catch (Exception e) {
                LOG.error("Failed to fetch spec from repository: {}", repo.name(), e);
                // Continue with other repositories even if one fails
            }
        }
        
        if (specs.isEmpty()) {
            throw new RuntimeException("Failed to fetch any valid OpenAPI specs");
        }
        
        return specs;
    }
}