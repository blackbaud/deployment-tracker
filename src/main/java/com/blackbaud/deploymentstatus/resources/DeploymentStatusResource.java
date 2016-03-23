package com.blackbaud.deploymentstatus.resources;

import com.blackbaud.deploymentstatus.DeploymentStatusConverter;
import com.blackbaud.deploymentstatus.api.ResourcePaths;
import com.blackbaud.deploymentstatus.api.DeploymentStatus;

import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.blackbaud.deploymentstatus.core.domain.DeploymentStatusEntity;
import com.blackbaud.deploymentstatus.core.domain.DeploymentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path(ResourcePaths.DEPLOYMENT_STATUS_PATH + "/{foundation}/{space}")
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentStatusResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private DeploymentStatusConverter converter;

    @Autowired
    private DeploymentStatusRepository repository;

    @POST
    public DeploymentStatus addDeploymentStatus(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                                @Valid DeploymentStatus status) {
        DeploymentStatusEntity entity = converter.toEntity(status);
        entity.setFoundation(foundation);
        entity.setSpace(space);

        return converter.toApi(repository.save(entity));
    }

    @GET
    @Path("{appName}" + ResourcePaths.ACTIVE_PATH)
    public DeploymentStatus findActiveAppForSpace(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                                  @PathParam("appName") String appName) {
        DeploymentStatusEntity statusEntity = repository.findOneByFoundationAndSpaceAndAppName(foundation, space, appName);
        if (statusEntity == null) {
            throw new NotFoundException();
        }
        return converter.toApi(statusEntity);
    }

    @GET
    @Path(ResourcePaths.ACTIVE_PATH)
    public List<DeploymentStatus> findAllActiveForSpace(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        List<DeploymentStatusEntity> statusEntities = repository.findManyByFoundationAndSpace(foundation, space);
        return converter.toApiList(statusEntities);
    }

}
