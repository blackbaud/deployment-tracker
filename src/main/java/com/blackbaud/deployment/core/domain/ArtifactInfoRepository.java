package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactInfoRepository extends CrudRepository<ArtifactInfoEntity, ArtifactInfoPrimaryKey> {

    List<ArtifactInfoEntity> findByArtifactId(String artifactId);
    ArtifactInfoEntity findFirstByArtifactIdAndBuildVersionLessThanOrderByBuildVersionDesc(String artifactId, String buildVersion);
    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionLessThanEqual(String artifactId, String toVersion);
    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionGreaterThan(String artifactId, String fromVersion);
    List<ArtifactInfoEntity> findByArtifactIdAndBuildVersionGreaterThanAndBuildVersionLessThanEqual(String artifactId, String fromVersion, String toVersion);
}
