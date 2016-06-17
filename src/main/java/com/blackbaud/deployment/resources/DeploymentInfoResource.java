package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoService;
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
@Path(ResourcePaths.DEPRECATED_DEPLOYMENT_INFO_PATH + "/{foundation}/{space}")
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentInfoResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ArtifactReleaseInfoConverter converter;

    @Autowired
    private ArtifactReleaseInfoService artifactReleaseInfoService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public ArtifactReleaseInfo update(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                      @Valid ArtifactReleaseInfo artifactReleaseInfo) {
        return artifactReleaseInfoService.save(artifactReleaseInfo, foundation, space);
    }

    @GET
    @Path("{artifactId}")
    public ArtifactReleaseInfo find(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                    @PathParam("artifactId") String artifactId) {
        ArtifactReleaseInfo artifactReleaseInfo = artifactReleaseInfoService.findOneByFoundationAndSpaceAndArtifactId(foundation, space, artifactId);
        if (artifactReleaseInfo == null) {
            throw new NotFoundException();
        }
        return artifactReleaseInfo;
    }

    @GET
    public List<ArtifactReleaseInfo> findAllInSpace(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        return artifactReleaseInfoService.findManyByFoundationAndSpace(foundation, space);
    }

}
