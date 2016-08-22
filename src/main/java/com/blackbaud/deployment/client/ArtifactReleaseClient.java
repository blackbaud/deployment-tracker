package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ArtifactReleaseClient extends CrudClient<ArtifactRelease, ArtifactReleaseClient> {

    public ArtifactReleaseClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_RELEASE_PATH, ArtifactRelease.class);
    }

    public ArtifactRelease create(String foundation, String space, ArtifactRelease info) {
        return crudClientRequest.path(foundation).path(space)
                .createWithPost(info);
    }

    public List<ArtifactRelease> findAllInSpace(String foundation, String space) {
        return crudClientRequest.path(foundation).path(space)
                .findMany();
    }

}
