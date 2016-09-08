package com.blackbaud.deployment.client;


import com.blackbaud.deployment.api.ArtifactOrderUpdate;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

public class ReleasePlanArtifactOrderClient extends CrudClient<ArtifactOrderUpdate, ReleasePlanArtifactOrderClient> {

    public ReleasePlanArtifactOrderClient(String baseUrl) {
        super(baseUrl, ResourcePaths.RELEASE_PLAN_ARTIFACT_REORDER, ArtifactOrderUpdate.class);
    }

    public void updateArtifactOrder(ArtifactOrderUpdate artifactOrderUpdate) {
        getUntypedCrudClientRequest().createWithPost(artifactOrderUpdate);
    }
}
