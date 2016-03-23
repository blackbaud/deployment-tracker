package com.blackbaud.deploymentstatus.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deploymentstatus.ComponentTest
import com.blackbaud.deploymentstatus.api.DeploymentStatus
import com.blackbaud.deploymentstatus.client.DeploymentStatusClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deploymentstatus.core.CoreARandom.aRandom

@ComponentTest
class DeploymentStatusResourceSpec extends Specification {

    @Autowired
    private DeploymentStatusClient deploymentStatusClient

    def "should add new status"() {
        given:
        String foundation = "pivotal"
        String space = "dev"
        DeploymentStatus status = aRandom.deploymentStatus().build()

        when:
        deploymentStatusClient.findActiveApp(foundation, space, status.appName)

        then:
        thrown(NotFoundException)

        when:
        deploymentStatusClient.createDeploymentStatus(foundation, space, status)

        then:
        assert status == deploymentStatusClient.findActiveApp(foundation, space, status.appName)
    }

}
