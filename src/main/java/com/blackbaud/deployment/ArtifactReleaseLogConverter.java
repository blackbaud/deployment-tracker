package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactReleaseLogConverter {

    public ArtifactReleaseLog toApi(ArtifactReleaseInfoLogEntity entity) {
        if (entity == null) {
            return null;
        }
        return ArtifactReleaseLog.builder()
                .artifactId(entity.getArtifactId())
                .buildVersion(entity.getBuildVersion())
                .releaseVersion(entity.getReleaseVersion())
                .prevBuildVersion(entity.getPrevBuildVersion())
                .prevReleaseVersion(entity.getPrevReleaseVersion())
                .deployer(entity.getDeployer())
                .build();
    }

    public List<ArtifactReleaseLog> toApiList(List<ArtifactReleaseInfoLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
