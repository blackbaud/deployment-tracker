package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.GitLogParser;
import com.blackbaud.deployment.core.domain.GitLogParserFactory;
import com.blackbaud.deployment.core.domain.GitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.BACKFILL_GIT_LOG_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class BackfillGitLogResource {

    @Autowired
    GitLogRepository gitLogRepository;

    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    GitLogParserFactory gitLogParserFactory;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{artifactId}")
    public void post(@PathParam("artifactId") String artifactId) {
        backfillGitLogForArtifact(artifactId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void post() {
        artifactInfoRepository.getDistinctArtifactIds().forEach(this::backfillGitLogForArtifact);
    }

    private void backfillGitLogForArtifact(String artifactId) {
        ArtifactInfoEntity artifactInfoEntity = artifactInfoRepository.findFirstByArtifactIdOrderByBuildVersionDesc(artifactId);
        GitLogParser parser = gitLogParserFactory.createParser(artifactInfoEntity);
        gitLogRepository.save(parser.getGitLogEntities(artifactInfoEntity.getArtifactId()));
    }

}

