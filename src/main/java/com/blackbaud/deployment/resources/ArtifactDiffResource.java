package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactDiff;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoPrimaryKey;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.Gitter;
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
import javax.ws.rs.core.MediaType;
import java.net.URL;
import java.util.List;

@Component
@Path(ResourcePaths.ARTIFACT_DIFF_PATH + "/{artifactId}")
@Produces(MediaType.APPLICATION_JSON)
public class ArtifactDiffResource {

    @Autowired
    private ArtifactInfoConverter converter;

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private Gitter gitter;

    @GET
    @Path("/{fromBuildVersion}/{toBuildVersion}")
    public ArtifactDiff diff(@PathParam("artifactId") String artifactId, @PathParam("fromBuildVersion") String fromBuildVersion, @PathParam("toBuildVersion") String toBuildVersion) {
        ArtifactInfo fromArtifactInfo = demandArtifactInfo(artifactId, fromBuildVersion);
        ArtifactInfo toArtifactInfo = demandArtifactInfo(artifactId, toBuildVersion);

        String commits = gitter.getCommitsBetween(artifactId, fromArtifactInfo.getGitSha(), toArtifactInfo.getGitSha());
        List<URL> stories = gitter.parseStories(commits);

        return ArtifactDiff.builder()
                .fromArtifactInfo(fromArtifactInfo)
                .toArtifactInfo(toArtifactInfo)
                .gitCommits(commits)
                .stories(stories)
                .build();
    }

    private ArtifactInfo demandArtifactInfo(String artifactId, String buildVersion) {
        ArtifactInfoEntity artifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, buildVersion));
        if (artifactInfoEntity == null) {
            throw new NotFoundException();
        }
        return converter.toApi(artifactInfoEntity);
    }

}
