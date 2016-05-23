package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactInfoConverter {
    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactInfoEntity toEntity(ArtifactInfo info) {
        return entityMapper.mapIfNotNull(info, ArtifactInfoEntity.class);
    }

    public List<ArtifactInfoEntity> toEntityList(Iterable<ArtifactInfo> infoList) {
        return entityMapper.mapList(infoList, this::toEntity);
    }

    public ArtifactInfo toApi(ArtifactInfoEntity entity) {
        if (entity == null) {
            return null;
        }
        return entityMapper.mapIfNotNull(entity, ArtifactInfo.class);
    }

    public List<ArtifactInfo> toApiList(List<ArtifactInfoEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
