package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ArtifactOrderUpdate;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ReleasePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.RELEASE_PLAN_ARTIFACT_REORDER)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ReleasePlanArtifactResource {

    @Inject
    private ReleasePlanService releasePlanService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateArtifactOrder(@Valid ArtifactOrderUpdate artifactOrderUpdate) {
        releasePlanService.updateArtifactOrder(artifactOrderUpdate.getMoveSha(),
                                               artifactOrderUpdate.getAnchorSha(),
                                               artifactOrderUpdate.getPosition());
    }
}
