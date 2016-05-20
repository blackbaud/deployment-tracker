package com.blackbaud.deployment.core.domain;

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

    @Transactional
    public DeploymentInfoEntity save(DeploymentInfoEntity entity) {
        artifactInfoRepository.save(extractArtifactInfo(entity));
        return deploymentInfoRepository.save(entity);
    }

    public DeploymentInfoEntity findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId) {
        return deploymentInfoRepository.findOneByFoundationAndSpaceAndArtifactId(foundation, space, artifactId);
    }

    public List<DeploymentInfoEntity> findManyByFoundationAndSpace(String foundation, String space) {
        return deploymentInfoRepository.findManyByFoundationAndSpace(foundation, space);
    }

    private ArtifactInfoEntity extractArtifactInfo(DeploymentInfoEntity deploymentInfoEntity) {
        return ArtifactInfoEntity.builder()
                .artifactId(deploymentInfoEntity.getArtifactId())
                .buildVersion(deploymentInfoEntity.getBuildVersion())
                .gitSha(deploymentInfoEntity.getGitSha())
                .build();
    }

}