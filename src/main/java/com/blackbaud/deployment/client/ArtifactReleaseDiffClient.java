package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ArtifactReleaseDiffClient extends CrudClient<ArtifactReleaseDiff, ArtifactReleaseDiffClient> {

    public ArtifactReleaseDiffClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_RELEASE_REPORT, ArtifactReleaseDiff.class);
    }

    public List<ArtifactReleaseDiff> findAll() {
        return crudClientRequest.findMany();
    }

}
