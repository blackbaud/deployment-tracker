package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.core.domain.git.GitLogEntity;
import com.blackbaud.deployment.core.domain.git.GitLogParser;
import com.blackbaud.deployment.core.domain.git.GitLogParserFactory;
import com.blackbaud.deployment.core.domain.git.GitLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ArtifactInfoService {

    @Autowired
    private ArtifactInfoConverter converter;

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private GitLogParserFactory gitLogParserFactory;

    @Autowired
    private GitLogRepository gitLogRepository;

    public int remediationCreate(List<ArtifactInfo> artifactInfos) {
        List<ArtifactInfo> newArtifactInfos = new ArrayList<>();
        artifactInfos.stream().forEach(artifactInfo -> {
            try {
                newArtifactInfos.add(remediationCreate(artifactInfo));
            } catch (Exception ex) {
                log.debug("{}. Continuing with the rest.", ex.getMessage());
            }
        });
        return newArtifactInfos.size();
    }

    public ArtifactInfo remediationCreate(ArtifactInfo artifactInfo) {
        ArtifactInfoEntity existingEntity = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion());
        if (existingEntity != null && existingEntity.getGitSha() != null) {
            if (!existingEntity.getGitSha().equals(artifactInfo.getGitSha())) {
                throw new ArtifactInfoIsImmutableException("Cannot create " + artifactInfo + " because " + existingEntity + " already exists.");
            }
            return artifactInfo;
        }
        return create(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion(), converter.toEntity(artifactInfo));
    }

    public ArtifactInfo create(ArtifactInfo artifactInfo) {
        return create(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion(), converter.toEntity(artifactInfo));
    }

    public ArtifactInfo create(String artifactId, String buildVersion, ArtifactInfoEntity artifact) {
        persistGitLogForArtifact(artifactId, artifact);
        artifact.setArtifactId(artifactId);
        artifact.setBuildVersion(buildVersion);
        return converter.toApi(artifactInfoRepository.save(artifact));
    }

    private void persistGitLogForArtifact(String artifactId, ArtifactInfoEntity artifact) {
        GitLogParser parser = gitLogParserFactory.createParser(artifact);
        List<GitLogEntity> gitLogEntities = parser.getGitLogEntities(artifactId);
        gitLogRepository.save(gitLogEntities);
    }

    public class ArtifactInfoIsImmutableException extends BadRequestException {
        public ArtifactInfoIsImmutableException(String message) {
            super(message);
        }
    }
}
