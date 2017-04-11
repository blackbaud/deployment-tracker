package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactDependencyRepository extends CrudRepository<ArtifactDependencyEntity, ArtifactDependencyPrimaryKey> {

    List<ArtifactDependencyEntity> findByArtifactIdAndBuildVersion(String artifactId, String buildVersion);
}
