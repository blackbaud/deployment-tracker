package com.blackbaud.deploymentstatus.client;

import com.blackbaud.rest.client.CrudClient;
import com.blackbaud.rest.client.CrudClientRequest;
import com.blackbaud.deploymentstatus.api.ResourcePaths;
import com.blackbaud.deploymentstatus.api.DeploymentStatus;

import javax.validation.Valid;
import java.util.List;

public class DeploymentStatusClient extends CrudClient<DeploymentStatus, DeploymentStatusClient> {

    public DeploymentStatusClient(String baseUrl) {
        super(baseUrl, ResourcePaths.DEPLOYMENT_STATUS_PATH, DeploymentStatus.class);
    }

    public DeploymentStatus update(String foundation, String space, DeploymentStatus status) {
        return crudClientRequest.path(foundation).path(space)
                .updateWithPut(status);
    }

    public DeploymentStatus find(String foundation, String space, String artifactId) {
        return crudClientRequest.path(foundation).path(space).path(artifactId)
                .find();
    }

    public List<DeploymentStatus> findAllInSpace(String foundation, String space) {
        return crudClientRequest.path(foundation).path(space)
                .findMany();
    }

}
