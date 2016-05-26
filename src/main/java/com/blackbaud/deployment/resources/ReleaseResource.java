package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.api.DevProdDeploymentInfos;
import com.blackbaud.deployment.core.domain.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Component
@Path(ResourcePaths.RELEASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ReleaseResource {

    @Autowired
    private ReleaseService releaseService;

    @GET
    @Path ("current/summary")
    public Map<String, DevProdDeploymentInfos> getCurrentReleaseSummary(){
        return releaseService.getCurrentSummary();
    }

}
