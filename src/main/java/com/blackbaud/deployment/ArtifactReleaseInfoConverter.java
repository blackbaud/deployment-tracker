package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
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

    public ArtifactReleaseInfo toApi(ArtifactReleaseInfoLogEntity entity) {
        if (entity == null) {
            return null;
        }
        ArtifactReleaseInfo artifactReleaseInfo = entityMapper.mapIfNotNull(entity, ArtifactReleaseInfo.class);
        String gitSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(entity.getArtifactId(), entity.getBuildVersion()).getGitSha();
        artifactReleaseInfo.setGitSha(gitSha);
        return artifactReleaseInfo;
    }

    public List<ArtifactReleaseInfo> toApiList(List<ArtifactReleaseInfoLogEntity> entityList) {
        return entityList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
