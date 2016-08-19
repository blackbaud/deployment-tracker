package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.ArtifactReleaseLogConverter;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.api.ArtifactReleaseLogDetail;
import com.blackbaud.deployment.core.domain.git.GitLogService;
import com.blackbaud.deployment.core.domain.git.StoriesAndDevelopers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private ArtifactReleaseLogConverter logConverter;

    @Autowired
    GitLogService gitLogService;

    @Transactional
    public ArtifactReleaseInfo save(ArtifactReleaseInfo artifactReleaseInfo, String foundation, String space) {
        artifactInfoService.create(artifactReleaseInfo.getArtifactId(), artifactReleaseInfo.getBuildVersion(), extractArtifactInfo(artifactReleaseInfo));
        ArtifactReleaseLogEntity mostRecentRelease = artifactReleaseLogRepository.findFirstByArtifactIdOrderByReleaseVersionDesc(artifactReleaseInfo.getArtifactId());
        ArtifactReleaseLogEntity newRelease = ArtifactReleaseLogEntity.builder()
                .artifactId(artifactReleaseInfo.getArtifactId())
                .buildVersion(artifactReleaseInfo.getBuildVersion())
                .releaseVersion(artifactReleaseInfo.getReleaseVersion())
                .prevBuildVersion(mostRecentRelease == null ? null : mostRecentRelease.getBuildVersion())
                .prevReleaseVersion(mostRecentRelease == null ? null : mostRecentRelease.getReleaseVersion())
                .foundation(foundation)
                .space(space)
                .deployer("")
                .build();
        artifactReleaseLogRepository.save(newRelease);
        return artifactReleaseInfo;
    }

    public List<ArtifactReleaseLogDetail> findAll() {
        List<ArtifactReleaseLogEntity> artifactReleaseLogEntityInfo = (List<ArtifactReleaseLogEntity>) artifactReleaseLogRepository.findAll();
        List<ArtifactReleaseLogDetail> artifactReleaseLogDetailList = new ArrayList<>();
        for (ArtifactReleaseLogEntity releaseLog : artifactReleaseLogEntityInfo) {
            ArtifactReleaseLogDetail artifactReleaseLogDetail = logConverter.toApi(releaseLog);
            setReleaseDateFromReleaseVersion(artifactReleaseLogDetail);
            String currentSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getBuildVersion()).getGitSha();
            String prevSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getPrevBuildVersion()).getGitSha();
            addStoriesAndDevelopersFromDb(artifactReleaseLogDetail, currentSha, prevSha);
            artifactReleaseLogDetailList.add(artifactReleaseLogDetail);
        }
        return artifactReleaseLogDetailList;
    }

    private void setReleaseDateFromReleaseVersion(ArtifactReleaseLogDetail artifactReleaseLogDetail) {
        artifactReleaseLogDetail.setReleaseDate(convertReleaseVersionToDate(artifactReleaseLogDetail.getReleaseVersion()));
    }

    private ZonedDateTime convertReleaseVersionToDate(String releaseVersion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssz");
        try {
            return ZonedDateTime.parse(releaseVersion+"UTC", formatter);
        } catch (Exception ex) {
            log.warn("Unparsable release version!! should be yyyyMMdd_hhmmss!! Got {}!!!", releaseVersion);
            return null;
        }
    }

    public ArtifactReleaseInfo findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(artifactReleaseLogRepository.findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(foundation, space, artifactId));
    }

    public List<ArtifactReleaseInfo> findManyByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(artifactReleaseLogRepository.findManyByFoundationAndSpace(foundation, space));
    }

    private ArtifactInfoEntity extractArtifactInfo(ArtifactReleaseInfo artifactReleaseInfo) {
        return ArtifactInfoEntity.builder()
                .artifactId(artifactReleaseInfo.getArtifactId())
                .buildVersion(artifactReleaseInfo.getBuildVersion())
                .gitSha(artifactReleaseInfo.getGitSha())
                .build();
    }

    private void addStoriesAndDevelopersFromDb(ArtifactReleaseLogDetail artifactReleaseLogDetail, String currentSha, String prevSha) {
        StoriesAndDevelopers storiesAndDevelopers = gitLogService.getStoriesAndDevelopers(artifactReleaseLogDetail.getArtifactId(), prevSha, currentSha);

        artifactReleaseLogDetail.setStories(storiesAndDevelopers.getStories());
        artifactReleaseLogDetail.setDevelopers(storiesAndDevelopers.getDevelopers());
    }
}
