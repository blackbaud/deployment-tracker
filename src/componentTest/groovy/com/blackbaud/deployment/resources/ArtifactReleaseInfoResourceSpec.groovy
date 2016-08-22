package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
import com.blackbaud.deployment.core.domain.ReleaseService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import javax.ws.rs.WebApplicationException

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseInfoResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseInfoClient artifactReleaseInfoClient
    @Autowired
    private ArtifactInfoClient artifactInfoClient

    private String foundation = "pivotal"
    private String space = "dev"

    private final ArtifactRelease artifactRelease = RealArtifacts.getRecentDeploymentTrackerRelease()

    def "should add new deployment info"() {
        when:
        artifactReleaseInfoClient.find(foundation, space, artifactRelease.artifactId)

        then:
        thrown(NotFoundException)

        when:
        artifactReleaseInfoClient.update(foundation, space, artifactRelease)

        then:
        assert artifactRelease == artifactReleaseInfoClient.find(foundation, space, artifactRelease.artifactId)
    }

    def "should replace deployment info on update"() {
        given:
        ArtifactRelease initialStatus = artifactRelease
        ArtifactRelease updatedStatus = aRandom.artifactReleaseInfo()
                .artifactId(artifactRelease.artifactId)
                .gitSha(artifactRelease.gitSha)
                .buildVersion("0.20160607.100000")
                .build()

        when:
        artifactReleaseInfoClient.update(foundation, space, initialStatus)
        artifactReleaseInfoClient.update(foundation, space, updatedStatus)

        then:
        ArtifactRelease activeStatus = artifactReleaseInfoClient.find(foundation, space, updatedStatus.artifactId)
        assert initialStatus != activeStatus
        assert updatedStatus == activeStatus
    }

    def "should track distinct deployment info by foundation and space"() {
        given:
        ArtifactRelease artifactReleaseInfo1 = RealArtifacts.getRecentDeploymentTrackerRelease()
        ArtifactRelease artifactReleaseInfo2 = RealArtifacts.getRecentNotificationsRelease()
        ArtifactRelease artifactReleaseInfo3 = RealArtifacts.getBluemoonDojoRelease()

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
        ArtifactRelease app1Status = RealArtifacts.getRecentDeploymentTrackerRelease()
        ArtifactRelease app2Status = RealArtifacts.getRecentNotificationsRelease()
        ArtifactRelease otherSpaceApp = RealArtifacts.getRecentNotificationsRelease()
        otherSpaceApp.releaseVersion = '0.20160606.200000';

        when:
        artifactReleaseInfoClient.update(foundation, space, app1Status)
        artifactReleaseInfoClient.update(foundation, space, app2Status)
        artifactReleaseInfoClient.update(foundation, "other-space", otherSpaceApp)

        then:
        assert [app1Status, app2Status] == artifactReleaseInfoClient.findAllInSpace(foundation, space)
    }

    def "should populate artifact info resource" (){
        given:
        ArtifactRelease artifactReleaseInfo = RealArtifacts.getEarlyDeploymentTrackerRelease()
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

    def "Missing commits throws exception"() {
        given:
        ArtifactRelease dev = aRandom.artifactReleaseInfo()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160606.194525")
                .gitSha("3e176dced48f7b888707337261ba5b97902cf5b8")
                .build()

        when:
        artifactReleaseInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, dev)

        then:
        WebApplicationException ex = thrown()
    }

    def "Invalid github repo throws exception"() {
        given:
        ArtifactRelease artifactReleaseInfo = aRandom.artifactReleaseInfo().build();

        when:
        artifactReleaseInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, artifactReleaseInfo)

        then:
        BadRequestException ex = thrown()
        ex.errorEntity.message == "Cannot find repository with name " + artifactReleaseInfo.artifactId
    }
}
