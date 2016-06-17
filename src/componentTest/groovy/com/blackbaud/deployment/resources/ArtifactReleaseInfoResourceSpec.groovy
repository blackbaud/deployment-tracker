package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseInfo
import com.blackbaud.deployment.api.RandomArtifactReleaseInfoBuilder
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseInfoResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseInfoClient artifactReleaseInfoClient
    @Autowired
    private ArtifactInfoClient artifactInfoClient

    private String foundation = "pivotal"
    private String space = "dev"

    def "should add new deployment info"() {
        given:
        ArtifactReleaseInfo artifactReleaseInfo = aRandom.artifactReleaseInfo().build()

        when:
        artifactReleaseInfoClient.find(foundation, space, artifactReleaseInfo.artifactId)

        then:
        thrown(NotFoundException)

        when:
        artifactReleaseInfoClient.update(foundation, space, artifactReleaseInfo)

        then:
        assert artifactReleaseInfo == artifactReleaseInfoClient.find(foundation, space, artifactReleaseInfo.artifactId)
    }

    def "should replace deployment info on update"() {
        given:
        RandomArtifactReleaseInfoBuilder deploymentStatusBuilder = aRandom.artifactReleaseInfo()
        ArtifactReleaseInfo initialStatus = deploymentStatusBuilder.build()
        ArtifactReleaseInfo updatedStatus = deploymentStatusBuilder.buildVersion("updated-version").build()

        when:
        artifactReleaseInfoClient.update(foundation, space, initialStatus)
        artifactReleaseInfoClient.update(foundation, space, updatedStatus)

        then:
        ArtifactReleaseInfo activeStatus = artifactReleaseInfoClient.find(foundation, space, updatedStatus.artifactId)
        assert initialStatus != activeStatus
        assert updatedStatus == activeStatus
    }

    def "should track distinct deployment info by foundation and space"() {
        given:
        ArtifactReleaseInfo artifactReleaseInfo1 = aRandom.artifactReleaseInfo().artifactId("app").build()
        ArtifactReleaseInfo artifactReleaseInfo2 = aRandom.artifactReleaseInfo().artifactId("app").build()
        ArtifactReleaseInfo artifactReleaseInfo3 = aRandom.artifactReleaseInfo().artifactId("app").build()

        when:
        artifactReleaseInfoClient.update(foundation, space, artifactReleaseInfo1)
        artifactReleaseInfoClient.update(foundation, "space-two", artifactReleaseInfo2)
        artifactReleaseInfoClient.update("foundation-three", space, artifactReleaseInfo3)

        then:
        assert artifactReleaseInfo1 == artifactReleaseInfoClient.find(foundation, space, "app")
        assert artifactReleaseInfo2 == artifactReleaseInfoClient.find(foundation, "space-two", "app")
        assert artifactReleaseInfo3 == artifactReleaseInfoClient.find("foundation-three", space, "app")
    }

    def "should retrieve all app deployment info objects for a space"() {
        given:
        ArtifactReleaseInfo app1Status = aRandom.artifactReleaseInfo().artifactId("app1").build()
        ArtifactReleaseInfo app2Status = aRandom.artifactReleaseInfo().artifactId("app2").build()
        ArtifactReleaseInfo otherSpaceApp = aRandom.artifactReleaseInfo().artifactId("other-space").build()

        when:
        artifactReleaseInfoClient.update(foundation, space, app1Status)
        artifactReleaseInfoClient.update(foundation, space, app2Status)
        artifactReleaseInfoClient.update(foundation, "other-space", otherSpaceApp)

        then:
        assert [app1Status, app2Status] == artifactReleaseInfoClient.findAllInSpace(foundation, space)
    }

    def "should populate artifact info resource" (){
        given:
        ArtifactReleaseInfo artifactReleaseInfo = aRandom.artifactReleaseInfo().build()
        ArtifactInfo matchingArtifactInfo = ArtifactInfo.builder()
                .artifactId(artifactReleaseInfo.artifactId)
                .buildVersion(artifactReleaseInfo.buildVersion)
                .gitSha(artifactReleaseInfo.gitSha)
                .build()

        when:
        artifactReleaseInfoClient.update(foundation, space, artifactReleaseInfo)

        then:
        assert artifactInfoClient.find(artifactReleaseInfo.artifactId, artifactReleaseInfo.buildVersion) == matchingArtifactInfo
    }

}
