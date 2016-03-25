package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.DeploymentInfoConverter;
import com.blackbaud.deployment.api.DeploymentInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.DeploymentInfoEntity;
import com.blackbaud.deployment.core.domain.DeploymentInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
@Path(ResourcePaths.DEPLOYMENT_TRACKER_PATH + "/{foundation}/{space}")
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentTrackerResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private DeploymentInfoConverter converter;

    @Autowired
    private DeploymentInfoRepository repository;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public DeploymentInfo update(@PathParam("foundation") String foundation, @PathParam("space") String space,
                                 @Valid DeploymentInfo status) {
        DeploymentInfoEntity entity = converter.toEntity(status);
        entity.setFoundation(foundation);
        entity.setSpace(space);

        return converter.toApi(repository.save(entity));
    }

    @GET
    @Path("{appName}")
    public DeploymentInfo find(@PathParam("foundation") String foundation, @PathParam("space") String space,
                               @PathParam("appName") String appName) {
        DeploymentInfoEntity statusEntity = repository.findOneByFoundationAndSpaceAndArtifactId(foundation, space, appName);
        if (statusEntity == null) {
            throw new NotFoundException();
        }
        return converter.toApi(statusEntity);
    }

    @GET
    public List<DeploymentInfo> findAllInSpace(@PathParam("foundation") String foundation, @PathParam("space") String space) {
        List<DeploymentInfoEntity> statusEntities = repository.findManyByFoundationAndSpace(foundation, space);
        return converter.toApiList(statusEntities);
    }

}
