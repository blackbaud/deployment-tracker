package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
    public List<ArtifactReleaseLog> getArtifactReleaseLog() {
        return artifactReleaseLogService.findAll();
    }
}

