package com.blackbaud.deploymentstatus.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deploymentstatus.ComponentTest
import com.blackbaud.deploymentstatus.api.DeploymentStatus
import com.blackbaud.deploymentstatus.api.RandomDeploymentStatusBuilder
import com.blackbaud.deploymentstatus.client.DeploymentStatusClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deploymentstatus.core.CoreARandom.aRandom

@ComponentTest
class DeploymentStatusResourceSpec extends Specification {

    @Autowired
    private DeploymentStatusClient deploymentStatusClient
    private String foundation = "pivotal"
    private String space = "dev"

    def "should add new status"() {
        given:
        DeploymentStatus status = aRandom.deploymentStatus().build()

        when:
        deploymentStatusClient.find(foundation, space, status.appName)

        then:
        thrown(NotFoundException)

        when:
        deploymentStatusClient.update(foundation, space, status)

        then:
        assert status == deploymentStatusClient.find(foundation, space, status.appName)
    }

    def "should replace status on update"() {
        given:
        RandomDeploymentStatusBuilder deploymentStatusBuilder = aRandom.deploymentStatus()
        DeploymentStatus initialStatus = deploymentStatusBuilder.build()
        DeploymentStatus updatedStatus = deploymentStatusBuilder.buildVersion("updated-version").build()

        when:
        deploymentStatusClient.update(foundation, space, initialStatus)
        deploymentStatusClient.update(foundation, space, updatedStatus)

        then:
        DeploymentStatus activeStatus = deploymentStatusClient.find(foundation, space, updatedStatus.appName)
        assert initialStatus != activeStatus
        assert updatedStatus == activeStatus
    }

    def "should track distinct status by foundation and space"() {
        given:
        DeploymentStatus status1 = aRandom.deploymentStatus().appName("app").build()
        DeploymentStatus status2 = aRandom.deploymentStatus().appName("app").build()
        DeploymentStatus status3 = aRandom.deploymentStatus().appName("app").build()

        when:
        deploymentStatusClient.update(foundation, space, status1)
        deploymentStatusClient.update(foundation, "space-two", status2)
        deploymentStatusClient.update("foundation-three", space, status3)

        then:
        assert status1 == deploymentStatusClient.find(foundation, space, "app")
        assert status2 == deploymentStatusClient.find(foundation, "space-two", "app")
        assert status3 == deploymentStatusClient.find("foundation-three", space, "app")
    }

    def "should retrieve all app status objects for a space"() {
        given:
        DeploymentStatus app1Status = aRandom.deploymentStatus().appName("app1").build()
        DeploymentStatus app2Status = aRandom.deploymentStatus().appName("app2").build()
        DeploymentStatus otherSpaceApp = aRandom.deploymentStatus().appName("other-space").build()

        when:
        deploymentStatusClient.update(foundation, space, app1Status)
        deploymentStatusClient.update(foundation, space, app2Status)
        deploymentStatusClient.update(foundation, "other-space", otherSpaceApp)

        then:
        assert [app1Status, app2Status] == deploymentStatusClient.findAllInSpace(foundation, space)
    }

}
