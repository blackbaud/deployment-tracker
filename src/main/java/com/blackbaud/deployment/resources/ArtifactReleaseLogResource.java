package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.api.ArtifactReleaseLogDetail;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.ARTIFACT_RELEASE_LOG)
@Produces(MediaType.APPLICATION_JSON)
public class ArtifactReleaseLogResource {

    @Autowired
    private ArtifactReleaseLogService artifactReleaseLogService;

    @GET
    public List<ArtifactReleaseLogDetail> getArtifactReleaseLog() {
        return artifactReleaseLogService.findAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ArtifactReleaseInfo create(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                     @Valid ArtifactReleaseInfo artifactReleaseInfo) {
        try{
            return artifactReleaseLogService.save(artifactReleaseInfo, foundation, space);
        } catch (GitLogParserFactory.InvalidRepositoryException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }
}

