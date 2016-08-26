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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Component
@Path(ResourcePaths.ARTIFACT_RELEASE_PATH + "/{foundation}/{space}")
@Produces(MediaType.APPLICATION_JSON)
public class ArtifactReleaseResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ArtifactReleaseLogService artifactReleaseLogService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ArtifactRelease create(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                  @Valid ArtifactRelease artifactRelease) {
        try{
            return artifactReleaseLogService.save(artifactRelease, foundation, space);
        } catch (GitLogParserFactory.InvalidRepositoryException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @POST
    @Path("bulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ArtifactRelease> createAll(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                  @Valid List<ArtifactRelease> artifactReleases) {
        List<ArtifactRelease> result = new ArrayList<>();
        try{
            artifactReleases.stream().forEach(artifactRelease -> {
                result.add(artifactReleaseLogService.save(artifactRelease, foundation, space));
            });
        } catch (GitLogParserFactory.InvalidRepositoryException ex){
            throw new BadRequestException(ex.getMessage());
        }
        return result;
    }

    @GET
    public List<ArtifactRelease> findAllBySpaceAndFoundation(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        return artifactReleaseLogService.findAllByFoundationAndSpace(foundation, space);
    }

    @GET
    @Path("current")
    public List<ArtifactRelease> findLatestOfEachArtifactBySpaceAndFoundation(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        return artifactReleaseLogService.findLatestOfEachArtifactByFoundationAndSpace(foundation, space);
    }

}
