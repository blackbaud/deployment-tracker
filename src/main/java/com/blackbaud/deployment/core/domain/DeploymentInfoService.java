package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.DeploymentInfoConverter;
import com.blackbaud.deployment.api.DeploymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DeploymentInfoService {
    @Autowired
    DeploymentInfoRepository deploymentInfoRepository;
    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private DeploymentInfoConverter converter;

    @Transactional
    public DeploymentInfo save(DeploymentInfo deploymentInfo, String foundation, String space) {
        DeploymentInfoEntity entity = converter.toEntity(deploymentInfo, foundation, space);
        artifactInfoRepository.save(extractArtifactInfo(entity));
        return converter.toApi(deploymentInfoRepository.save(entity));
    }

    public DeploymentInfo findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return converter.toApi(deploymentInfoRepository.findOneByFoundationAndSpaceAndArtifactId(foundation, space, artifactId));
    }

    public List<DeploymentInfo> findManyByFoundationAndSpace(String foundation, String space) {
        return converter.toApiList(deploymentInfoRepository.findManyByFoundationAndSpace(foundation, space));
    }

    private ArtifactInfoEntity extractArtifactInfo(DeploymentInfoEntity deploymentInfoEntity) {
        return ArtifactInfoEntity.builder()
                .artifactId(deploymentInfoEntity.getArtifactId())
                .buildVersion(deploymentInfoEntity.getBuildVersion())
                .gitSha(deploymentInfoEntity.getGitSha())
                .build();
    }

}