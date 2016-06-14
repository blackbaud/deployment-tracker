package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.api.DeploymentDiff;
import com.blackbaud.deployment.api.DeploymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ReleaseService {

    public static final String DEV_FOUNDATION = "pivotal-dev";
    public static final String DEV_SPACE = "dev-apps";

    public static final String PROD_FOUNDATION = "pivotal-prod1";
    public static final String PROD_SPACE = "prod1-apps";

    @Autowired
    private DeploymentInfoService deploymentInfoService;

    @Autowired
    private GitLogParserFactory gitLogParserFactory;

    public Map<String, DeploymentDiff> createDeploymentDiffs() {
        List<DeploymentInfo> devInfos = deploymentInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<DeploymentInfo> prodInfos = deploymentInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);
        TreeMap<String, DeploymentDiff> releaseSummary = combineDeploymentInfos(devInfos, prodInfos);
        addStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    public Map<String, DeploymentDiff> createDeploymentDiffs(List<DeploymentInfo> prodInfos) {
        List<DeploymentInfo> devInfos = deploymentInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        TreeMap<String, DeploymentDiff> releaseSummary = combineDeploymentInfos(devInfos, prodInfos);
        addStoriesAndDevelopers(releaseSummary);
        return releaseSummary;
    }

    private TreeMap<String, DeploymentDiff> combineDeploymentInfos(List<DeploymentInfo> devInfos, List<DeploymentInfo> prodInfos) {
        TreeMap<String, DeploymentDiff> allDeploymentInfos = new TreeMap<>();
        addDevDeploymentInfos(devInfos, allDeploymentInfos);
        addProdDeploymentInfo(prodInfos, allDeploymentInfos);
        return allDeploymentInfos;
    }

    public void addDevDeploymentInfos(List<DeploymentInfo> devInfos, Map<String, DeploymentDiff> allDeploymentInfos) {
        for (DeploymentInfo devInfo : devInfos) {
            allDeploymentInfos.put(devInfo.getArtifactId(),
                                   DeploymentDiff.builder()
                                           .dev(devInfo)
                                           .build()
            );
        }
    }

    private void addProdDeploymentInfo(List<DeploymentInfo> prodInfos, Map<String, DeploymentDiff> allDeploymentInfos) {
        for (DeploymentInfo prodInfo : prodInfos) {
            DeploymentDiff deploymentInfos = allDeploymentInfos.get(prodInfo.getArtifactId());
            if (deploymentInfos == null) {
                allDeploymentInfos.put(prodInfo.getArtifactId(),
                                       DeploymentDiff.builder()
                                               .prod(prodInfo)
                                               .build());
            } else {
                deploymentInfos.setProd(prodInfo);
            }
        }
    }

    public void addStoriesAndDevelopers(TreeMap<String, DeploymentDiff> allDeploymentInfos) {
        for (String artifactId : allDeploymentInfos.keySet()) {
            DeploymentDiff deploymentDiff = allDeploymentInfos.get(artifactId);
            GitLogParser parser = gitLogParserFactory.createParser(artifactId, deploymentDiff.getProdSha(), deploymentDiff.getDevSha());
            deploymentDiff.setStories(parser.getStories());
            deploymentDiff.setDevelopers(parser.getDevelopers());
        }
    }
}
