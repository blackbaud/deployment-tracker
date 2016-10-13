package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactReleaseDiff;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ArtifactReleaseReportClient extends CrudClient<ArtifactReleaseDiff, ArtifactReleaseReportClient> {

    public ArtifactReleaseReportClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_RELEASE_REPORT, ArtifactReleaseDiff.class);
    }

    public List<ArtifactReleaseDiff> findAll() {
        return crudClientRequest.findMany();
    }

    public List<ArtifactReleaseDiff> findAll(String foundation) {
        return crudClientRequest.path(foundation).findMany();
    }

}
