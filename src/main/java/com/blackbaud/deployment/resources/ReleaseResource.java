package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.DeploymentDiff;
import com.blackbaud.deployment.api.DeploymentInfo;
import com.blackbaud.deployment.api.Release;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Component
@Path(ResourcePaths.RELEASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ReleaseResource {

    @Autowired
    private ReleaseService releaseService;

    @GET
    @Path(ResourcePaths.CURRENT_PATH)
    public Release getCurrentRelease() {
        return new Release(releaseService.createDeploymentDiffs());
    }

    @POST
    @Path(ResourcePaths.CURRENT_PATH)
    public Release getCurrentReleaseForDevSnapshot(List<DeploymentInfo> devDeploymentInfos) {
        return new Release(releaseService.createDeploymentDiffs(devDeploymentInfos));
    }

    @GET
    @Path(ResourcePaths.DEPRECATED_CURRENT_SUMMARY_PATH)
    public Map<String, DeploymentDiff> getDeprecatedCurrentReleaseSummary() {
        return releaseService.createDeploymentDiffs();
    }

}
