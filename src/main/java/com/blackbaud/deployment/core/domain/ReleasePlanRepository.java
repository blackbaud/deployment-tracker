package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.api.ReleasePlan;
import org.springframework.data.repository.CrudRepository;

public interface ReleasePlanRepository extends CrudRepository<ReleasePlanEntity, Integer> {

    ReleasePlanEntity findByClosedNull();
}
