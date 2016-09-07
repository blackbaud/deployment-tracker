package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ArtifactRelease;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
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

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Transactional
    public ArtifactRelease save(ArtifactRelease artifactRelease, String foundation, String space) {
        artifactInfoService.create(extractArtifactInfo(artifactRelease));
        return saveArtifactReleaseLog(artifactRelease, foundation, space);
    }

    public ArtifactRelease saveArtifactReleaseLog(ArtifactRelease artifactRelease, String foundation, String space) {
        ArtifactReleaseLogEntity mostRecentRelease = artifactReleaseLogRepository.findFirstByArtifactIdAndFoundationAndSpaceOrderByReleaseVersionDesc(artifactRelease.getArtifactId(), foundation, space);
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

    private ArtifactInfo extractArtifactInfo(ArtifactRelease artifactRelease) {
        return ArtifactInfo.builder()
                .artifactId(artifactRelease.getArtifactId())
                .buildVersion(artifactRelease.getBuildVersion())
                .gitSha(artifactRelease.getGitSha())
                .build();
    }

    public ArtifactRelease findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(artifactReleaseLogRepository.findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(foundation, space, artifactId));
    }

    public List<ArtifactRelease> findAllByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(artifactReleaseLogRepository.findAllByFoundationAndSpaceOrderByArtifactIdAscReleaseVersionDesc(foundation, space));
    }

    public List<ArtifactRelease> findLatestOfEachArtifactByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(artifactReleaseLogRepository.findLatestOfEachArtifactByFoundationAndSpace(foundation, space));
    }

    public void remediationCreate(String foundation, String space, List<ArtifactRelease> artifactReleases) {
        artifactReleases.stream().forEach(artifactRelease -> {
            try {
                remediationCreate(foundation, space, artifactRelease);
            } catch (Exception ex) {
                log.debug("{}. Continuing with the rest.", ex.getMessage());
            }
        });
    }

    private void remediationCreate(String foundation, String space, ArtifactRelease artifactRelease) {
        ArtifactInfoEntity artifactInfo = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(artifactRelease.getArtifactId(), artifactRelease.getBuildVersion());
        if (artifactInfo == null) {
            throw new NotFoundException("ArtifactInfo for this release " + artifactRelease + " does not exist");
        }
        saveArtifactReleaseLog(artifactRelease, foundation, space);
    }
}
