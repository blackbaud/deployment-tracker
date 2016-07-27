package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.GitLogConverter;
import com.blackbaud.deployment.api.GitLogInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.GitLogParser;
import com.blackbaud.deployment.core.domain.GitLogParserFactory;
import com.blackbaud.deployment.core.domain.GitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.GIT_LOG_INFO_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class GitLogInfoResource {

    @Autowired
    GitLogRepository gitLogRepository;

    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    GitLogParserFactory gitLogParserFactory;

    @Autowired
    GitLogConverter gitLogConverter;

    @GET
    @Path("{artifactId}")
    public List<GitLogInfo> findMany(@PathParam("artifactId") String artifactId) {
        return gitLogConverter.toApiList(gitLogRepository.fetchOrderedGitLogForArtifactId(artifactId));
    }

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

