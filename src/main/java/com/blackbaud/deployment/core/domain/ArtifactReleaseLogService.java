package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseDiffConverter;
import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.core.domain.git.GitLogService;
import com.blackbaud.deployment.core.domain.git.StoriesAndDevelopers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ArtifactReleaseLogService {

    @Autowired
    ArtifactReleaseLogRepository artifactReleaseLogRepository;

    @Autowired
    private ArtifactInfoService artifactInfoService;

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private ArtifactReleaseInfoConverter converter;

    @Autowired
    private ArtifactReleaseDiffConverter diffConverter;

    @Autowired
    GitLogService gitLogService;

    @Transactional
    public ArtifactRelease save(ArtifactRelease artifactRelease, String foundation, String space) {
        artifactInfoService.create(artifactRelease.getArtifactId(), artifactRelease.getBuildVersion(), extractArtifactInfo(artifactRelease));
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

    public List<ArtifactReleaseDiff> findAll() {
        List<ArtifactReleaseLogEntity> artifactReleaseLogEntityInfo = (List<ArtifactReleaseLogEntity>) artifactReleaseLogRepository.findAll();
        List<ArtifactReleaseDiff> artifactReleaseLogDetailList = new ArrayList<>();
        for (ArtifactReleaseLogEntity releaseLog : artifactReleaseLogEntityInfo) {
            ArtifactInfoEntity currentInfo = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getBuildVersion());
            ArtifactInfoEntity prevInfo = null;
            if(releaseLog.getPrevBuildVersion() != null) {
                prevInfo = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getPrevBuildVersion());
            }
            ArtifactReleaseDiff artifactReleaseDiff = diffConverter.toApi(releaseLog, currentInfo, prevInfo);
            addStoriesAndDevelopersFromDb(artifactReleaseDiff, artifactReleaseDiff.getCurrentRelease().getGitSha(), artifactReleaseDiff.getPrevRelease().getGitSha());
            artifactReleaseLogDetailList.add(artifactReleaseDiff);
        }
        return artifactReleaseLogDetailList;
    }

    public ArtifactRelease findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(artifactReleaseLogRepository.findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(foundation, space, artifactId));
    }

    public List<ArtifactRelease> findManyByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(artifactReleaseLogRepository.findManyByFoundationAndSpace(foundation, space));
    }

    private ArtifactInfoEntity extractArtifactInfo(ArtifactRelease artifactRelease) {
        return ArtifactInfoEntity.builder()
                .artifactId(artifactRelease.getArtifactId())
                .buildVersion(artifactRelease.getBuildVersion())
                .gitSha(artifactRelease.getGitSha())
                .build();
    }

    private void addStoriesAndDevelopersFromDb(ArtifactReleaseDiff artifactReleaseDiff, String currentSha, String prevSha) {
        StoriesAndDevelopers storiesAndDevelopers = gitLogService.getStoriesAndDevelopers(artifactReleaseDiff.getArtifactId(), prevSha, currentSha);
        artifactReleaseDiff.setStories(storiesAndDevelopers.getStories());
        artifactReleaseDiff.setDevelopers(storiesAndDevelopers.getDevelopers());
    }
}
