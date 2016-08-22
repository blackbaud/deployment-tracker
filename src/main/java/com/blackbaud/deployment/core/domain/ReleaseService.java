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

    public static final String DEV_FOUNDATION = "pivotal-currentRelease";
    public static final String DEV_SPACE = "currentRelease-apps";

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
        List<ArtifactRelease> devInfos = artifactReleaseLogService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactRelease> prodInfos = artifactReleaseLogService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs(List<ArtifactRelease> prodInfos) {
        List<ArtifactRelease> devInfos = artifactReleaseLogService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffsForReleasePlanArtifacts(List<ArtifactRelease> prodInfos) {
        ReleasePlanEntity releasePlan = releasePlanService.getCurrentReleasePlan();
        if (releasePlan == null) {
            return Collections.emptyMap();
        }
        List<ArtifactRelease> releasePlanReleaseInfos = getReleasePlanReleaseInfosForDiffing(releasePlan);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(releasePlanReleaseInfos, prodInfos);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    private List<ArtifactRelease> getReleasePlanReleaseInfosForDiffing(ReleasePlanEntity releasePlan) {
        List<ArtifactInfo> releasePlanInfos = releasePlanConverter.toApi(releasePlan).getArtifacts();
        List<ArtifactRelease> devInfos = releasePlanInfos.stream().map(
                artifactInfo -> ArtifactRelease.builder()
                        .buildVersion(artifactInfo.getBuildVersion())
                        .artifactId(artifactInfo.getArtifactId())
                        .gitSha(artifactInfo.getGitSha())
                        .releaseVersion(FAKE_RELEASE_VERSION).build())
                .collect(Collectors.toList());
        return devInfos;
    }

    private TreeMap<String, ArtifactReleaseDiff> combineArtifactReleaseInfos(List<ArtifactRelease> devInfos, List<ArtifactRelease> prodInfos) {
        TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseInfos = new TreeMap<>();
        addDevArtifactReleaseInfos(devInfos, allArtifactReleaseInfos);
        addProdArtifactReleaseInfo(prodInfos, allArtifactReleaseInfos);
        return allArtifactReleaseInfos;
    }

    public void addDevArtifactReleaseInfos(List<ArtifactRelease> devInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (ArtifactRelease devInfo : devInfos) {
            if (isReleasable(devInfo)) {
                allArtifactReleaseInfos.put(devInfo.getArtifactId(),
                                            ArtifactReleaseDiff.builder()
                                                    .currentRelease(devInfo)
                                                    .build()
                );
            }
        }
    }

    private boolean isReleasable(ArtifactRelease artifactRelease) {
        return !nonReleasable.contains(artifactRelease.getArtifactId());
    }

    private void addProdArtifactReleaseInfo(List<ArtifactRelease> prodInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (ArtifactRelease prodInfo : prodInfos) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleaseInfos.get(prodInfo.getArtifactId());
            if (artifactReleaseDiff == null) {
                allArtifactReleaseInfos.put(prodInfo.getArtifactId(),
                                            ArtifactReleaseDiff.builder()
                                                    .prevRelease(prodInfo)
                                                    .build());
            } else {
                artifactReleaseDiff.setPrevRelease(prodInfo);
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
