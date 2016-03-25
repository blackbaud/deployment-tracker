package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeploymentInfoRepository extends CrudRepository<DeploymentInfoEntity, Long> {

    DeploymentInfoEntity findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId);

    List<DeploymentInfoEntity> findManyByFoundationAndSpace(String foundation, String space);

}
