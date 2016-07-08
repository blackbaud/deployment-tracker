package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ArtifactReleaseInfoService {
    @Autowired
    ArtifactReleaseInfoRepository artifactReleaseInfoRepository;
    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private ArtifactReleaseInfoConverter converter;

    @Autowired
    private ArtifactInfoService artifactInfoService;

    @Transactional
    public ArtifactReleaseInfo save(ArtifactReleaseInfo artifactReleaseInfo, String foundation, String space) {
        ArtifactReleaseInfoEntity entity = converter.toEntity(artifactReleaseInfo, foundation, space);
        artifactInfoService.create(artifactReleaseInfo.getArtifactId(), artifactReleaseInfo.getBuildVersion(), extractArtifactInfo(entity));
        return converter.toApi(artifactReleaseInfoRepository.save(entity));
    }

    public ArtifactReleaseInfo findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(artifactReleaseInfoRepository.findOneByFoundationAndSpaceAndArtifactId(foundation, space, artifactId));
    }

    public List<ArtifactReleaseInfo> findManyByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(artifactReleaseInfoRepository.findManyByFoundationAndSpace(foundation, space));
    }

    private ArtifactInfoEntity extractArtifactInfo(ArtifactReleaseInfoEntity artifactReleaseInfoEntity) {
        return ArtifactInfoEntity.builder()
                .artifactId(artifactReleaseInfoEntity.getArtifactId())
                .buildVersion(artifactReleaseInfoEntity.getBuildVersion())
                .gitSha(artifactReleaseInfoEntity.getGitSha())
                .build();
    }

}