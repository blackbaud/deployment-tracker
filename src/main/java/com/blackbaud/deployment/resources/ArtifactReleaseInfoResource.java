package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogService;
import com.blackbaud.deployment.core.domain.git.GitLogParserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
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
@Path(ResourcePaths.ARTIFACT_RELEASE_INFO_PATH + "/{foundation}/{space}")
@Produces(MediaType.APPLICATION_JSON)
public class ArtifactReleaseInfoResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ArtifactReleaseLogService artifactReleaseLogService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @SuppressWarnings("CPD-START")
    public ArtifactRelease update(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                  @Valid ArtifactRelease artifactRelease) {
        try{
            return artifactReleaseLogService.save(artifactRelease, foundation, space);
        } catch (GitLogParserFactory.InvalidRepositoryException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @GET
    @Path("{artifactId}")
    @SuppressWarnings("CPD-END")
    public ArtifactRelease find(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                @PathParam("artifactId") String artifactId) {
        ArtifactRelease artifactRelease = artifactReleaseLogService.findOneByFoundationAndSpaceAndArtifactId(foundation, space, artifactId);
        if (artifactRelease == null) {
            throw new NotFoundException();
        }
        return artifactRelease;
    }

    @GET
    public List<ArtifactRelease> findAllInSpace(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        return artifactReleaseLogService.findManyByFoundationAndSpace(foundation, space);
    }

}
