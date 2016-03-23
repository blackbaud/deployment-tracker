package com.blackbaud.deploymentstatus.client;

import com.blackbaud.rest.client.CrudClient;
import com.blackbaud.rest.client.CrudClientRequest;
import com.blackbaud.deploymentstatus.api.ResourcePaths;
import com.blackbaud.deploymentstatus.api.DeploymentStatus;

import java.util.List;

public class DeploymentStatusClient extends CrudClient<DeploymentStatus, DeploymentStatusClient> {

    public DeploymentStatusClient(String baseUrl) {
        super(baseUrl, ResourcePaths.DEPLOYMENT_STATUS_PATH, DeploymentStatus.class);
    }

}
