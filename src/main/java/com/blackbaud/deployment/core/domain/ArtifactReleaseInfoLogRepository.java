package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactReleaseInfoLogRepository extends CrudRepository<ArtifactReleaseInfoLogEntity, ArtifactReleaseInfoLogPrimaryKey> {

    ArtifactReleaseInfoLogEntity findFirstByArtifactIdOrderByReleaseVersionDesc(String artifactId);

    ArtifactReleaseInfoLogEntity findFirstByFoundationAndSpaceAndArtifactIdOrderByReleaseVersionDesc(String foundation, String space, String artifactId);

    List<ArtifactReleaseInfoLogEntity> findManyByArtifactIdInAndFoundationAndSpace(List<String> artifactIdList, String foundation, String space);

    List<ArtifactReleaseInfoLogEntity> findManyByFoundationAndSpace(String foundation, String space);

}
