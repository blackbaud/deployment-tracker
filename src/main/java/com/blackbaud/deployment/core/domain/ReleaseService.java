package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.api.DeploymentInfo;
import com.blackbaud.deployment.api.DevProdDeploymentInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ReleaseService {

    @Autowired
    private DeploymentInfoService deploymentInfoService;

    public static final String DEV_FOUNDATION = "pivotal-dev";
    public static final String DEV_SPACE = "dev-apps";

    public static final String PROD_FOUNDATION = "pivotal-prod1";
    public static final String PROD_SPACE = "prod1-apps";

    public Map<String, DevProdDeploymentInfos> getCurrentSummary() {
        List<DeploymentInfo> devDeploymentInfos = deploymentInfoService.findManyByFoundationAndSpace(DEV_FOUNDATION, DEV_SPACE);
        List<DeploymentInfo> prodDeploymentInfos = deploymentInfoService.findManyByFoundationAndSpace(PROD_FOUNDATION, PROD_SPACE);

        Map<String, DevProdDeploymentInfos> releaseSummary = getArtifactsWithBuildChanges(devDeploymentInfos, prodDeploymentInfos);
        return releaseSummary;
    }

    // TODO: complain if version in dev is older than prod
    private Map<String, DevProdDeploymentInfos> getArtifactsWithBuildChanges(List<DeploymentInfo> devDeploymentInfos,
                                                                             List<DeploymentInfo> prodDeploymentInfos) {

        Map<String, DevProdDeploymentInfos> releaseSummary = combineDeploymentInfos(devDeploymentInfos, prodDeploymentInfos);
        removeArtifactsWithNoChanges(releaseSummary);
        return releaseSummary;
    }

    private TreeMap<String, DevProdDeploymentInfos> combineDeploymentInfos(List<DeploymentInfo> devDeploymentInfos, List<DeploymentInfo> prodDeploymentInfos) {
        TreeMap<String, DevProdDeploymentInfos> allDeploymentInfos = new TreeMap<>();
        for (DeploymentInfo devDeploymentInfo : devDeploymentInfos) {
            allDeploymentInfos.put(devDeploymentInfo.getArtifactId(), new DevProdDeploymentInfos(devDeploymentInfo, null));
        }
        for (DeploymentInfo prodDeploymentInfo : prodDeploymentInfos) {
            DevProdDeploymentInfos deploymentInfos = allDeploymentInfos.get(prodDeploymentInfo.getArtifactId());
            setProdDeploymentInfo(allDeploymentInfos, prodDeploymentInfo, deploymentInfos);
        }
        return allDeploymentInfos;
    }

    private void setProdDeploymentInfo(TreeMap<String, DevProdDeploymentInfos> allDeploymentInfos, DeploymentInfo prodDeploymentInfo, DevProdDeploymentInfos deploymentInfos) {
        if (deploymentInfos == null){
            allDeploymentInfos.put(prodDeploymentInfo.getArtifactId(), new DevProdDeploymentInfos(null, prodDeploymentInfo));
        } else {
            deploymentInfos.setProd(prodDeploymentInfo);
        }
    }

    private void removeArtifactsWithNoChanges(Map<String, DevProdDeploymentInfos> allDeploymentInfos) {
        for (Map.Entry<String, DevProdDeploymentInfos> entry : allDeploymentInfos.entrySet()){
            DevProdDeploymentInfos deploymentInfos = entry.getValue();
            if (deploymentInfos.sameVersion()){
                allDeploymentInfos.remove(entry.getKey());
            }
        }
    }
}
