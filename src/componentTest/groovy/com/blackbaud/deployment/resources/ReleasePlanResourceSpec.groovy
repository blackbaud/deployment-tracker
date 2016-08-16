package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.ReleasePlanConverter
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ReleasePlan
import com.blackbaud.deployment.client.ArtifactInfoClient
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

    @Inject
    private ArtifactInfoConverter artifactInfoConverter

    @Inject
    private ArtifactInfoClient artifactInfoClient

    private final ArtifactInfo newArtifact = RealArtifacts.getRecentDeploymentTrackerArtifact()
    private final ArtifactInfo oldArtifact = RealArtifacts.getEarlyDeploymentTrackerArtifact()

    def "can create a new release plan"() {
        given:
        ReleasePlan newPlan = aRandom.releasePlan().build()

        when:
        ReleasePlan createdPlan = releasePlanClient.create(newPlan)

        then:
        createdPlan.id != null
        createdPlan.created != null
        createdPlan.activated == null
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

    def "cannot activate nonexistent release plan"() {
        given:
        ReleasePlanEntity releasePlan = aRandom.releasePlanEntity().build()

        when:
        releasePlanClient.activateReleasePlan(releasePlan.id)

        then:
        Exception exception = thrown()
        exception instanceof NotFoundException
    }

    def "can put artifacts to an existing release plan"() {
        given:
        ReleasePlanEntity currentPlan = createCurrentReleasePlan()
        ArtifactInfoEntity artifact1 = aRandom.artifactInfoEntity().build()
        ArtifactInfoEntity artifact2 = aRandom.artifactInfoEntity().build()
        artifactInfoRepository.save([artifact1, artifact2])

        when:
        releasePlanClient.addArtifact(currentPlan.id, artifactInfoConverter.toApi(artifact1))
        releasePlanClient.addArtifact(currentPlan.id, artifactInfoConverter.toApi(artifact2))

        then:
        ReleasePlanEntity updatedPlan = releasePlanRepository.findOne(currentPlan.id)
        updatedPlan.artifacts.sort() == [artifact1, artifact2].sort()
    }

    def "can update an artifact for a release plan"() {
        given: "a releasePlan"
        ReleasePlanEntity currentPlan = createCurrentReleasePlan()

        and: "create the original artifact info"
        artifactInfoClient.update(oldArtifact.artifactId, oldArtifact.buildVersion, oldArtifact)

        and: "create the newer artifact info to be updated on the release plan"
        artifactInfoClient.update(newArtifact.artifactId, newArtifact.buildVersion, newArtifact)

        and: "the original artifact version is added to the releasePlan"
        releasePlanClient.addArtifact(currentPlan.id, oldArtifact)

        when: "the updated artifact is added to the same releasePlan"
        releasePlanClient.addArtifact(currentPlan.id, newArtifact)

        then: "the releasePlan has the new artifact with the new version"
        ReleasePlan releasePlan = releasePlanClient.getCurrentReleasePlan()
        releasePlan.artifacts == [newArtifact]
    }

    def "cannot put artifacts to an activated release plan"() {
        given:
        ReleasePlanEntity currentPlan = releasePlanRepository.save(aRandom.releasePlanEntity().build())
        ArtifactInfoEntity artifact = aRandom.artifactInfoEntity().build()
        artifactInfoRepository.save(artifact)

        when:
        releasePlanClient.addArtifact(currentPlan.id, artifactInfoConverter.toApi(artifact))

        then:
        Exception e = thrown()
        e instanceof BadRequestException
    }

    def "can delete a release plan"() {
        given:
        ReleasePlanEntity plan = createCurrentReleasePlan()

        when:
        releasePlanClient.delete(plan.id)

        then:
        releasePlanRepository.findOne(plan.id) == null

        when:
        releasePlanClient.delete(plan.id)

        then:
        notThrown()
    }

    def "can delete artifacts from a release plan" () {
        given:
        ReleasePlanEntity currentPlan = createCurrentReleasePlan()
        ArtifactInfoEntity artifact1 = aRandom.artifactInfoEntity().build()
        ArtifactInfoEntity artifact2 = aRandom.artifactInfoEntity().build()
        artifactInfoRepository.save([artifact1, artifact2])

        and:
        releasePlanClient.addArtifact(currentPlan.id, artifactInfoConverter.toApi(artifact1))
        releasePlanClient.addArtifact(currentPlan.id, artifactInfoConverter.toApi(artifact2))

        when:
        releasePlanClient.deleteArtifact(currentPlan.id, artifact1.artifactId);

        then:
        ReleasePlanEntity updatedPlan = releasePlanRepository.findOne(currentPlan.id)
        updatedPlan.artifacts == [artifact2]
    }

    def "cannot delete artifacts from an activated release plan"() {
        given:
        ReleasePlanEntity currentPlan = releasePlanRepository.save(aRandom.releasePlanEntity().build())
        ArtifactInfoEntity artifact = aRandom.artifactInfoEntity().build()
        artifactInfoRepository.save(artifact)

        when:
        releasePlanClient.deleteArtifact(currentPlan.id, artifact.artifactId)

        then:
        Exception e = thrown()
        e instanceof BadRequestException
    }

    def ReleasePlanEntity createCurrentReleasePlan() {
        ReleasePlanEntity currentReleasePlan = aRandom.releasePlanEntity()
                .activated(null)
                .build()
        releasePlanRepository.save(currentReleasePlan)
    }
}
