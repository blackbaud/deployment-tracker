package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.ReleasePlanConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ReleasePlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class ReleasePlanService {

    @Inject
    private ReleasePlanConverter converter;

    @Inject
    private ReleasePlanRepository releasePlanRepository;

    @Inject
    private ArtifactInfoConverter artifactInfoConverter;

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

    public ReleasePlanEntity getCurrentReleasePlan() {
        return releasePlanRepository.findByActivatedNull();
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
        releasePlan.setActivated(ZonedDateTime.now());
        return converter.toApi(releasePlanRepository.save(releasePlan));
    }

    public ReleasePlan addArtifact(Long id, ArtifactInfo newArtifact) {
        ReleasePlanEntity releasePlan = getExistingReleasePlan(id);
        if (releasePlan.getActivated() != null) {
            throw new BadRequestException("Release plan is already closed.");
        }
        releasePlan.addArtifact(artifactInfoConverter.toEntity(newArtifact));
        return converter.toApi(releasePlanRepository.save(releasePlan));
    }

    public void delete(Long id) {
        try {
            releasePlanRepository.delete(id);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Attempted to delete a deleted release plan");
        }
    }
}
