package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.ReleasePlanConverter
import com.blackbaud.deployment.api.ReleasePlan
import com.blackbaud.deployment.client.ReleasePlanClient
import com.blackbaud.deployment.core.domain.ReleasePlanEntity
import com.blackbaud.deployment.core.domain.ReleasePlanRepository
import spock.lang.Specification

import javax.inject.Inject
import javax.ws.rs.BadRequestException

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ReleasePlanResourceSpec extends Specification {

    @Inject
    private ReleasePlanClient releasePlanClient

    @Inject
    private ReleasePlanRepository releasePlanRepository

    @Inject
    private ReleasePlanConverter converter


    def "can create a new release plan"() {
        given:
        ReleasePlan newPlan = aRandom.releasePlan().build()

        when:
        ReleasePlan createdPlan = releasePlanClient.create(newPlan)

        then:
        createdPlan.notes == newPlan.notes
        createdPlan.created != null
        createdPlan.closed == null
    }

    def "can NOT create a new release plan if there is already an active one"() {
        given:
        createActiveReleasePlan()

        and:
        ReleasePlan newPlan = aRandom.releasePlan().build()

        when:
        ReleasePlan createdPlan = releasePlanClient.create(newPlan)

        then:
        Exception exception = thrown()
        exception instanceof BadRequestException
    }


    def "can get active release plan if one exists"(){
        given:
        ReleasePlanEntity active = createActiveReleasePlan()

        when:
        ReleasePlanEntity releasePlan = releasePlanRepository.findByClosedNull()

        then:
        active == releasePlan
    }

    def createActiveReleasePlan() {
        ReleasePlanEntity activeReleasePlan = aRandom.releasePlanEntity()
                .closed(null)
                .build()
        releasePlanRepository.save(activeReleasePlan)
        releasePlanRepository.findByClosedNull()
    }
}
