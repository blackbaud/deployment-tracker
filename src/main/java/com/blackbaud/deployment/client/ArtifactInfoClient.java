package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ArtifactInfoClient extends CrudClient<ArtifactInfo, ArtifactInfoClient> {

    public ArtifactInfoClient(String baseUrl) {
        super(baseUrl, ResourcePaths.ARTIFACT_INFO_PATH, ArtifactInfo.class);
    }

    public ArtifactInfo update(String artifactId, String buildVersion, ArtifactInfo artifactInfo) {
        return crudClientRequest.path(artifactId).path(buildVersion)
                .updateWithPut(artifactInfo);
    }

    public ArtifactInfo create(ArtifactInfo artifactInfo) {
        return crudClientRequest.createWithPost(artifactInfo);
    }

    public void create(List<ArtifactInfo> artifactInfos){
        getUntypedCrudClientRequest().getClientRequest().path("bulk").createWithPost(artifactInfos);
    }

    public ArtifactInfo find(String artifactId, String buildVersion) {
        return crudClientRequest.path(artifactId).path(buildVersion)
                .find();
    }

    public List<ArtifactInfo> findMany(String artifactId) {
        return crudClientRequest.path(artifactId).findMany();
    }

}
