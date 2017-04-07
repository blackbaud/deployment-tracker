package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.core.domain.ArtifactDependencyEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.ArtifactInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactInfoConverter {

    @Autowired
    private ArtifactInfoService artifactInfoService;

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
        ArtifactInfo artifactInfo = entityMapper.mapIfNotNull(entity, ArtifactInfo.class);
        artifactInfo.addDependencies(artifactInfoService.getDependencies(artifactInfo));
        return artifactInfo;
    }

    public List<ArtifactInfo> toApiList(List<ArtifactInfoEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
