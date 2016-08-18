package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.ArtifactReleaseLogConverter;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.googlecode.javaewah.IteratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
public class ArtifactReleaseInfoLogService {

    @Autowired
    ArtifactReleaseInfoLogRepository artifactReleaseInfoLogRepository;

    @Autowired
    private ArtifactInfoService artifactInfoService;

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private ArtifactReleaseInfoConverter converter;

    @Autowired
    private ArtifactReleaseLogConverter logConverter;
    
    @Autowired
    GitLogRepository gitLogRepository;

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

    public List<ArtifactReleaseLog> findAll() {
        List<ArtifactReleaseInfoLogEntity> artifactReleaseLogEntityInfo = (List<ArtifactReleaseInfoLogEntity>) artifactReleaseInfoLogRepository.findAll();
        List<ArtifactReleaseLog> artifactReleaseLogList = new ArrayList<>();
        for(ArtifactReleaseInfoLogEntity releaseLog: artifactReleaseLogEntityInfo) {
            ArtifactReleaseLog artifactReleaseLog = logConverter.toApi(releaseLog);
            String currentSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getBuildVersion()).getGitSha();
            String prevSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getPrevBuildVersion()).getGitSha();
            addStoriesAndDevelopersFromDb(artifactReleaseLog, currentSha, prevSha);
            artifactReleaseLogList.add(artifactReleaseLog);
        }
        return artifactReleaseLogList;
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

    private void addStoriesAndDevelopersFromDb(ArtifactReleaseLog artifactReleaseLog, String currentSha, String prevSha) {
        Set<String> stories = new TreeSet<>();
        Set<String> developers = new TreeSet<>();

        fetchGitLogEntries(artifactReleaseLog.getArtifactId(), currentSha, prevSha).forEach(gitLog -> {
            developers.add(gitLog.author);
            if (gitLog.storyId != null) {
                stories.add(gitLog.storyId);
            }
        });

        artifactReleaseLog.setStories(stories);
        artifactReleaseLog.setDevelopers(developers);

        log.debug("addStoriesAndDevelopersFromDb got stories={} and developers={}", stories, developers);
    }

    private List<GitLogEntity> fetchGitLogEntries(String artifactId, String currentSha, String prevSha) {
        if (currentSha == null) {
            return Collections.emptyList();
        } else if (prevSha == null) {
            return gitLogRepository.fetchGitLogUntilSha(artifactId, currentSha);
        } else {
            return gitLogRepository.fetchGitLogForCurrentAndPreviousGitShas(artifactId, currentSha, prevSha);
        }
    }
}
