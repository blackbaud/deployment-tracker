package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactInfoRepository extends CrudRepository<ArtifactInfoEntity, ArtifactInfoPrimaryKey> {

    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionGreaterThanAndBuildVersionLessThanEqual(String artifactId, String fromVersion, String toVersion);
}
