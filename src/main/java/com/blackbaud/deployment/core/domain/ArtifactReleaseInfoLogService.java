package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactReleaseInfoConverter;
import com.blackbaud.deployment.ArtifactReleaseLogConverter;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.blackbaud.deployment.core.domain.git.GitLogService;
import com.blackbaud.deployment.core.domain.git.StoriesAndDevelopers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
    GitLogService gitLogService;

    @Transactional
    public ArtifactReleaseInfo save(ArtifactReleaseInfo artifactReleaseInfo, String foundation, String space) {
        artifactInfoService.create(artifactReleaseInfo.getArtifactId(), artifactReleaseInfo.getBuildVersion(), extractArtifactInfo(artifactReleaseInfo));
        ArtifactReleaseInfoLogEntity mostRecentRelease = artifactReleaseInfoLogRepository.findFirstByArtifactIdOrderByReleaseVersionDesc(artifactReleaseInfo.getArtifactId());
        ArtifactReleaseInfoLogEntity newRelease = ArtifactReleaseInfoLogEntity.builder()
                .artifactId(artifactReleaseInfo.getArtifactId())
                .buildVersion(artifactReleaseInfo.getBuildVersion())
                .releaseVersion(artifactReleaseInfo.getReleaseVersion())
                .prevBuildVersion(mostRecentRelease == null ? null : mostRecentRelease.getBuildVersion())
                .prevReleaseVersion(mostRecentRelease == null ? null : mostRecentRelease.getReleaseVersion())
                .foundation(foundation)
                .space(space)
                .deployer("")
                .build();
        artifactReleaseInfoLogRepository.save(newRelease);
        return artifactReleaseInfo;
    }

    public List<ArtifactReleaseLog> findAll() {
        List<ArtifactReleaseInfoLogEntity> artifactReleaseLogEntityInfo = (List<ArtifactReleaseInfoLogEntity>) artifactReleaseInfoLogRepository.findAll();
        List<ArtifactReleaseLog> artifactReleaseLogList = new ArrayList<>();
        for(ArtifactReleaseInfoLogEntity releaseLog: artifactReleaseLogEntityInfo) {
            ArtifactReleaseLog artifactReleaseLog = logConverter.toApi(releaseLog);
            setReleaseDateFromReleaseVersion(artifactReleaseLog);
            String currentSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getBuildVersion()).getGitSha();
            String prevSha = artifactInfoRepository.findOneByArtifactIdAndBuildVersion(releaseLog.getArtifactId(), releaseLog.getPrevBuildVersion()).getGitSha();
            addStoriesAndDevelopersFromDb(artifactReleaseLog, currentSha, prevSha);
            artifactReleaseLogList.add(artifactReleaseLog);
        }
        return artifactReleaseLogList;
    }

    private void setReleaseDateFromReleaseVersion(ArtifactReleaseLog artifactReleaseLog) {
        artifactReleaseLog.setReleaseDate(convertReleaseVersionToDate(artifactReleaseLog.getReleaseVersion()));
    }

    private ZonedDateTime convertReleaseVersionToDate(String releaseVersion) {
        String[] tokens = releaseVersion.split("_");
        if (tokens.length != 2 && tokens[0].length() != 8 && tokens[1].length() != 6) {
            log.warn("Unparsable release version!! Got {}!!!", releaseVersion);
            return null;
        } else {
            LocalDate date;
            LocalTime time;
            try {
                date = parseReleaseDate(tokens[0]);
                time = parseReleaseTime(tokens[1]);
            } catch (NumberFormatException ex) {
                log.warn("Unparsable release version!! Not a number!! Got {}!!!", releaseVersion);
                return null;
            }
            return ZonedDateTime.of(date, time, ZoneId.of("UTC"));
        }
    }

    private LocalDate parseReleaseDate(String token) {
        return LocalDate.of(Integer.parseInt(token.substring(0,4)),
                        Integer.parseInt(token.substring(4,6)),
                        Integer.parseInt(token.substring(6,8)));
    }
    private LocalTime parseReleaseTime(String token) {
        return LocalTime.of(Integer.parseInt(token.substring(0,2)),
                Integer.parseInt(token.substring(2,4)),
                Integer.parseInt(token.substring(4,6)));
    }

    public ArtifactReleaseInfo findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(artifactReleaseInfoLogRepository.findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(foundation, space, artifactId));
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
        StoriesAndDevelopers storiesAndDevelopers = gitLogService.getStoriesAndDevelopers(artifactReleaseLog.getArtifactId(), prevSha, currentSha);

        artifactReleaseLog.setStories(storiesAndDevelopers.getStories());
        artifactReleaseLog.setDevelopers(storiesAndDevelopers.getDevelopers());
    }
}
