package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtifactInfoRepository extends CrudRepository<ArtifactInfoEntity, ArtifactInfoPrimaryKey> {

    ArtifactInfoEntity findFirstByArtifactIdAndBuildVersionLessThanOrderByBuildVersionDesc(String artifactId, String buildVersion);
    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionLessThanEqual(String artifactId, String toVersion);
    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionGreaterThan(String artifactId, String fromVersion);
    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionGreaterThanAndBuildVersionLessThanEqual(String artifactId, String fromVersion, String toVersion);
}
