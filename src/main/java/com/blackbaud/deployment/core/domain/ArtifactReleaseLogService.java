package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.api.ArtifactRelease;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class ArtifactReleaseLogService {

    @Autowired
    ArtifactReleaseLogRepository artifactReleaseLogRepository;

    @Autowired
    private ArtifactInfoService artifactInfoService;

    @Autowired
    private ArtifactReleaseInfoConverter converter;

    @Transactional
    public ArtifactRelease save(ArtifactRelease artifactRelease, String foundation, String space) {
        artifactInfoService.create(artifactRelease.getArtifactId(), artifactRelease.getBuildVersion(), extractArtifactInfo(artifactRelease));
        ArtifactReleaseLogEntity mostRecentRelease = artifactReleaseLogRepository.findFirstByArtifactIdOrderByReleaseVersionDesc(artifactRelease.getArtifactId());
        ArtifactReleaseLogEntity newRelease = ArtifactReleaseLogEntity.builder()
                .artifactId(artifactRelease.getArtifactId())
                .buildVersion(artifactRelease.getBuildVersion())
                .releaseVersion(artifactRelease.getReleaseVersion())
                .prevBuildVersion(mostRecentRelease == null ? null : mostRecentRelease.getBuildVersion())
                .prevReleaseVersion(mostRecentRelease == null ? null : mostRecentRelease.getReleaseVersion())
                .foundation(foundation)
                .space(space)
                .deployer("")
                .build();
        artifactReleaseLogRepository.save(newRelease);
        return artifactRelease;
    }
    
    private ArtifactInfoEntity extractArtifactInfo(ArtifactRelease artifactRelease) {
        return ArtifactInfoEntity.builder()
                .artifactId(artifactRelease.getArtifactId())
                .buildVersion(artifactRelease.getBuildVersion())
                .gitSha(artifactRelease.getGitSha())
                .build();
    }

    public ArtifactRelease findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(artifactReleaseLogRepository.findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(foundation, space, artifactId));
    }

    public List<ArtifactRelease> findLatestOfEachArtifactByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(artifactReleaseLogRepository.findLatestOfEachArtifactByFoundationAndSpace(foundation, space));
    }
}
