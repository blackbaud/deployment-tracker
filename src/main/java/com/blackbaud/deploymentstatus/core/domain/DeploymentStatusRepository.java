package com.blackbaud.deploymentstatus.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeploymentStatusRepository extends CrudRepository<DeploymentStatusEntity, Long> {

    DeploymentStatusEntity findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId);

    List<DeploymentStatusEntity> findManyByFoundationAndSpace(String foundation, String space);

}
