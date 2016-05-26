package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.api.DeploymentInfo;
import com.blackbaud.deployment.api.DevProdDeploymentInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class ReleaseService {

    @Autowired
    private DeploymentInfoService deploymentInfoService;

    public static final String DEV_FOUNDATION = "pivotal-dev";
    public static final String DEV_SPACE = "dev-apps";

    public static final String PROD_FOUNDATION = "pivotal-prod1";
    public static final String PROD_SPACE = "prod1-apps";

    @Autowired
    private GithubRepositoryService repositoryService;

    public Map<String, DevProdDeploymentInfos> getCurrentSummary() {
        List<DeploymentInfo> devInfos = deploymentInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<DeploymentInfo> prodInfos = deploymentInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);

        return combineDeploymentInfos(devInfos, prodInfos);
    }

    private TreeMap<String, DevProdDeploymentInfos> combineDeploymentInfos(List<DeploymentInfo> devInfos, List<DeploymentInfo> prodInfos) {
        TreeMap<String, DevProdDeploymentInfos> allDeploymentInfos = new TreeMap<>();
        addDevDeploymentInfos(devInfos, allDeploymentInfos);
        addProdDeploymentInfo(prodInfos, allDeploymentInfos);
        addStoryLinks(allDeploymentInfos);
        return allDeploymentInfos;
    }

    public void addDevDeploymentInfos(List<DeploymentInfo> devInfos, TreeMap<String, DevProdDeploymentInfos> allDeploymentInfos) {
        for (DeploymentInfo devInfo : devInfos) {
            allDeploymentInfos.put(devInfo.getArtifactId(), new DevProdDeploymentInfos(devInfo, null, null));
        }
    }

    private void addProdDeploymentInfo(List<DeploymentInfo> prodInfos, TreeMap<String, DevProdDeploymentInfos> allDeploymentInfos) {
        for (DeploymentInfo prodInfo : prodInfos) {
            DevProdDeploymentInfos deploymentInfos = allDeploymentInfos.get(prodInfo.getArtifactId());
            if (deploymentInfos == null) {
                allDeploymentInfos.put(prodInfo.getArtifactId(), new DevProdDeploymentInfos(null, prodInfo, null));
            } else {
                deploymentInfos.setProd(prodInfo);
            }
        }
    }

    public void addStoryLinks(TreeMap<String, DevProdDeploymentInfos> allDeploymentInfos) {
        for (String artifactId : allDeploymentInfos.keySet()) {
            DevProdDeploymentInfos deploymentInfos = allDeploymentInfos.get(artifactId);
            if (deploymentInfos.hasBothShas()) {
                Set<String> stories = repositoryService.getStories(artifactId, deploymentInfos.getProdSha(), deploymentInfos.getDevSha());
                deploymentInfos.setStories(stories);
            }
        }
    }

}
