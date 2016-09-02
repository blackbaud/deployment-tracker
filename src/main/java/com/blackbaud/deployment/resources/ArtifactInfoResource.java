package com.blackbaud.deployment.resources;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ResourcePaths;
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import com.blackbaud.deployment.core.domain.ArtifactInfoPrimaryKey;
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository;
import com.blackbaud.deployment.core.domain.ArtifactInfoService;
import com.blackbaud.deployment.core.domain.git.GitLogParserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.ARTIFACT_INFO_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ArtifactInfoResource {

    @Autowired
    private ArtifactInfoConverter converter;

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private ArtifactInfoService artifactInfoService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{artifactId}/{buildVersion}")
    public ArtifactInfo put(@PathParam("artifactId") String artifactId, @PathParam("buildVersion") String buildVersion,
                            @Valid ArtifactInfo artifactInfo) {
        return artifactInfoService.create(artifactId, buildVersion, converter.toEntity(artifactInfo));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ArtifactInfo create(@Valid ArtifactInfo artifactInfo) {
        return artifactInfoService.createIfNotExist(artifactInfo);
    }

    @GET
    @Path("{artifactId}")
    public List<ArtifactInfo> find(@PathParam("artifactId") String artifactId) {
        List<ArtifactInfoEntity> artifactInfoList = artifactInfoRepository.findByArtifactId(artifactId);
        if (artifactInfoList == null) {
            throw new NotFoundException();
        }
        return converter.toApiList(artifactInfoList);
    }

    @GET
    @Path("{artifactId}/{buildVersion}")
    public ArtifactInfo find(@PathParam("artifactId") String artifactId, @PathParam("buildVersion") String buildVersion) {
        ArtifactInfoEntity requestedArtifact = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, buildVersion));
        if (requestedArtifact == null) {
            throw new NotFoundException();
        }
        return converter.toApi(requestedArtifact);

    }

    @POST
    @Path("bulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ArtifactInfo> createAll(@Valid List<ArtifactInfo> artifactInfos) {
        try{
            return artifactInfoService.createIfNotExist(artifactInfos);
        } catch (GitLogParserFactory.InvalidRepositoryException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }
}
