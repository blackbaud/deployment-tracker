package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.ReleasePlanConverter;
import com.blackbaud.deployment.api.ReleasePlan;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ReleasePlanEntity;
import com.blackbaud.deployment.core.domain.ReleasePlanRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.ZonedDateTime;

@Component
@Path(ResourcePaths.RELEASE_PLAN_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ReleasePlanResource {

    @Inject
    private ReleasePlanRepository releasePlanRepository;

    @Inject
    private ReleasePlanConverter converter;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ReleasePlan createReleasePlan(ReleasePlan releasePlan) {
        ReleasePlanEntity entity = ReleasePlanEntity.builder()
                .notes(releasePlan.getNotes())
                .created(ZonedDateTime.now())
                .build();
        releasePlanRepository.save(entity);
        return converter.toApi(entity);
    }

}
