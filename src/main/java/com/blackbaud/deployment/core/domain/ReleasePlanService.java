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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReleasePlanService {

    @Inject
    private ReleasePlanConverter converter;

    @Inject
    private ReleasePlanRepository releasePlanRepository;

    @Inject
    private ArtifactInfoRepository artifactInfoRepository;

    @Inject
    private ArtifactInfoConverter artifactInfoConverter;

    public ReleasePlan createReleasePlan() {
        if (currentReleasePlanExists()) {
            throw new BadRequestException("A current plan already exists");
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
        ReleasePlanEntity releasePlan = releasePlanRepository.findByActivatedNull();
        if (releasePlan != null) {
            updateArtifactOrderIfNecessary(releasePlan);
        }
        return releasePlan;
    }

    public ReleasePlanEntity getExistingReleasePlan(Long id) {
        ReleasePlanEntity releasePlan = releasePlanRepository.findOne(id);
        if (releasePlan == null) {
            throw new NotFoundException("No release plan with id " + id + " exists");
        }
        updateArtifactOrderIfNecessary(releasePlan);
        return releasePlan;
    }

    private void updateArtifactOrderIfNecessary(ReleasePlanEntity releasePlan) {
        List<ArtifactInfoEntity> artifacts = releasePlan.getArtifacts();
        if (artifacts != null && artifacts.size() > 1) {
            artifacts.sort(Comparator.comparing(
                    ArtifactInfoEntity::getReleasePlanOrder, Comparator.nullsLast(Comparator.naturalOrder())));
            updateArtifactListOrder(artifacts);
        }
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
        failIfReleaseIsClosed(releasePlan);
        releasePlan.addArtifact(artifactInfoConverter.toEntity(newArtifact));
        return converter.toApi(releasePlanRepository.save(releasePlan));
    }

    public void delete(Long id) {
        try {
            ReleasePlanEntity releasePlan = getExistingReleasePlan(id);
            releasePlan.getArtifacts().stream().forEach(a -> {
                a.setReleasePlanOrder(null);
                artifactInfoRepository.save(a);
            });
            releasePlanRepository.delete(id);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Attempted to delete a deleted release plan");
        }
    }

    public void deleteArtifact(Long id, String artifactId) {
        ReleasePlanEntity releasePlan = getExistingReleasePlan(id);
        failIfReleaseIsClosed(releasePlan);
        releasePlan.getArtifacts().stream().filter(a -> a.getArtifactId().equals(artifactId)).findFirst().ifPresent(a -> {
            a.setReleasePlanOrder(null);
            artifactInfoRepository.save(a);
        });
        releasePlan.deleteArtifact(artifactId);
        releasePlanRepository.save(releasePlan);
        updateArtifactListOrder(releasePlan.getArtifacts());
    }

    public void updateArtifactOrder(String movingArtifactId, String anchorArtifactId, String position) {
        List<ArtifactInfoEntity> artifacts = getCurrentReleasePlan().getArtifacts();

        ArtifactInfoEntity artifactToMove = artifacts.stream().filter(a -> a.getArtifactId().equals(movingArtifactId)).findFirst().get();
        ArtifactInfoEntity artifactTarget = artifacts.stream().filter(a -> a.getArtifactId().equals(anchorArtifactId)).findFirst().get();

        artifacts.remove(artifactToMove);
        if (position.equals("above")) {
            artifacts.add(artifacts.indexOf(artifactTarget), artifactToMove);
        } else if (artifacts.size() >= artifacts.indexOf(artifactTarget) + 1){
            artifacts.add(artifacts.indexOf(artifactTarget) + 1, artifactToMove);
        } else {
            artifacts.add(artifactToMove);
        }

        updateArtifactListOrder(artifacts);
    }

    private void updateArtifactListOrder(List<ArtifactInfoEntity> artifacts) {
        artifacts.forEach(a -> a.setReleasePlanOrder(artifacts.indexOf(a) + 1));
        artifactInfoRepository.save(artifacts);
    }

    private void failIfReleaseIsClosed(ReleasePlanEntity releasePlanEntity) {
        if (releasePlanEntity.getActivated() != null) {
            throw new BadRequestException("Release plan is already closed.");
        }
    }
}
