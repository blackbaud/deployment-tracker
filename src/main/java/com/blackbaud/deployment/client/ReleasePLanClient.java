package com.blackbaud.deployment.client;

import com.blackbaud.deployment.api.ArtifactReleaseInfo;
import com.blackbaud.deployment.api.Release;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.rest.client.CrudClient;

import java.util.List;

public class ReleasePlanClient extends CrudClient<Release, ReleasePlanClient> {

    public ReleasePlanClient(String baseUrl) {
        super(baseUrl, ResourcePaths.RELEASE_PATH, Release.class);
    }

}
