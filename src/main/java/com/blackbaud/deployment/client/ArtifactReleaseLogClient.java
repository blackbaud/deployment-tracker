package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.blackbaud.deployment.api.ArtifactReleaseLog;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ArtifactReleaseLogClient extends CrudClient<ArtifactReleaseLog, ArtifactReleaseLogClient> {

    public ArtifactReleaseLogClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_RELEASE_LOG, ArtifactReleaseLog.class);
    }

    public List<ArtifactReleaseLog> findAll() {
        return crudClientRequest.findMany();
    }

}
