package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.core.domain.ArtifactDependencyEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactInfoConverter {
    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;
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

    public ArtifactInfo toApi(ArtifactDependencyEntity dependencyEntity) {
        if (dependencyEntity == null) {
            return null;
        }
        ArtifactInfoEntity dependencyInfo = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(dependencyEntity.getDependencyId(), dependencyEntity.getDependencyBuildVersion());

        ArtifactInfo artifactInfo = ArtifactInfo.builder()
                .artifactId(dependencyInfo.getArtifactId())
                .buildVersion(dependencyInfo.getBuildVersion())
                .gitSha(dependencyInfo.getGitSha())
                .build();
        return artifactInfo;
    }
}
