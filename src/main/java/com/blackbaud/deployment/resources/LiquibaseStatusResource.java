package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.api.LiquibaseStatus;
import com.blackbaud.security.BypassAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path("liquibase/status")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class LiquibaseStatusResource {

    @GET
    @BypassAuth
    public LiquibaseStatus getStatus() {
        return LiquibaseStatus.builder().status("COMPLETE").build();
    }

}
