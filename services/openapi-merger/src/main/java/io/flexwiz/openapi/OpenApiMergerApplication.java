package io.flexwiz.openapi;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

/**
 * OpenAPI Merger Main Application
 */
@OpenAPIDefinition(
    info = @Info(
        title = "OpenAPI Merger",
        version = "1.0.0",
        description = "Application for merging multiple OpenAPI specifications from different GitHub repositories",
        contact = @Contact(
            name = "API Team",
            email = "api-team@flexwiz.io"
        )
    )
)
public class OpenApiMergerApplication extends Application {
    // No implementation needed
}