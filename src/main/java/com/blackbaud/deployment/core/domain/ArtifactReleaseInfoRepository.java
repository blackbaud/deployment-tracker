package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtifactReleaseInfoRepository extends CrudRepository<ArtifactReleaseInfoEntity, Long> {

    ArtifactReleaseInfoEntity findOneByFoundationAndSpaceAndArtifactId(String foundation, String space, String artifactId);

    List<ArtifactReleaseInfoEntity> findManyByFoundationAndSpace(String foundation, String space);

}
