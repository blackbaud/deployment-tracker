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

    public List<ArtifactRelease> findAllBySpaceAndFoundation(String foundation, String space) {
        return crudClientRequest.path(foundation).path(space)
                .findMany();
    }

    public List<ArtifactRelease> findLatestOfEachArtifactBySpaceAndFoundation(String foundation, String space) {
        return crudClientRequest.path(foundation).path(space).path("current")
                .findMany();
    }

    public void remediationCreate(String foundation, String space, List<ArtifactRelease> artifactReleases) {
        getUntypedCrudClientRequest().getClientRequest().path(foundation).path(space).path(ResourcePaths.REMEDIATE_PATH).createWithPost(artifactReleases);
    }
}
