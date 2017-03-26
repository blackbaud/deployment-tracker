package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

public interface ArtifactDependencyRepository extends CrudRepository<ArtifactDependencyEntity, ArtifactDependencyPrimaryKey> {

    ArtifactDependencyEntity findOneByArtifactIdAndBuildVersion(String artifactId, String buildVersion);
}
