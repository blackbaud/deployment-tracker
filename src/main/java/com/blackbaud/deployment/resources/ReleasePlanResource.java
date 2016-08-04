package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ReleasePlan;
import com.blackbaud.deployment.api.ResourcePaths;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.RELEASE_PLAN_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ReleasePlanResource {

    @POST
    public ReleasePlan createReleasePlan(List<ReleasePlan> releasePlan) {
        return null;
    }

}
