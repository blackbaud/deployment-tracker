package com.blackbaud.deployment.client;

import com.blackbaud.rest.client.CrudClient;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.api.DeploymentInfo;

import java.util.List;

public class DeploymentTrackerClient extends CrudClient<DeploymentInfo, DeploymentTrackerClient> {

    public DeploymentTrackerClient(String baseUrl) {
        super(baseUrl, ResourcePaths.DEPLOYMENT_TRACKER_PATH, DeploymentInfo.class);
    }

    public DeploymentInfo update(String foundation, String space, DeploymentInfo info) {
        return crudClientRequest.path(foundation).path(space)
                .updateWithPut(info);
    }

    public DeploymentInfo find(String foundation, String space, String artifactId) {
        return crudClientRequest.path(foundation).path(space).path(artifactId)
                .find();
    }

    public List<DeploymentInfo> findAllInSpace(String foundation, String space) {
        return crudClientRequest.path(foundation).path(space)
                .findMany();
    }

}
