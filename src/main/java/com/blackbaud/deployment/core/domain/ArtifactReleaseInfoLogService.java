package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ArtifactReleaseInfoLogService {

    @Autowired
    ArtifactReleaseInfoLogRepository artifactReleaseInfoLogRepository;

    @Autowired
    private ArtifactInfoService artifactInfoService;

    @Autowired
    private ArtifactReleaseInfoConverter converter;

    @Transactional
    public ArtifactReleaseInfo save(ArtifactReleaseInfo artifactReleaseInfo, String foundation, String space) {
        ArtifactReleaseInfoLogEntity lastRelease = artifactReleaseInfoLogRepository.findFirstByArtifactIdOrderByReleaseVersionDesc(artifactReleaseInfo.getArtifactId());
        ArtifactReleaseInfoLogEntity releaseLog = ArtifactReleaseInfoLogEntity.builder()
                .artifactId(artifactReleaseInfo.getArtifactId())
                .buildVersion(artifactReleaseInfo.getBuildVersion())
                .releaseVersion(artifactReleaseInfo.getReleaseVersion())
                .prevBuildVersion(lastRelease == null ? null : lastRelease.getBuildVersion())
                .prevReleaseVersion(lastRelease == null ? null : lastRelease.getReleaseVersion())
                .foundation(foundation)
                .space(space)
                .deployer("")
                .build();
        artifactReleaseInfoLogRepository.save(releaseLog);
        artifactInfoService.create(artifactReleaseInfo.getArtifactId(), artifactReleaseInfo.getBuildVersion(), extractArtifactInfo(artifactReleaseInfo));
        return artifactReleaseInfo;
    }

    public ArtifactReleaseInfo findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(artifactReleaseInfoLogRepository.findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(foundation, space, artifactId));
    }

    public List<ArtifactReleaseInfo> findManyByFoundationAndSpaceAndArtifactIdsIn(String foundation, String space, List<String> artifactIds) {
        return converter.toApiList(artifactReleaseInfoLogRepository.findManyByArtifactIdInAndFoundationAndSpace(artifactIds, foundation, space));
    }

    public List<ArtifactReleaseInfo> findManyByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(artifactReleaseInfoLogRepository.findManyByFoundationAndSpace(foundation, space));
    }
    private ArtifactInfoEntity extractArtifactInfo(ArtifactReleaseInfo artifactReleaseInfo) {
        return ArtifactInfoEntity.builder()
                .artifactId(artifactReleaseInfo.getArtifactId())
                .buildVersion(artifactReleaseInfo.getBuildVersion())
                .gitSha(artifactReleaseInfo.getGitSha())
                .build();
    }
}
