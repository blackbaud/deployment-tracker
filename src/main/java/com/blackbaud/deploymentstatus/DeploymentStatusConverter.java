package com.blackbaud.deploymentstatus;

import com.blackbaud.deploymentstatus.api.DeploymentStatus;
import com.blackbaud.deploymentstatus.core.domain.DeploymentStatusEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeploymentStatusConverter {
    private EntityMapper entityMapper = new EntityMapper();

    public DeploymentStatusEntity toEntity(DeploymentStatus status) {
        return entityMapper.mapIfNotNull(status, DeploymentStatusEntity.class);
    }

    public List<DeploymentStatusEntity> toEntityList(Iterable<DeploymentStatus> statusList) {
        return entityMapper.mapList(statusList, this::toEntity);
    }

    public DeploymentStatus toApi(DeploymentStatusEntity entity) {
        if (entity == null) {
            return null;
        }
        return entityMapper.mapIfNotNull(entity, DeploymentStatus.class);
    }

    public List<DeploymentStatus> toApiList(List<DeploymentStatusEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
