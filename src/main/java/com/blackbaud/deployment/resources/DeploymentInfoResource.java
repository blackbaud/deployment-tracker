package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.DeploymentInfoConverter;
import com.blackbaud.deployment.api.DeploymentInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.DeploymentInfoEntity;
import com.blackbaud.deployment.core.domain.DeploymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Component
@Path(ResourcePaths.DEPLOYMENT_INFO_PATH + "/{foundation}/{space}")
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentInfoResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private DeploymentInfoConverter converter;

    @Autowired
    private DeploymentInfoService deploymentInfoService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public DeploymentInfo update(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                 @Valid DeploymentInfo deploymentInfo) {
        return deploymentInfoService.save(deploymentInfo, foundation, space);
    }

    @GET
    @Path("{artifactId}")
    public DeploymentInfo find(@PathParam("foundation") String foundation, @PathParam("space") String space,
                               @PathParam("artifactId") String artifactId) {
        DeploymentInfo deploymentInfo = deploymentInfoService.findOneByFoundationAndSpaceAndArtifactId(foundation, space, artifactId);
        if (deploymentInfo == null) {
            throw new NotFoundException();
        }
        return deploymentInfo;
    }

    @GET
    public List<DeploymentInfo> findAllInSpace(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        return deploymentInfoService.findManyByFoundationAndSpace(foundation, space);
    }

}
