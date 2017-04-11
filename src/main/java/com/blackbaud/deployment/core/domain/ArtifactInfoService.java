package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.core.domain.git.GitLogEntity;
import com.blackbaud.deployment.core.domain.git.GitLogParser;
import com.blackbaud.deployment.core.domain.git.GitLogParserFactory;
import com.blackbaud.deployment.core.domain.git.GitLogRepository;
import com.offbytwo.jenkins.model.Artifact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        ArtifactDependencyEntity dependencyEntity = new ArtifactDependencyEntity(artifactInfo, dependency);
        artifactDependencyRepository.save(dependencyEntity);
    }

    private void persistGitLogForArtifact(String artifactId, ArtifactInfoEntity artifact) {
        GitLogParser parser = gitLogParserFactory.createParser(artifact);
        List<GitLogEntity> gitLogEntities = parser.getGitLogEntities(artifactId);
        gitLogRepository.save(gitLogEntities);
    }

    public List<ArtifactInfo> getDependencies(ArtifactRelease artifactRelease) {
        return getDependencies(artifactRelease.getArtifactId(), artifactRelease.getBuildVersion());
    }

    public List<ArtifactInfo> getDependencies(ArtifactInfo artifactInfo) {
        return getDependencies(artifactInfo.getArtifactId(), artifactInfo.getBuildVersion());
    }

    public List<ArtifactInfo> getDependencies(String artifactId, String buildVersion) {
        List<ArtifactDependencyEntity> dependencies = getDependenciesFor(artifactId, buildVersion);
        if (dependencies == null) {
            return null;
        } else {
            List<ArtifactInfoEntity> dependencyInfos = dependencies.stream()
                    .map(dep -> artifactInfoRepository.findOneByArtifactIdAndBuildVersion(dep.getDependencyId(), dep.getDependencyBuildVersion()))
                    .collect(Collectors.toList());
            return converter.toApiList(dependencyInfos);
        }
    }

    private List<ArtifactDependencyEntity> getDependenciesFor(String artifactId, String buildVersion) {
        return artifactDependencyRepository.findByArtifactIdAndBuildVersion(artifactId, buildVersion);
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
