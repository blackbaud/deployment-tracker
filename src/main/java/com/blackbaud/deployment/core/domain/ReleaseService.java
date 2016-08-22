package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.ReleasePlanConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.core.domain.git.GitLogService;
import com.blackbaud.deployment.core.domain.git.StoriesAndDevelopers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
    public static final String FAKE_RELEASE_VERSION = "12345";

    @Autowired
    private ArtifactReleaseLogService artifactReleaseLogService;

    @Autowired
    ArtifactInfoService artifactInfoService;

    @Autowired
    ArtifactInfoConverter artifactInfoConverter;

    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    GitLogService gitLogService;

    @Autowired
    private ReleasePlanService releasePlanService;

    @Autowired
    private ReleasePlanConverter releasePlanConverter;

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs() {
        List<ArtifactRelease> devArtifactReleases = artifactReleaseLogService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactRelease> prodArtifactReleases = artifactReleaseLogService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleases(devArtifactReleases, prodArtifactReleases);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs(List<ArtifactRelease> prodArtifactReleases) {
        List<ArtifactRelease> devArtifactReleases = artifactReleaseLogService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleases(devArtifactReleases, prodArtifactReleases);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffsForReleasePlanArtifacts(List<ArtifactRelease> prodArtifactReleases) {
        ReleasePlanEntity releasePlan = releasePlanService.getCurrentReleasePlan();
        if (releasePlan == null) {
            return Collections.emptyMap();
        }
        List<ArtifactRelease> releasePlanReleases = getReleasePlanReleasesForDiffing(releasePlan);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleases(releasePlanReleases, prodArtifactReleases);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    private List<ArtifactRelease> getReleasePlanReleasesForDiffing(ReleasePlanEntity releasePlan) {
        List<ArtifactInfo> releasePlanInfos = releasePlanConverter.toApi(releasePlan).getArtifacts();
        List<ArtifactRelease> devReleases = releasePlanInfos.stream().map(
                artifactInfo -> ArtifactRelease.builder()
                        .buildVersion(artifactInfo.getBuildVersion())
                        .artifactId(artifactInfo.getArtifactId())
                        .gitSha(artifactInfo.getGitSha())
                        .releaseVersion(FAKE_RELEASE_VERSION).build())
                .collect(Collectors.toList());
        return devReleases;
    }

    private TreeMap<String, ArtifactReleaseDiff> combineArtifactReleases(List<ArtifactRelease> devArtifactReleases, List<ArtifactRelease> prodArtifactReleases) {
        TreeMap<String, ArtifactReleaseDiff> allArtifactReleases = new TreeMap<>();
        addDevArtifactReleases(devArtifactReleases, allArtifactReleases);
        addProdArtifactReleases(prodArtifactReleases, allArtifactReleases);
        return allArtifactReleases;
    }

    public void addDevArtifactReleases(List<ArtifactRelease> devArtifactReleases, Map<String, ArtifactReleaseDiff> allArtifactReleases) {
        devArtifactReleases.stream().filter(devRelease -> isReleasable(devRelease)).forEach(devRelease -> {
            allArtifactReleases.put(devRelease.getArtifactId(),
                                    ArtifactReleaseDiff.builder()
                                            .currentRelease(devRelease)
                                            .build()
            );
        });
    }

    private boolean isReleasable(ArtifactRelease artifactRelease) {
        return !nonReleasable.contains(artifactRelease.getArtifactId());
    }

    private void addProdArtifactReleases(List<ArtifactRelease> prodArtifactReleases, Map<String, ArtifactReleaseDiff> allArtifactReleases) {
        for (ArtifactRelease prodRelease : prodArtifactReleases) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleases.get(prodRelease.getArtifactId());
            if (artifactReleaseDiff == null) {
                allArtifactReleases.put(prodRelease.getArtifactId(),
                                            ArtifactReleaseDiff.builder()
                                                    .prevRelease(prodRelease)
                                                    .build());
            } else {
                artifactReleaseDiff.setPrevRelease(prodRelease);
            }
        }
    }

    public void addAllStoriesAndDevelopers(TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseDiffs) {
        for (ArtifactReleaseDiff artifactReleaseDiff : allArtifactReleaseDiffs.values()) {
            StoriesAndDevelopers storiesAndDevelopers = gitLogService.getStoriesAndDevelopers(artifactReleaseDiff.getArtifactId(), artifactReleaseDiff.getProdSha(), artifactReleaseDiff.getDevSha());

            artifactReleaseDiff.setStories(storiesAndDevelopers.getStories());
            artifactReleaseDiff.setDevelopers(storiesAndDevelopers.getDevelopers());
        }
    }

}
