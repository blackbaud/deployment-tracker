package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.DeploymentInfo;
import com.blackbaud.deployment.api.Release;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.GitLogParserFactory;
import com.blackbaud.deployment.core.domain.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            return new Release(releaseService.createDeploymentDiffs());
        } catch (GitLogParserFactory.InvalidRepositoryException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @POST
    @Path(ResourcePaths.CURRENT_PATH)
    public Release getCurrentReleaseForProdSnapshot(List<DeploymentInfo> prodDeploymentInfos) {
        try {
            return new Release(releaseService.createDeploymentDiffs(prodDeploymentInfos));
        } catch (GitLogParserFactory.InvalidRepositoryException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}
