package com.blackbaud.deployment.client;

import com.blackbaud.rest.client.CrudClient;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.api.ArtifactReleaseInfo;

import java.util.List;

public class ArtifactReleaseInfoClient extends CrudClient<ArtifactReleaseInfo, ArtifactReleaseInfoClient> {

    public ArtifactReleaseInfoClient(String baseUrl) {
        super(baseUrl, ResourcePaths.DEPRECATED_DEPLOYMENT_INFO_PATH, ArtifactReleaseInfo.class);
    }

    public ArtifactReleaseInfo update(String foundation, String space, ArtifactReleaseInfo info) {
        return crudClientRequest.path(foundation).path(space)
                .updateWithPut(info);
    }

    public ArtifactReleaseInfo find(String foundation, String space, String artifactId) {
        return crudClientRequest.path(foundation).path(space).path(artifactId)
                .find();
    }

    public List<ArtifactReleaseInfo> findAllInSpace(String foundation, String space) {
        return crudClientRequest.path(foundation).path(space)
                .findMany();
    }

}
