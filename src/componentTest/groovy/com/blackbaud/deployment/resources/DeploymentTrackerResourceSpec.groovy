package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.DeploymentInfo
import com.blackbaud.deployment.api.RandomDeploymentInfoBuilder
import com.blackbaud.deployment.client.DeploymentTrackerClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class DeploymentTrackerResourceSpec extends Specification {

    @Autowired
    private DeploymentTrackerClient deploymentInfoClient
    private String foundation = "pivotal"
    private String space = "dev"

    def "should add new status"() {
        given:
        DeploymentInfo status = aRandom.deploymentInfo().build()

        when:
        deploymentInfoClient.find(foundation, space, status.artifactId)

        then:
        thrown(NotFoundException)

        when:
        deploymentInfoClient.update(foundation, space, status)

        then:
        assert status == deploymentInfoClient.find(foundation, space, status.artifactId)
    }

    def "should replace status on update"() {
        given:
        RandomDeploymentInfoBuilder deploymentStatusBuilder = aRandom.deploymentInfo()
        DeploymentInfo initialStatus = deploymentStatusBuilder.build()
        DeploymentInfo updatedStatus = deploymentStatusBuilder.buildVersion("updated-version").build()

        when:
        deploymentInfoClient.update(foundation, space, initialStatus)
        deploymentInfoClient.update(foundation, space, updatedStatus)

        then:
        DeploymentInfo activeStatus = deploymentInfoClient.find(foundation, space, updatedStatus.artifactId)
        assert initialStatus != activeStatus
        assert updatedStatus == activeStatus
    }

    def "should track distinct status by foundation and space"() {
        given:
        DeploymentInfo status1 = aRandom.deploymentInfo().artifactId("app").build()
        DeploymentInfo status2 = aRandom.deploymentInfo().artifactId("app").build()
        DeploymentInfo status3 = aRandom.deploymentInfo().artifactId("app").build()

        when:
        deploymentInfoClient.update(foundation, space, status1)
        deploymentInfoClient.update(foundation, "space-two", status2)
        deploymentInfoClient.update("foundation-three", space, status3)

        then:
        assert status1 == deploymentInfoClient.find(foundation, space, "app")
        assert status2 == deploymentInfoClient.find(foundation, "space-two", "app")
        assert status3 == deploymentInfoClient.find("foundation-three", space, "app")
    }

    def "should retrieve all app status objects for a space"() {
        given:
        DeploymentInfo app1Status = aRandom.deploymentInfo().artifactId("app1").build()
        DeploymentInfo app2Status = aRandom.deploymentInfo().artifactId("app2").build()
        DeploymentInfo otherSpaceApp = aRandom.deploymentInfo().artifactId("other-space").build()

        when:
        deploymentInfoClient.update(foundation, space, app1Status)
        deploymentInfoClient.update(foundation, space, app2Status)
        deploymentInfoClient.update(foundation, "other-space", otherSpaceApp)

        then:
        assert [app1Status, app2Status] == deploymentInfoClient.findAllInSpace(foundation, space)
    }

}
