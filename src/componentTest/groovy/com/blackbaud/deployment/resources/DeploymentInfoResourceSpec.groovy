package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.DeploymentInfo
import com.blackbaud.deployment.api.RandomDeploymentInfoBuilder
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.DeploymentInfoClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class DeploymentInfoResourceSpec extends Specification {

    @Autowired
    private DeploymentInfoClient deploymentInfoClient
    @Autowired
    private ArtifactInfoClient artifactInfoClient

    private String foundation = "pivotal"
    private String space = "dev"

    def "should add new deployment info"() {
        given:
        DeploymentInfo deploymentInfo = aRandom.deploymentInfo().build()

        when:
        deploymentInfoClient.find(foundation, space, deploymentInfo.artifactId)

        then:
        thrown(NotFoundException)

        when:
        deploymentInfoClient.update(foundation, space, deploymentInfo)

        then:
        assert deploymentInfo == deploymentInfoClient.find(foundation, space, deploymentInfo.artifactId)
    }

    def "should replace deployment info on update"() {
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

    def "should track distinct deployment info by foundation and space"() {
        given:
        DeploymentInfo deploymentInfo1 = aRandom.deploymentInfo().artifactId("app").build()
        DeploymentInfo deploymentInfo2 = aRandom.deploymentInfo().artifactId("app").build()
        DeploymentInfo deploymentInfo3 = aRandom.deploymentInfo().artifactId("app").build()

        when:
        deploymentInfoClient.update(foundation, space, deploymentInfo1)
        deploymentInfoClient.update(foundation, "space-two", deploymentInfo2)
        deploymentInfoClient.update("foundation-three", space, deploymentInfo3)

        then:
        assert deploymentInfo1 == deploymentInfoClient.find(foundation, space, "app")
        assert deploymentInfo2 == deploymentInfoClient.find(foundation, "space-two", "app")
        assert deploymentInfo3 == deploymentInfoClient.find("foundation-three", space, "app")
    }

    def "should retrieve all app deployment info objects for a space"() {
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

    def "should populate artifact info resource" (){
        given:
        DeploymentInfo deploymentInfo = aRandom.deploymentInfo().build()
        ArtifactInfo matchingArtifactInfo = ArtifactInfo.builder()
                .artifactId(deploymentInfo.artifactId)
                .buildVersion(deploymentInfo.buildVersion)
                .gitSha(deploymentInfo.gitSha)
                .build()

        when:
        deploymentInfoClient.update(foundation, space, deploymentInfo)

        then:
        assert artifactInfoClient.find(deploymentInfo.artifactId, deploymentInfo.buildVersion) == matchingArtifactInfo
    }

}
