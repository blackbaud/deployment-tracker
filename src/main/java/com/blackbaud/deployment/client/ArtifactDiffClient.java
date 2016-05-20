package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactDiff;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

public class ArtifactDiffClient extends CrudClient<ArtifactDiff, ArtifactDiffClient> {

    public ArtifactDiffClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_DIFF_PATH, ArtifactDiff.class);
    }

    public ArtifactDiff find(String artifactId, String fromBuildVersion, String toBuildVersion) {
        return crudClientRequest.path(artifactId).path(fromBuildVersion).path(toBuildVersion)
                .find();
    }

}
