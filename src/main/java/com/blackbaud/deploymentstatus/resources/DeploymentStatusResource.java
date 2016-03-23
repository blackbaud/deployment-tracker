package com.blackbaud.deploymentstatus.resources;

import com.blackbaud.deploymentstatus.api.ResourcePaths;
import com.blackbaud.deploymentstatus.api.DeploymentStatus;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Path(ResourcePaths.DEPLOYMENT_STATUS_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentStatusResource {

    @Context
    private UriInfo uriInfo;

    @GET
    public List<DeploymentStatus> findAll() {
        return null;
    }

}
