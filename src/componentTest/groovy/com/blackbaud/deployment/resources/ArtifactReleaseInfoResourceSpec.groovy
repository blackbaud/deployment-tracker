package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseInfo
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

    private final ArtifactReleaseInfo newArtifact = RealArtifacts.getRecentDeploymentTrackerRelease()

    def "should add new deployment info"() {
        when:
        artifactReleaseInfoClient.find(foundation, space, newArtifact.artifactId)

        then:
        thrown(NotFoundException)

        when:
        artifactReleaseInfoClient.update(foundation, space, newArtifact)

        then:
        assert newArtifact == artifactReleaseInfoClient.find(foundation, space, newArtifact.artifactId)
    }

    def "should replace deployment info on update"() {
        given:
        ArtifactReleaseInfo initialStatus = newArtifact
        ArtifactReleaseInfo updatedStatus = aRandom.artifactReleaseInfo()
                .artifactId(newArtifact.artifactId)
                .gitSha(newArtifact.gitSha)
                .buildVersion("updated-version")
                .build()

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
        ArtifactReleaseInfo artifactReleaseInfo1 = RealArtifacts.getRecentDeploymentTrackerRelease()
        ArtifactReleaseInfo artifactReleaseInfo2 = RealArtifacts.getRecentNotificationsRelease()
        ArtifactReleaseInfo artifactReleaseInfo3 = RealArtifacts.getBluemoonDojoRelease()

        when:
        artifactReleaseInfoClient.update(foundation, space, artifactReleaseInfo1)
        artifactReleaseInfoClient.update(foundation, "space-two", artifactReleaseInfo2)
        artifactReleaseInfoClient.update("foundation-three", space, artifactReleaseInfo3)

        then:
        assert artifactReleaseInfo1 == artifactReleaseInfoClient.find(foundation, space, artifactReleaseInfo1.artifactId)
        assert artifactReleaseInfo2 == artifactReleaseInfoClient.find(foundation, "space-two", artifactReleaseInfo2.artifactId)
        assert artifactReleaseInfo3 == artifactReleaseInfoClient.find("foundation-three", space, artifactReleaseInfo3.artifactId)
    }

    def "should retrieve all app deployment info objects for a space"() {
        given:
        ArtifactReleaseInfo app1Status = RealArtifacts.getRecentDeploymentTrackerRelease()
        ArtifactReleaseInfo app2Status = RealArtifacts.getRecentNotificationsRelease()
        ArtifactReleaseInfo otherSpaceApp = RealArtifacts.getRecentNotificationsRelease()

        when:
        artifactReleaseInfoClient.update(foundation, space, app1Status)
        artifactReleaseInfoClient.update(foundation, space, app2Status)
        artifactReleaseInfoClient.update(foundation, "other-space", otherSpaceApp)

        then:
        assert [app1Status, app2Status] == artifactReleaseInfoClient.findAllInSpace(foundation, space)
    }

    def "should populate artifact info resource" (){
        given:
        ArtifactReleaseInfo artifactReleaseInfo = RealArtifacts.getEarlyDeploymentTrackerRelease()
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
