package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactReleaseLogConverter {

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactReleaseInfoEntity toEntity(ArtifactReleaseInfo info, String foundation, String space) {
        ArtifactReleaseInfoEntity entity = entityMapper.mapIfNotNull(info, ArtifactReleaseInfoEntity.class);
        entity.setFoundation(foundation);
        entity.setSpace(space);
        return entity;
    }

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
