package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.ArtifactReleaseDiffConverter;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogReportQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.ARTIFACT_RELEASE_REPORT)
@Produces(MediaType.APPLICATION_JSON)
public class ArtifactReleaseReportResource {

    @Autowired
    private ArtifactReleaseLogReportQuery artifactReleaseLogReportQuery;

    @Autowired
    private ArtifactReleaseDiffConverter converter;

    @GET
    @Path("{foundation}")
    public List<ArtifactReleaseDiff> getArtifactReleaseLog(@PathParam("foundation") String foundation) {
        return converter.toApiList(artifactReleaseLogReportQuery.getArtifactReleaseReport(foundation));
    }
}