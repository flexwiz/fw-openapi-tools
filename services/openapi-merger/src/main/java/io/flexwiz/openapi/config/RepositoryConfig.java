package io.flexwiz.openapi.config;

import java.util.List;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

/**
 * Configuration Model
 */
@ConfigMapping(prefix = "openapi-merger")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface RepositoryConfig {
    // List of Github repositories
    List<Repository> repositories();
    
    interface Repository {
        String name();
        String url();
        String branch();
        String specPath();
        String token();
    }
}