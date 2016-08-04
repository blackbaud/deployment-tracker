package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ReleasePlan;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

public class ReleasePlanClient extends CrudClient<ReleasePlan, ReleasePlanClient> {

    public ReleasePlanClient(String baseUrl) {
        super(baseUrl, ResourcePaths.RELEASE_PLAN_PATH, ReleasePlan.class);
    }

    public ReleasePlan getActiveReleasePlan() {
        return crudClientRequest.path(ResourcePaths.ACTIVE_PATH).find();
    }
}
