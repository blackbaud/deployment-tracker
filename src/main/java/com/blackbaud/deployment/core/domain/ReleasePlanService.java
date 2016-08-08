package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ReleasePlanConverter;
import com.blackbaud.deployment.api.ReleasePlan;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.ZonedDateTime;

@Component
public class ReleasePlanService {

    @Inject
    private ReleasePlanConverter converter;

    @Inject
    private ReleasePlanRepository releasePlanRepository;

    public ReleasePlan createReleasePlan(ReleasePlan releasePlan) {
        if (currentReleasePlanExists()) {
            throw new BadRequestException("A current release plan already exists");
        }
        return createNewReleasePlan(releasePlan);
    }

    private ReleasePlan createNewReleasePlan(ReleasePlan releasePlan) {
        ReleasePlanEntity entity = ReleasePlanEntity.builder()
                .notes(releasePlan.getNotes())
                .created(ZonedDateTime.now())
                .build();
        releasePlanRepository.save(entity);
        return converter.toApi(entity);
    }

    private boolean currentReleasePlanExists() {
        return getCurrentReleasePlan() != null;
    }

    public ReleasePlanEntity getCurrentReleasePlan() {
        return releasePlanRepository.findByArchivedNull();
    }

    public ReleasePlanEntity getExistingReleasePlan(Long id) {
        ReleasePlanEntity releasePlan = releasePlanRepository.findOne(id);
        if (releasePlan == null) {
            throw new NotFoundException("No release plan with id " + id + " exists");
        }
        return releasePlan;
    }

    public ReleasePlan updateNotes(Long id, String notes) {
        ReleasePlanEntity releasePlan = getExistingReleasePlan(id);
        releasePlan.setNotes(notes);
        return converter.toApi(releasePlanRepository.save(releasePlan));
    }

    public ReleasePlan activate(Long id) {
        ReleasePlanEntity releasePlan = getExistingReleasePlan(id);
        if (releasePlan.getArchived() != null) {
            throw new BadRequestException("Cannot activate a archived release plan");
        }
        releasePlan.setActivated(ZonedDateTime.now());
        return converter.toApi(releasePlanRepository.save(releasePlan));
    }

    public ReleasePlan archive(Long id) {
        ReleasePlanEntity releasePlan = getExistingReleasePlan(id);
        releasePlan.setArchived(ZonedDateTime.now());
        return converter.toApi(releasePlanRepository.save(releasePlan));
    }
}
