package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogReportResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ArtifactReleaseDiffConverter {

    public ArtifactReleaseDiff toApi(ArtifactReleaseLogReportResult reportResult) {
        ArtifactRelease currentRelease = new ArtifactRelease(reportResult.getArtifactId(), reportResult.getBuildVersion(), reportResult.getReleaseVersion(), reportResult.getGitSha());
        ArtifactRelease previousRelease = new ArtifactRelease(reportResult.getArtifactId(), reportResult.getPrevBuildVersion(), reportResult.getPrevReleaseVersion(), reportResult.getPrevGitSha());
        return ArtifactReleaseDiff.builder()
                .artifactId(reportResult.getArtifactId())
                .currentRelease(currentRelease)
                .prevRelease(previousRelease)
                .space(reportResult.getSpace())
                .foundation(reportResult.getFoundation())
                .releaseDate(convertReleaseVersionToDate(reportResult.getReleaseVersion()))
                .deployer(reportResult.getDeployer())
                .stories(reportResult.getStories())
                .developers(reportResult.getDevelopers())
                .build();
    }

    public List<ArtifactReleaseDiff> toApiList(List<ArtifactReleaseLogReportResult> reportResultList) {
        return reportResultList.stream()
                .map(this::toApi)
                .collect(Collectors.toList());
    }

    public ArtifactReleaseDiff toApi(ArtifactReleaseLogEntity entity, ArtifactInfoEntity currentInfo, ArtifactInfoEntity prevInfo) {
        ArtifactRelease currentRelease = new ArtifactRelease(currentInfo.getArtifactId(), currentInfo.getBuildVersion(), entity.getReleaseVersion(), currentInfo.getGitSha());
        ArtifactRelease prevRelease = toArtifactRelease(prevInfo, entity.getPrevReleaseVersion());
        return ArtifactReleaseDiff.builder()
                .currentRelease(currentRelease)
                .prevRelease(prevRelease)
                .space(entity.getSpace())
                .foundation(entity.getFoundation())
                .releaseDate(convertReleaseVersionToDate(entity.getReleaseVersion()))
                .deployer(entity.getDeployer()).build();
    }

    private ArtifactRelease toArtifactRelease(ArtifactInfoEntity artifactInfoEntity, String releaseVersion) {
        ArtifactRelease artifactRelease = new ArtifactRelease();
        if (artifactInfoEntity != null) {
            artifactRelease = new ArtifactRelease(artifactInfoEntity.getArtifactId(), artifactInfoEntity.getBuildVersion(), releaseVersion, getPrevGitSha(artifactInfoEntity));
        }
        return artifactRelease;
    }

    private String getArtifactId(ArtifactRelease currentRelease, ArtifactRelease prevRelease) {
        return currentRelease == null ? prevRelease.getArtifactId() : currentRelease.getArtifactId();
    }

    private String getPrevGitSha(ArtifactInfoEntity releaseInfo) {
        String prevSha = null;
        if (releaseInfo != null) {
            prevSha = releaseInfo.getGitSha();
        } else {
            log.warn("Release log has previous build version for artifact info that doesn't exist!!");
        }
        return prevSha;
    }

    private ZonedDateTime convertReleaseVersionToDate(String releaseVersion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssz");
        try {
            return ZonedDateTime.parse(releaseVersion + "UTC", formatter);
        } catch (Exception ex) {
            log.warn("Unparsable release version!! should be yyyyMMdd_hhmmss!! Got {}!!!", releaseVersion);
            return null;
        }
    }


}
