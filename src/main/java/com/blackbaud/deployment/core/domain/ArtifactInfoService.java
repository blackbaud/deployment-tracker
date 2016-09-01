package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.core.domain.git.GitLogEntity;
import com.blackbaud.deployment.core.domain.git.GitLogParser;
import com.blackbaud.deployment.core.domain.git.GitLogParserFactory;
import com.blackbaud.deployment.core.domain.git.GitLogRepository;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.List;

@Component
@Log4j
public class ArtifactInfoService {

    @Autowired
    private ArtifactInfoConverter converter;

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private GitLogParserFactory gitLogParserFactory;

    @Autowired
    private GitLogRepository gitLogRepository;

    public ArtifactInfo createIfNotExist(ArtifactInfo artifactInfo) {
        ArtifactInfoEntity existingEntity = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion());
        if (existingEntity != null) {
            if (!existingEntity.getGitSha().equals(artifactInfo.getGitSha())) {
                throw new ArtifactInfoAlreadyHasGitShaException(existingEntity.toString() + " cannot be overidden with " + artifactInfo);
            }
            return artifactInfo;
        }
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

    public class ArtifactInfoAlreadyHasGitShaException extends BadRequestException {
        public ArtifactInfoAlreadyHasGitShaException(String message) {
            super(message);
        }
    }
}
