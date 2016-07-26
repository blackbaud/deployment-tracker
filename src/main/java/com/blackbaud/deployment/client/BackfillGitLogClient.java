package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

public class BackfillGitLogClient extends CrudClient<Object, BackfillGitLogClient> {

    public BackfillGitLogClient(String baseUrl) {
        super(baseUrl, ResourcePaths.BACKFILL_GIT_LOG_PATH, Object.class);
    }

    public void post(String artifactId) {
        crudClientRequest.createWithPost(artifactId, null);
    }

    public void post() {
        crudClientRequest.createWithPost(null);
    }
}
