package com.blackbaud.deployment;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class ArtifactReleaseDiffConverter {

    public ArtifactReleaseDiff toApi(ArtifactReleaseLogEntity entity, ArtifactInfoEntity currentInfo, ArtifactInfoEntity prevInfo) {
        ArtifactRelease currentRelease = new ArtifactRelease(currentInfo.getArtifactId(), currentInfo.getBuildVersion(), entity.getReleaseVersion(), currentInfo.getGitSha());
        ArtifactRelease prevRelease = new ArtifactRelease(prevInfo.getArtifactId(), prevInfo.getBuildVersion(), entity.getPrevReleaseVersion(), getPrevGitSha(prevInfo));
        ArtifactReleaseDiff releaseDiff = ArtifactReleaseDiff.builder()
                .currentRelease(currentRelease)
                .prevRelease(prevRelease)
                .space(entity.getSpace())
                .foundation(entity.getFoundation())
                .releaseDate(convertReleaseVersionToDate(entity.getReleaseVersion()))
                .deployer(entity.getDeployer()).build();
        return releaseDiff;
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
