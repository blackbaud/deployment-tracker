package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ReleasePlanConverter;
import com.blackbaud.deployment.api.ReleasePlan;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.time.ZonedDateTime;

@Component
public class ReleasePlanService {

    @Inject
    private ReleasePlanConverter converter;

    @Inject
    private ReleasePlanRepository releasePlanRepository;

    public ReleasePlan createReleasePlan() {
        if (currentReleasePlanExists()) {
            throw new BadRequestException("A current release plan already exists");
        }
        return createNewReleasePlan();
    }

    private ReleasePlan createNewReleasePlan() {
        ReleasePlanEntity entity = ReleasePlanEntity.builder()
                .created(ZonedDateTime.now())
                .build();
        releasePlanRepository.save(entity);
        return converter.toApi(entity);
    }

    private boolean currentReleasePlanExists() {
        return getCurrentReleasePlan() != null;
    }

    public ReleasePlanEntity getCurrentReleasePlan(){
        return releasePlanRepository.findByArchivedNull();
    }

    public ReleasePlanEntity getExistingReleasePlan(Long id) {
        ReleasePlanEntity releasePlan = releasePlanRepository.findOne(id);
        if (releasePlan == null) {
            throw new BadRequestException("No release plan with id "+ id +" exists");
        }
        return releasePlan;
    }


}
