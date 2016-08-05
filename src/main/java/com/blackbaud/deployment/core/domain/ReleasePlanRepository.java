package com.blackbaud.deployment.core.domain;

import org.springframework.data.repository.CrudRepository;

public interface ReleasePlanRepository extends CrudRepository<ReleasePlanEntity, Long> {

    ReleasePlanEntity findByArchivedNull();
}
