package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.GitLogInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class GitLogInfoClient extends CrudClient<GitLogInfo, GitLogInfoClient> {

    public GitLogInfoClient(String baseUrl) {
        super(baseUrl, ResourcePaths.GIT_LOG_INFO_PATH, GitLogInfo.class);
    }

    public List<GitLogInfo> find(String artifactId) {
        return crudClientRequest.path(artifactId).findMany();
    }

    public void post(String artifactId) {
        crudClientRequest.createWithPost(artifactId, null);
    }

    public void post() {
        crudClientRequest.createWithPost(null);
    }
}
