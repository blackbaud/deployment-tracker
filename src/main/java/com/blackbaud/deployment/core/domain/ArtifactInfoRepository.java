package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtifactInfoRepository extends CrudRepository<ArtifactInfoEntity, ArtifactInfoPrimaryKey> {

    ArtifactInfoEntity findFirstByArtifactIdOrderByBuildVersionDesc(String artifactId);

    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionGreaterThanAndBuildVersionLessThanEqual(String artifactId, String fromVersion, String toVersion);
}
