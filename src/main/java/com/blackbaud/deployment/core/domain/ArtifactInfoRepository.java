package com.blackbaud.deployment.core.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactInfoRepository extends CrudRepository<ArtifactInfoEntity, ArtifactInfoPrimaryKey> {

    List<ArtifactInfoEntity> findByArtifactId(String artifactId);

    ArtifactInfoEntity findOneByArtifactIdAndBuildVersion(String artifactId, String buildVersion);

    ArtifactInfoEntity findFirstByArtifactIdOrderByBuildVersionDesc(String artifactId);

    @Query(value = "select distinct artifact_id from artifact_info", nativeQuery = true)
    List<String> getDistinctArtifactIds();

}
