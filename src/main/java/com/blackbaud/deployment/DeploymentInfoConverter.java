package com.blackbaud.deployment;

import com.blackbaud.deployment.api.DeploymentInfo;
import com.blackbaud.deployment.core.domain.DeploymentInfoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeploymentInfoConverter {
    private EntityMapper entityMapper = new EntityMapper();

    public DeploymentInfoEntity toEntity(DeploymentInfo info, String foundation, String space) {
        DeploymentInfoEntity entity = entityMapper.mapIfNotNull(info, DeploymentInfoEntity.class);
        entity.setFoundation(foundation);
        entity.setSpace(space);
        return entity;
    }

    public DeploymentInfo toApi(DeploymentInfoEntity entity) {
        if (entity == null) {
            return null;
        }
        return entityMapper.mapIfNotNull(entity, DeploymentInfo.class);
    }

    public List<DeploymentInfo> toApiList(List<DeploymentInfoEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
