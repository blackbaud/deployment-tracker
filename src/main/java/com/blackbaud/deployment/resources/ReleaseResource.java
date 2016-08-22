package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.Release;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.git.GitLogParserFactory;
import com.blackbaud.deployment.core.domain.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.RELEASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ReleaseResource {

    @Autowired
    private ReleaseService releaseService;

    @GET
    @Path(ResourcePaths.CURRENT_PATH)
    public Release getCurrentRelease() {
        try {
            return new Release(releaseService.createArtifactReleaseDiffs());
        } catch (GitLogParserFactory.InvalidRepositoryException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @POST
    @Path(ResourcePaths.CURRENT_PATH)
    public Release getCurrentReleaseForProdSnapshot(@NotNull List<ArtifactRelease> prodArtifactReleases) {
        try {
            return new Release(releaseService.createArtifactReleaseDiffs(prodArtifactReleases));
        } catch (GitLogParserFactory.InvalidRepositoryException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @POST
    @Path(ResourcePaths.RELEASE_PLAN_DIFF_PATH)
    public Release getCurrentReleasePlanDiffForProdSnapshot(@NotNull List<ArtifactRelease> prodArtifactReleases) {
        try {
            return new Release(releaseService.createArtifactReleaseDiffsForReleasePlanArtifacts(prodArtifactReleases));
        } catch (GitLogParserFactory.InvalidRepositoryException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}
