package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.core.domain.ArtifactInfoService;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogReportResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ArtifactReleaseDiffConverter {

    @Autowired
    private ArtifactInfoService artifactInfoService;

    public ArtifactReleaseDiff toApi(ArtifactReleaseLogReportResult reportResult) {
        ArtifactRelease currentRelease = getCurrentReleaseFromReport(reportResult);
        ArtifactRelease previousRelease = getPrevReleaseFromReport(reportResult);
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

    private ArtifactRelease getPrevReleaseFromReport(ArtifactReleaseLogReportResult reportResult) {
        List<ArtifactInfo> prevDependencies = artifactInfoService.getDependencies(reportResult.getArtifactId(), reportResult.getPrevBuildVersion());

        return ArtifactRelease.builder()
                .artifactId(reportResult.getArtifactId())
                .buildVersion(reportResult.getPrevBuildVersion())
                .releaseVersion(reportResult.getPrevReleaseVersion())
                .gitSha(reportResult.getPrevGitSha())
                .deployJobUrl(reportResult.getPrevDeployJobUrl())
                .dependencies(prevDependencies == null ? null : prevDependencies)
                .build();
    }

    private ArtifactRelease getCurrentReleaseFromReport(ArtifactReleaseLogReportResult reportResult) {
        List<ArtifactInfo> currentDependencies = artifactInfoService.getDependencies(reportResult.getArtifactId(), reportResult.getBuildVersion());
        return ArtifactRelease.builder()
                .artifactId(reportResult.getArtifactId())
                .buildVersion(reportResult.getBuildVersion())
                .releaseVersion(reportResult.getReleaseVersion())
                .gitSha(reportResult.getGitSha())
                .deployJobUrl(reportResult.getDeployJobUrl())
                .dependencies(currentDependencies == null ? null : currentDependencies)
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
