package com.blackbaud.deployment;

import com.blackbaud.deployment.api.GitLogInfo;
import com.blackbaud.deployment.core.domain.GitLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitLogConverter {

    private EntityMapper entityMapper = new EntityMapper();

    public GitLogInfo toApi(GitLogEntity entity) {
        if (entity == null) {
            return null;
        }
        return entityMapper.mapIfNotNull(entity, GitLogInfo.class);
    }

    public List<GitLogInfo> toApiList(List<GitLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }

}
