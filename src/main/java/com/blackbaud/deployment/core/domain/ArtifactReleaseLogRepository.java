package com.blackbaud.deployment.core.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;

public interface ArtifactReleaseLogRepository extends CrudRepository<ArtifactReleaseLogEntity, ArtifactReleaseLogPrimaryKey> {

    ArtifactReleaseLogEntity findFirstByArtifactIdOrderByReleaseVersionDesc(String artifactId);

    ArtifactReleaseLogEntity findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(String foundation, String space, String artifactId);

    @Query(value = "select distinct on (artifact_id) * from artifact_release_log arl " +
                   "where foundation = :foundation and space = :space order by artifact_id, release_version DESC",
           nativeQuery = true)
    List<ArtifactReleaseLogEntity> findLatestOfEachArtifactByFoundationAndSpace(@Param("foundation") String foundation, @Param("space") String space);

}
