package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ReleaseService {

    public static final String DEV_FOUNDATION = "pivotal-dev";
    public static final String DEV_SPACE = "dev-apps";

    public static final String PROD_FOUNDATION = "pivotal-prod1";
    public static final String PROD_SPACE = "prod1-apps";

    private static final List<String> nonReleasable = Arrays.asList("bluemoon-dojo",
                                                                    "bluemoon-dojo-ui",
                                                                    "data-pipeline-kafka-rest-proxy",
                                                                    "data-pipeline-performance-consumer",
                                                                    "data-pipeline-performance-producer",
                                                                    "data-pipeline-tests-endpoints",
                                                                    "mock-data-sync-api");

    @Autowired
    private ArtifactReleaseInfoService artifactReleaseInfoService;

    @Autowired
    private GitLogParserFactory gitLogParserFactory;

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs() {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactReleaseInfo> prodInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
        addStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs(List<ArtifactReleaseInfo> prodInfos) {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
        addStoriesAndDevelopers(releaseSummary);
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
            if (isReleasable(devInfo)){
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

    public void addStoriesAndDevelopers(TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (String artifactId : allArtifactReleaseInfos.keySet()) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleaseInfos.get(artifactId);
            GitLogParser parser = gitLogParserFactory.createParser(artifactId, artifactReleaseDiff);
            artifactReleaseDiff.setStories(parser.getStories());
            artifactReleaseDiff.setDevelopers(parser.getDevelopers());
        }
    }

}
