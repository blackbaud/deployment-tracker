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
        ArtifactRelease currentRelease = new ArtifactRelease(reportResult.getArtifactId(), reportResult.getBuildVersion(), reportResult.getReleaseVersion(), reportResult.getGitSha(), reportResult.getDeployJobUrl());
        ArtifactRelease previousRelease = new ArtifactRelease(reportResult.getArtifactId(), reportResult.getPrevBuildVersion(), reportResult.getPrevReleaseVersion(), reportResult.getPrevGitSha(), reportResult.getPrevDeployJobUrl());
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

    private ZonedDateTime convertReleaseVersionToDate(String releaseVersion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssz");
        try {
            return ZonedDateTime.parse(releaseVersion + "UTC", formatter);
        } catch (Exception ex) {
            log.warn("Unparsable release version!! should be yyyyMMdd_hhmmss! Got {}. Exception: {}", releaseVersion, ex);
            return null;
        }
    }

}
