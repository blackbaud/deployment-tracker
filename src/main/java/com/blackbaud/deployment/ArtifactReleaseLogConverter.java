package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactReleaseLogDetail;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactReleaseLogConverter {

    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactReleaseLogDetail toApi(ArtifactReleaseLogEntity entity) {
        return entityMapper.mapIfNotNull(entity, ArtifactReleaseLogDetail.class);
    }

    public List<ArtifactReleaseLogDetail> toApiList(List<ArtifactReleaseLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
