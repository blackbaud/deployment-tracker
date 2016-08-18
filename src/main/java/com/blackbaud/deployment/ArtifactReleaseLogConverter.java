package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactReleaseLogConverter {

    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactReleaseLog toApi(ArtifactReleaseInfoLogEntity entity) {
        return entityMapper.mapIfNotNull(entity, ArtifactReleaseLog.class);
    }

    public List<ArtifactReleaseLog> toApiList(List<ArtifactReleaseInfoLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
