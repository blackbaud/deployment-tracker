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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Component
@Slf4j
public class ReleaseService {

    public static final String DEV_FOUNDATION = "oscf-dev";
    public static final String DEV_SPACE = "dev-apps";

    public static final String PROD_FOUNDATION = "oscf-prod";
    public static final String PROD_SPACE = "prod-apps";

    private static final List<String> nonReleasable = Arrays.asList("bluemoon-dojo",
                                                                    "bluemoon-dojo-ui",
                                                                    "data-pipeline-kafka-rest-proxy",
                                                                    "data-pipeline-performance-consumer",
                                                                    "data-pipeline-performance-producer",
                                                                    "data-pipeline-tests-endpoints",
                                                                    "mock-data-sync-api",
                                                                    "kafka-oauth-sasl-provider",
                                                                    "search-adapter-poc",
                                                                    "skyshell",
                                                                    "skyshell2",
                                                                    "elasticsearch-loader-poc",
                                                                    "solr-loader-poc",
                                                                    "search-component",
                                                                    "segmentation-component",
                                                                    "notifications-component");

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
        List<ArtifactRelease> devArtifactReleases = artifactReleaseLogService.findMostRecentOfEachArtifactByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactRelease> prodArtifactReleases = artifactReleaseLogService.findMostRecentOfEachArtifactByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleases(devArtifactReleases, prodArtifactReleases);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs(List<ArtifactRelease> prodArtifactReleases) {
        List<ArtifactRelease> devArtifactReleases = artifactReleaseLogService.findMostRecentOfEachArtifactByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        addDependenciesToProdReleases(prodArtifactReleases);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleases(devArtifactReleases, prodArtifactReleases);
        addAllStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    private void addDependenciesToProdReleases(List<ArtifactRelease> prodArtifactReleases) {
        prodArtifactReleases.stream()
                .filter(release -> "bluemoon-ui".equals(release.getArtifactId()))
                .forEach(release -> release.setDependencies(artifactInfoService.getDependencies(release)));
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffsForReleasePlanArtifacts(List<ArtifactRelease> prodArtifactReleases) {
        ReleasePlanEntity releasePlan = releasePlanService.getCurrentReleasePlan();
        if (releasePlan == null) {
            return Collections.emptyMap();
        }
        List<ArtifactRelease> releasePlanReleases = getReleasePlanReleasesForDiffing(releasePlan);
        addDependenciesToProdReleases(prodArtifactReleases);
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
                        .releaseVersion(null)
                        .dependencies(artifactInfoService.getDependencies(artifactInfo))
                        .build())
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
                                            .artifactId(devRelease.getArtifactId())
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
                                                    .artifactId(prodRelease.getArtifactId())
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

            StoriesAndDevelopers storiesAndDevelopersForDependencies = gitLogService.getStoriesAndDevelopersForDependencies(artifactReleaseDiff);
            artifactReleaseDiff.addStories(storiesAndDevelopersForDependencies.getStories());
            artifactReleaseDiff.addDevelopers(storiesAndDevelopersForDependencies.getDevelopers());
        }
    }

}
