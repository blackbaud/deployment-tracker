package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactReleaseInfoConverter {

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactRelease toApi(ArtifactReleaseLogEntity entity) {
        if (entity == null) {
            return null;
        }
        ArtifactRelease artifactRelease = entityMapper.mapIfNotNull(entity, ArtifactRelease.class);
        String gitSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(entity.getArtifactId(), entity.getBuildVersion()).getGitSha();
        artifactRelease.setGitSha(gitSha);
        return artifactRelease;
    }

    public List<ArtifactRelease> toApiList(List<ArtifactReleaseLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
