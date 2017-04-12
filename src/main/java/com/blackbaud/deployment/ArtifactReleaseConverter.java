package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.ArtifactInfoService;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactReleaseConverter {

    @Autowired
    private ArtifactInfoService artifactInfoService;

    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactRelease toApi(ArtifactReleaseLogEntity entity) {
        if (entity == null) {
            return null;
        }
        ArtifactRelease artifactRelease = entityMapper.mapIfNotNull(entity, ArtifactRelease.class);
        ArtifactInfo artifactInfo = artifactInfoService.find(entity.getArtifactId(), entity.getBuildVersion());
        artifactRelease.setGitSha(artifactInfo.getGitSha());
        artifactRelease.setDependencies(artifactInfo.getDependencies());
        return artifactRelease;
    }

    public ArtifactRelease toApi(ArtifactInfo artifactInfo) {
        if (artifactInfo == null) {
            return null;
        }
        return ArtifactRelease.builder().artifactId(artifactInfo.getArtifactId())
                .buildVersion(artifactInfo.getBuildVersion())
                .gitSha(artifactInfo.getGitSha())
                .dependencies(artifactInfo.getDependencies())
                .build();
    }

    public List<ArtifactRelease> toApiList(List<ArtifactReleaseLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
