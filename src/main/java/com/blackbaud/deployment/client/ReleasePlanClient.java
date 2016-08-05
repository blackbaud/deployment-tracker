package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ReleasePlan;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

public class ReleasePlanClient extends CrudClient<ReleasePlan, ReleasePlanClient> {

    public ReleasePlanClient(String baseUrl) {
        super(baseUrl, ResourcePaths.RELEASE_PLAN_PATH, ReleasePlan.class);
    }

    public ReleasePlan getCurrentReleasePlan() {
        return crudClientRequest.path(ResourcePaths.CURRENT_PATH).find();
    }

    public void updateNotes(Long id, String notes) {
        getUntypedCrudClientRequest().path(id).path(ResourcePaths.NOTES_PATH).updateWithPut(notes);
    }

    public void activateReleasePlan(Long id) {
        getUntypedCrudClientRequest().path(id).path(ResourcePaths.ACTIVATE_PATH).updateWithPut(id);
    }

    public void archiveReleasePlan(Long id) {
        getUntypedCrudClientRequest().path(id).path(ResourcePaths.ARCHIVE_PATH).updateWithPut(id);
    }

    public void addArtifact(ArtifactInfo artifactInfo) {}
}
