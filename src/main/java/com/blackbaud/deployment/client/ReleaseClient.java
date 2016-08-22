package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactRelease;
import com.blackbaud.deployment.api.Release;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ReleaseClient extends CrudClient<Release, ReleaseClient> {

    public ReleaseClient(String baseUrl) {
        super(baseUrl, ResourcePaths.RELEASE_PATH, Release.class);
    }

    public Release getCurrentRelease() {
        return crudClientRequest.path(ResourcePaths.CURRENT_PATH).find();
    }

    public Release getCurrentReleaseForProdSnapshot(List<ArtifactRelease> prodArtifactReleases) {
        return (Release) getUntypedCrudClientRequest()
                .path(ResourcePaths.CURRENT_PATH)
                .createWithPost(prodArtifactReleases);
    }

    public Release getCurrentReleasePlanDiffForProdSnapshot(List<ArtifactRelease> prodArtifactReleases) {
        return (Release) getUntypedCrudClientRequest()
                .path(ResourcePaths.RELEASE_PLAN_DIFF_PATH)
                .createWithPost(prodArtifactReleases);
    }

}
