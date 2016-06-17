package com.blackbaud.deployment.core.domain;

<<<<<<< HEAD
<<<<<<< HEAD
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
=======
import com.blackbaud.deployment.api.DeploymentDiff;
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
>>>>>>> f678692... more renaming
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

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs() {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactReleaseInfo> prodInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
=======
    public Map<String, DeploymentDiff> createDeploymentDiffs() {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactReleaseInfo> prodInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, DeploymentDiff> releaseSummary = combineDeploymentInfos(devInfos, prodInfos);
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
    public Map<String, ArtifactReleaseDiff> createDeploymentDiffs() {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactReleaseInfo> prodInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineDeploymentInfos(devInfos, prodInfos);
>>>>>>> f678692... more renaming
=======
    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs() {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<ArtifactReleaseInfo> prodInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
>>>>>>> e82996a... so much renaming
        addStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs(List<ArtifactReleaseInfo> prodInfos) {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
=======
    public Map<String, DeploymentDiff> createDeploymentDiffs(List<ArtifactReleaseInfo> prodInfos) {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, DeploymentDiff> releaseSummary = combineDeploymentInfos(devInfos, prodInfos);
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
    public Map<String, ArtifactReleaseDiff> createDeploymentDiffs(List<ArtifactReleaseInfo> prodInfos) {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineDeploymentInfos(devInfos, prodInfos);
>>>>>>> f678692... more renaming
=======
    public Map<String, ArtifactReleaseDiff> createArtifactReleaseDiffs(List<ArtifactReleaseInfo> prodInfos) {
        List<ArtifactReleaseInfo> devInfos = artifactReleaseInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, ArtifactReleaseDiff> releaseSummary = combineArtifactReleaseInfos(devInfos, prodInfos);
>>>>>>> e82996a... so much renaming
        addStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
    private TreeMap<String, ArtifactReleaseDiff> combineArtifactReleaseInfos(List<ArtifactReleaseInfo> devInfos, List<ArtifactReleaseInfo> prodInfos) {
        TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseInfos = new TreeMap<>();
        addDevArtifactReleaseInfos(devInfos, allArtifactReleaseInfos);
        addProdArtifactReleaseInfo(prodInfos, allArtifactReleaseInfos);
        return allArtifactReleaseInfos;
    }

    public void addDevArtifactReleaseInfos(List<ArtifactReleaseInfo> devInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
=======
    private TreeMap<String, DeploymentDiff> combineDeploymentInfos(List<ArtifactReleaseInfo> devInfos, List<ArtifactReleaseInfo> prodInfos) {
        TreeMap<String, DeploymentDiff> allDeploymentInfos = new TreeMap<>();
=======
    private TreeMap<String, ArtifactReleaseDiff> combineDeploymentInfos(List<ArtifactReleaseInfo> devInfos, List<ArtifactReleaseInfo> prodInfos) {
<<<<<<< HEAD
        TreeMap<String, ArtifactReleaseDiff> allDeploymentInfos = new TreeMap<>();
>>>>>>> f678692... more renaming
        addDevDeploymentInfos(devInfos, allDeploymentInfos);
        addProdDeploymentInfo(prodInfos, allDeploymentInfos);
        return allDeploymentInfos;
    }

<<<<<<< HEAD
    public void addDevDeploymentInfos(List<ArtifactReleaseInfo> devInfos, Map<String, DeploymentDiff> allDeploymentInfos) {
>>>>>>> 746a757... LUM-9138 first pass at renaming
        for (ArtifactReleaseInfo devInfo : devInfos) {
            if (isReleasable(devInfo)){
                allArtifactReleaseInfos.put(devInfo.getArtifactId(),
=======
    public void addDevDeploymentInfos(List<ArtifactReleaseInfo> devInfos, Map<String, ArtifactReleaseDiff> allDeploymentInfos) {
        for (ArtifactReleaseInfo devInfo : devInfos) {
            if (isReleasable(devInfo)){
                allDeploymentInfos.put(devInfo.getArtifactId(),
>>>>>>> f678692... more renaming
=======
=======
    private TreeMap<String, ArtifactReleaseDiff> combineArtifactReleaseInfos(List<ArtifactReleaseInfo> devInfos, List<ArtifactReleaseInfo> prodInfos) {
>>>>>>> e82996a... so much renaming
        TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseInfos = new TreeMap<>();
        addDevArtifactReleaseInfos(devInfos, allArtifactReleaseInfos);
        addProdArtifactReleaseInfo(prodInfos, allArtifactReleaseInfos);
        return allArtifactReleaseInfos;
    }

    public void addDevArtifactReleaseInfos(List<ArtifactReleaseInfo> devInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (ArtifactReleaseInfo devInfo : devInfos) {
            if (isReleasable(devInfo)){
                allArtifactReleaseInfos.put(devInfo.getArtifactId(),
>>>>>>> 5c342a4... more renaming
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

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
    private void addProdArtifactReleaseInfo(List<ArtifactReleaseInfo> prodInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (ArtifactReleaseInfo prodInfo : prodInfos) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleaseInfos.get(prodInfo.getArtifactId());
            if (artifactReleaseDiff == null) {
                allArtifactReleaseInfos.put(prodInfo.getArtifactId(),
                                       ArtifactReleaseDiff.builder()
=======
    private void addProdDeploymentInfo(List<ArtifactReleaseInfo> prodInfos, Map<String, DeploymentDiff> allDeploymentInfos) {
=======
    private void addProdDeploymentInfo(List<ArtifactReleaseInfo> prodInfos, Map<String, ArtifactReleaseDiff> allDeploymentInfos) {
>>>>>>> f678692... more renaming
        for (ArtifactReleaseInfo prodInfo : prodInfos) {
            ArtifactReleaseDiff deploymentInfos = allDeploymentInfos.get(prodInfo.getArtifactId());
            if (deploymentInfos == null) {
                allDeploymentInfos.put(prodInfo.getArtifactId(),
<<<<<<< HEAD
                                       DeploymentDiff.builder()
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
=======
    private void addProdDeploymentInfo(List<ArtifactReleaseInfo> prodInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
=======
    private void addProdArtifactReleaseInfo(List<ArtifactReleaseInfo> prodInfos, Map<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
>>>>>>> e82996a... so much renaming
        for (ArtifactReleaseInfo prodInfo : prodInfos) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleaseInfos.get(prodInfo.getArtifactId());
            if (artifactReleaseDiff == null) {
                allArtifactReleaseInfos.put(prodInfo.getArtifactId(),
>>>>>>> 5c342a4... more renaming
                                       ArtifactReleaseDiff.builder()
>>>>>>> f678692... more renaming
                                               .prod(prodInfo)
                                               .build());
            } else {
                artifactReleaseDiff.setProd(prodInfo);
            }
        }
    }

<<<<<<< HEAD
<<<<<<< HEAD
    public void addStoriesAndDevelopers(TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (String artifactId : allArtifactReleaseInfos.keySet()) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleaseInfos.get(artifactId);
=======
    public void addStoriesAndDevelopers(TreeMap<String, ArtifactReleaseDiff> allDeploymentInfos) {
        for (String artifactId : allDeploymentInfos.keySet()) {
            ArtifactReleaseDiff artifactReleaseDiff = allDeploymentInfos.get(artifactId);
>>>>>>> f678692... more renaming
=======
    public void addStoriesAndDevelopers(TreeMap<String, ArtifactReleaseDiff> allArtifactReleaseInfos) {
        for (String artifactId : allArtifactReleaseInfos.keySet()) {
            ArtifactReleaseDiff artifactReleaseDiff = allArtifactReleaseInfos.get(artifactId);
>>>>>>> 5c342a4... more renaming
            GitLogParser parser = gitLogParserFactory.createParser(artifactId, artifactReleaseDiff.getProdSha(), artifactReleaseDiff.getDevSha());
            artifactReleaseDiff.setStories(parser.getStories());
            artifactReleaseDiff.setDevelopers(parser.getDevelopers());
        }
    }
}
