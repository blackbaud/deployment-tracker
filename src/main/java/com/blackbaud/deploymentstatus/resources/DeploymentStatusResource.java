package com.blackbaud.deploymentstatus.resources;

import com.blackbaud.deploymentstatus.DeploymentStatusConverter;
import com.blackbaud.deploymentstatus.api.DeploymentStatus;
import com.blackbaud.deploymentstatus.api.ResourcePaths;
import com.blackbaud.deploymentstatus.core.domain.DeploymentStatusEntity;
import com.blackbaud.deploymentstatus.core.domain.DeploymentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

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

    @PUT
    public DeploymentStatus update(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                   @Valid DeploymentStatus status) {
        DeploymentStatusEntity entity = converter.toEntity(status);
        entity.setFoundation(foundation);
        entity.setSpace(space);

        return converter.toApi(repository.save(entity));
    }

    @GET
    @Path("{appName}")
    public DeploymentStatus find(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                 @PathParam("appName") String appName) {
        DeploymentStatusEntity statusEntity = repository.findOneByFoundationAndSpaceAndAppName(foundation, space, appName);
        if (statusEntity == null) {
            throw new NotFoundException();
        }
        return converter.toApi(statusEntity);
    }

    @GET
    public List<DeploymentStatus> findAllInSpace(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        List<DeploymentStatusEntity> statusEntities = repository.findManyByFoundationAndSpace(foundation, space);
        return converter.toApiList(statusEntities);
    }

}
