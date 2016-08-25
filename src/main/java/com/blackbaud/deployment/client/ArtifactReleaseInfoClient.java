package com.blackbaud.deployment.client;

import com.blackbaud.rest.client.CrudClient;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.api.ArtifactRelease;

import java.util.List;

public class ArtifactReleaseInfoClient extends CrudClient<ArtifactRelease, ArtifactReleaseInfoClient> {

    public ArtifactReleaseInfoClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_RELEASE_INFO_PATH, ArtifactRelease.class);
    }

    public ArtifactRelease update(String foundation, String space, ArtifactRelease info) {
        return crudClientRequest.path(foundation).path(space)
                .updateWithPut(info);
    }

    public ArtifactRelease find(String foundation, String space, String artifactId) {
        return crudClientRequest.path(foundation).path(space).path(artifactId)
                .find();
    }

    public List<ArtifactRelease> findLatestOfEachArtifactBySpaceAndFoundation(String foundation, String space) {
        return crudClientRequest.path(foundation).path(space)
                .findMany();
    }

}
