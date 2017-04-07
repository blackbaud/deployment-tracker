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
import javax.ws.rs.NotFoundException;
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
    private ArtifactDependencyRepository artifactDependencyRepository;

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
                log.debug("Skipping exception: {}.", ex);
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
        return create(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion(), artifactInfo);
    }

    public ArtifactInfo create(ArtifactInfo artifactInfo) {
        return create(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion(), artifactInfo);
    }

    public ArtifactInfo create(String artifactId, String buildVersion, ArtifactInfo artifactInfo) {
        ArtifactInfoEntity artifact = saveArtifactInfo(artifactId, buildVersion, artifactInfo);
        saveArtifactDependency(artifactInfo);
        return converter.toApi(artifact);
    }

    public ArtifactInfoEntity saveArtifactInfo(String artifactId, String buildVersion, ArtifactInfo artifactInfo) {
        ArtifactInfoEntity artifact = converter.toEntity(artifactInfo);
        persistGitLogForArtifact(artifactId, artifact);
        artifact.setArtifactId(artifactId);
        artifact.setBuildVersion(buildVersion);
        artifactInfoRepository.save(artifact);
        return artifact;
    }

    public void saveArtifactDependency(ArtifactInfo artifactInfo) {
        if (artifactInfo.getDependencies() == null || artifactInfo.getDependencies().isEmpty()) {
            return;
        }
        ArtifactInfo dependency = artifactInfo.getDependencies().get(0);
        ArtifactDependencyEntity dependencyEntity = ArtifactDependencyEntity.builder()
                .artifactId(artifactInfo.getArtifactId())
                .buildVersion(artifactInfo.getBuildVersion())
                .dependencyId(dependency.getArtifactId())
                .dependencyBuildVersion(dependency.getBuildVersion())
                .build();
        artifactDependencyRepository.save(dependencyEntity);
    }

    private void persistGitLogForArtifact(String artifactId, ArtifactInfoEntity artifact) {
        GitLogParser parser = gitLogParserFactory.createParser(artifact);
        List<GitLogEntity> gitLogEntities = parser.getGitLogEntities(artifactId);
        gitLogRepository.save(gitLogEntities);
    }

    public ArtifactInfo getDependencies(ArtifactInfo artifactInfo) {
        ArtifactDependencyEntity artifactDependencyPair = getDependenciesFor(artifactInfo);
        if (artifactDependencyPair == null) {
            return null;
        } else {
            ArtifactInfoEntity dependencyInfo = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(artifactDependencyPair.getDependencyId(), artifactDependencyPair.getDependencyBuildVersion());
            return converter.toApi(dependencyInfo);
        }
    }

    // TODO change to find a list of dependencies
    private ArtifactDependencyEntity getDependenciesFor(ArtifactInfo artifactInfo) {
        return artifactDependencyRepository.findOneByArtifactIdAndBuildVersion(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion());
    }

    public ArtifactInfo find(String artifactId, String buildVersion) {
        ArtifactInfoEntity requestedArtifact = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, buildVersion));
        if (requestedArtifact == null) {
            return null;
        }
        return converter.toApi(requestedArtifact);
    }

    public class ArtifactInfoIsImmutableException extends BadRequestException { // NOSONAR
        public ArtifactInfoIsImmutableException(String message) {
            super(message);
        }
    }
}
