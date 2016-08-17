package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactReleaseInfoLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtifactReleaseInfoConverter {

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    private EntityMapper entityMapper = new EntityMapper();

    public ArtifactReleaseInfoEntity toEntity(ArtifactReleaseInfo info, String foundation, String space) {
        ArtifactReleaseInfoEntity entity = entityMapper.mapIfNotNull(info, ArtifactReleaseInfoEntity.class);
        entity.setFoundation(foundation);
        entity.setSpace(space);
        return entity;
    }

    public ArtifactReleaseInfo toApi(ArtifactReleaseInfoLogEntity entity) {
        if (entity == null) {
            return null;
        }
        String gitSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(entity.getArtifactId(), entity.getBuildVersion()).getGitSha();
        return ArtifactReleaseInfo.builder()
                .artifactId(entity.getArtifactId())
                .buildVersion(entity.getBuildVersion())
                .releaseVersion(entity.getReleaseVersion())
                .gitSha(gitSha)
                .build();
    }

    public List<ArtifactReleaseInfo> toApiList(List<ArtifactReleaseInfoLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
