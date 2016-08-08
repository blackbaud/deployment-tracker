package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

@Component
@Slf4j
public class ReleaseService {

    public static final String DEV_FOUNDATION = "pivotal-dev";
    public static final String DEV_SPACE = "dev-apps";

    public static final String PROD_FOUNDATION = "pivotal-prod1";
    public static final String PROD_SPACE = "prod1-apps";

    private static final List<String> nonReleasable = Arrays.asList("bb-help",
                                                                    "bluemoon-dojo",
                                                                    "bluemoon-dojo-ui",
                                                                    "data-pipeline-kafka-rest-proxy",
                                                                    "data-pipeline-performance-consumer",
                                                                    "data-pipeline-performance-producer",
                                                                    "data-pipeline-tests-endpoints",
                                                                    "mock-data-sync-api",
                                                                    "kafka-oauth-sasl-provider");

    @Autowired
    private ArtifactReleaseInfoService artifactReleaseInfoService;

    @Autowired
    ArtifactInfoService artifactInfoService;

    @Autowired
    ArtifactInfoConverter artifactInfoConverter;

    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    GitLogRepository gitLogRepository;

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs() {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactReleaseInfo> prodInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs(List<ArtifactReleaseInfo> prodInfos) {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    private TreeMap<String, ArtifactReleaseDiff> combineArtifactReleaseInfos(List<ArtifactReleaseInfo> devInfos, List<ArtifactReleaseInfo> prodInfos) {
        TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseInfos = new TreeMap<>();
        addDevArtifactReleaseInfos(devInfos, allArtifactReleaseInfos);
        addProdArtifactReleaseInfo(prodInfos, allArtifactReleaseInfos);
        return allArtifactReleaseInfos;
    }

    public void addDevArtifactReleaseInfos(List<ArtifactReleaseInfo> devInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (ArtifactReleaseInfo devInfo : devInfos) {
            if (isReleasable(devInfo)) {
                allArtifactReleaseInfos.put(devInfo.getArtifactId(),
                                            ArtifactReleaseDiff.builder()
                                                    .dev(devInfo)
                                                    .build()
                );
            }
        }
    }

    private boolean isReleasable(ArtifactReleaseInfo artifactReleaseInfo) {
        return !nonReleasable.contains(artifactReleaseInfo.getArtifactId());
    }

    private void addProdArtifactReleaseInfo(List<ArtifactReleaseInfo> prodInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (ArtifactReleaseInfo prodInfo : prodInfos) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleaseInfos.get(prodInfo.getArtifactId());
            if (artifactReleaseDiff == null) {
                allArtifactReleaseInfos.put(prodInfo.getArtifactId(),
                                            ArtifactReleaseDiff.builder()
                                                    .prod(prodInfo)
                                                    .build());
            } else {
                artifactReleaseDiff.setProd(prodInfo);
            }
        }
    }

    public void addAllStoriesAndDevelopers(TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseDiffs) {
        for (ArtifactReleaseDiff artifactReleaseDiff : allArtifactReleaseDiffs.values()) {
            addStoriesAndDevelopersFromDb(artifactReleaseDiff);
        }
    }

    private void addStoriesAndDevelopersFromDb(ArtifactReleaseDiff artifactReleaseDiff) {
        Set<String> stories = new TreeSet<>();
        Set<String> developers = new TreeSet<>();

        fetchGitLogEntries(artifactReleaseDiff.getArtifactId(), artifactReleaseDiff.getDevSha(), artifactReleaseDiff.getProdSha()).forEach(gitLog -> {
            developers.add(gitLog.author);
            if (gitLog.storyId != null) {
                stories.add(gitLog.storyId);
            }
        });

        artifactReleaseDiff.setStories(stories);
        artifactReleaseDiff.setDevelopers(developers);

        log.debug("addStoriesAndDevelopersFromDb got stories={} and developers={}", stories, developers);
    }

    private List<GitLogEntity> fetchGitLogEntries(String artifactId, String devSha, String prodSha) {
        if (devSha == null) {
            return Collections.emptyList();
        } else if (prodSha == null) {
            return gitLogRepository.fetchGitLogUntilSha(artifactId, devSha);
        } else {
            return gitLogRepository.fetchGitLogForCurrentAndPreviousGitShas(artifactId, devSha, prodSha);
        }
    }

}
