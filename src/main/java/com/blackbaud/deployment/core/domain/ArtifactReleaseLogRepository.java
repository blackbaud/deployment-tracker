package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactReleaseLogRepository extends CrudRepository<ArtifactReleaseLogEntity, ArtifactReleaseLogPrimaryKey> {

    ArtifactReleaseLogEntity findFirstByArtifactIdOrderByReleaseVersionDesc(String artifactId);

    ArtifactReleaseLogEntity findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(String foundation, String space, String artifactId);

    List<ArtifactReleaseLogEntity> findManyByFoundationAndSpace(String foundation, String space);

}
