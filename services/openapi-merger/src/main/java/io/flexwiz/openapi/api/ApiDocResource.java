package io.flexwiz.openapi.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.flexwiz.openapi.merger.MergerService;

/**
 * OpenAPI docs resource
 */
@Path("/api-docs")
@ApplicationScoped
public class ApiDocResource {
    
    @Inject
    MergerService mergerService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMergedSpecAsJson() {
        return Response.ok(mergerService.getMergedSpecAsJson()).build();
    }
    
    @GET
    @Path("/yaml")
    @Produces("application/yaml")
    public Response getMergedSpecAsYaml() {
        return Response.ok(mergerService.getMergedSpecAsYaml()).build();
    }
    
    @GET
    @Path("/refresh")
    public Response refreshSpecs() {
        mergerService.invalidateCache();
        return Response.ok("Cache invalidated, specs will be refreshed on next request").build();
    }
}