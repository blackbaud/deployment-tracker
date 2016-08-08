package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.ReleasePlanConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ReleasePlan;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ReleasePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.RELEASE_PLAN_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ReleasePlanResource {

    @Inject
    private ReleasePlanConverter converter;

    @Inject
    private ReleasePlanService releasePlanService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ReleasePlan createReleasePlan() {
        return releasePlanService.createReleasePlan();
    }

    @GET
    @Path(ResourcePaths.CURRENT_PATH)
    public ReleasePlan getCurrentReleasePlan() {
        ReleasePlan releasePlan = converter.toApi(releasePlanService.getCurrentReleasePlan());
        if (releasePlan == null) {
            throw new NotFoundException("No current release plan exists");
        }
        return releasePlan;
    }

    @PUT
    @Path("{id}/" + ResourcePaths.NOTES_PATH)
    public ReleasePlan updateNotes(@PathParam("id") Long id, String notes) {
        return releasePlanService.updateNotes(id, notes);
    }

    @PUT
    @Path("{id}/" + ResourcePaths.ACTIVATE_PATH)
    public ReleasePlan activate(@PathParam("id") Long id) {
        return releasePlanService.activate(id);
    }

    @PUT
    @Path("{id}/" + ResourcePaths.ARCHIVE_PATH)
    public ReleasePlan archive(@PathParam("id") Long id) {
        return releasePlanService.archive(id);
    }

    @PUT
    @Path("{id}/" + ResourcePaths.ARTIFACT_PATH)
    public ReleasePlan addArtifact(@PathParam("id") Long id, ArtifactInfo newArtifact) {
        return releasePlanService.addArtifact(id, newArtifact);
    }
}
