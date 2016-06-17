package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
<<<<<<< HEAD
<<<<<<< HEAD
public class ArtifactReleaseInfoConverter {
=======
public class DeploymentInfoConverter {
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
public class ArtifactReleaseInfoConverter {
>>>>>>> d0b6af9... LUM-9138 more renaming
    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactReleaseInfoEntity toEntity(ArtifactReleaseInfo info, String foundation, String space) {
        ArtifactReleaseInfoEntity entity = entityMapper.mapIfNotNull(info, ArtifactReleaseInfoEntity.class);
        entity.setFoundation(foundation);
        entity.setSpace(space);
        return entity;
    }

    public ArtifactReleaseInfo toApi(ArtifactReleaseInfoEntity entity) {
        if (entity == null) {
            return null;
        }
        return entityMapper.mapIfNotNull(entity, ArtifactReleaseInfo.class);
    }

    public List<ArtifactReleaseInfo> toApiList(List<ArtifactReleaseInfoEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
