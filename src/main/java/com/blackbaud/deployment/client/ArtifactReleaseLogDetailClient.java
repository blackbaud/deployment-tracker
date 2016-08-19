package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactReleaseLogDetail;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ArtifactReleaseLogDetailClient extends CrudClient<ArtifactReleaseLogDetail, ArtifactReleaseLogDetailClient> {

    public ArtifactReleaseLogDetailClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_RELEASE_LOG, ArtifactReleaseLogDetail.class);
    }

    public List<ArtifactReleaseLogDetail> findAll() {
        return crudClientRequest.findMany();
    }

}
