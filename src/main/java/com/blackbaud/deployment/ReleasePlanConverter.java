package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ReleasePlan;
import com.blackbaud.deployment.core.domain.ReleasePlanEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReleasePlanConverter {
    private EntityMapper entityMapper = new EntityMapper();

    public ReleasePlanEntity toEntity(ReleasePlan info) {
        return entityMapper.mapIfNotNull(info, ReleasePlanEntity.class);
    }

    public List<ReleasePlanEntity> toEntityList(Iterable<ReleasePlan> infoList) {
        return entityMapper.mapList(infoList, this::toEntity);
    }

    public ReleasePlan toApi(ReleasePlanEntity entity) {
        if (entity == null) {
            return null;
        }
        return entityMapper.mapIfNotNull(entity, ReleasePlan.class);
    }

    public List<ReleasePlan> toApiList(List<ReleasePlanEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
