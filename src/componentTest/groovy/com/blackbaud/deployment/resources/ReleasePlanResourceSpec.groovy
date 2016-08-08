package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.ReleasePlanConverter
import com.blackbaud.deployment.api.ReleasePlan
import com.blackbaud.deployment.client.ReleasePlanClient
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import com.blackbaud.deployment.core.domain.ReleasePlanEntity
import com.blackbaud.deployment.core.domain.ReleasePlanRepository
import spock.lang.Specification

import javax.inject.Inject
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotFoundException

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ReleasePlanResourceSpec extends Specification {

    @Inject
    private ReleasePlanClient releasePlanClient

    @Inject
    private ReleasePlanRepository releasePlanRepository

    @Inject
    private ReleasePlanConverter converter

    @Inject
    private ArtifactInfoRepository artifactInfoRepository

    def "can create a new release plan"() {
        given:
        ReleasePlan newPlan = aRandom.releasePlan().build()

        when:
        ReleasePlan createdPlan = releasePlanClient.create(newPlan)

        then:
        createdPlan.notes == newPlan.notes
        createdPlan.id != null
        createdPlan.created != null
        createdPlan.activated == null
        createdPlan.archived == null
    }

    def "can NOT create a new release plan if there is already a current one"() {
        given:
        createCurrentReleasePlan()

        and:
        ReleasePlan newPlan = aRandom.releasePlan().build()

        when:
        releasePlanClient.create(newPlan)

        then:
        Exception exception = thrown()
        exception instanceof BadRequestException
    }


    def "can get current release plan if one exists"() {
        given:
        createCurrentReleasePlan()

        when:
        ReleasePlan releasePlan = releasePlanClient.getCurrentReleasePlan()

        then:
        releasePlan != null
    }

    def "get an exception if no current release plan"() {
        when:
        releasePlanClient.getCurrentReleasePlan()

        then:
        Exception exception = thrown()
        exception instanceof NotFoundException
    }

    def "can update notes to existing release plan"() {
        given:
        ReleasePlanEntity existingPlan = createCurrentReleasePlan()
        String newNotes = "new notes"

        when:
        releasePlanClient.updateNotes(existingPlan.id, newNotes)

        then:
        ReleasePlanEntity updatedReleasePlan = releasePlanRepository.findOne(existingPlan.id)
        updatedReleasePlan.notes == newNotes
    }

    def "can activate release plan"() {
        given:
        ReleasePlanEntity currentReleasePlan = createCurrentReleasePlan()

        when:
        releasePlanClient.activateReleasePlan(currentReleasePlan.id)

        then:
        ReleasePlanEntity updatedReleasePlan = releasePlanRepository.findOne(currentReleasePlan.id)
        updatedReleasePlan.activated != null
    }

    def "cannot activate archived release plan"() {
        given:
        ReleasePlanEntity archivedReleasePlan = aRandom.releasePlanEntity().build()
        archivedReleasePlan = releasePlanRepository.save(archivedReleasePlan)

        when:
        releasePlanClient.activateReleasePlan(archivedReleasePlan.id)

        then:
        Exception exception = thrown()
        exception instanceof BadRequestException
    }

    def "can archive an existing release plan"() {
        given:
        ReleasePlanEntity releasePlan = createCurrentReleasePlan()

        when:
        releasePlanClient.archiveReleasePlan(releasePlan.id)

        then:
        ReleasePlanEntity archivedReleasePlan = releasePlanRepository.findOne(releasePlan.id)
        archivedReleasePlan.archived != null
    }

    def "cannot activate nonexistent release plan"() {
        given:
        ReleasePlanEntity archivedReleasePlan = aRandom.releasePlanEntity().build()

        when:
        releasePlanClient.activateReleasePlan(archivedReleasePlan.id)

        then:
        Exception exception = thrown()
        exception instanceof NotFoundException
    }

    def "associate an artifact with an existing release plan"() {
        given:
        ReleasePlanEntity existingPlan = createCurrentReleasePlan()
        ArtifactInfoEntity core = ArtifactInfoEntity.builder().artifactId("core").buildVersion("1").gitSha("asd").build()
        core = artifactInfoRepository.save(core)
        existingPlan.artifacts = [core]

        when:
        releasePlanRepository.save(existingPlan)

        then:
        ReleasePlanEntity updatedPlan = releasePlanRepository.findOne(existingPlan.id)
        updatedPlan.artifacts == [core]
    }

    def "associate an artifact with a new release plan"() {
        given:
        ArtifactInfoEntity core = ArtifactInfoEntity.builder().artifactId("core").buildVersion("1").gitSha("asd").build()
        core = artifactInfoRepository.save(core)
        ReleasePlanEntity newPlan = ReleasePlanEntity.builder().artifacts([core]).build()

        when:
        newPlan = releasePlanRepository.save(newPlan)

        then:
        ReleasePlanEntity updatedPlan = releasePlanRepository.findOne(newPlan.id)
        updatedPlan.artifacts == [core]
    }

    def ReleasePlanEntity createCurrentReleasePlan() {
        ReleasePlanEntity currentReleasePlan = aRandom.releasePlanEntity()
                .archived(null)
                .build()
        releasePlanRepository.save(currentReleasePlan)
    }
}
