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

    private final ArtifactRelease earlyArtifactRelease = RealArtifacts.getEarlyDeploymentTrackerRelease()
    private final ArtifactRelease recentArtifactRelease = RealArtifacts.getRecentDeploymentTrackerRelease()

    def "should add new deployment info"() {
        when:
        artifactReleaseInfoClient.find(foundation, space, earlyArtifactRelease.artifactId)

        then:
        thrown(NotFoundException)

        when:
        artifactReleaseInfoClient.update(foundation, space, earlyArtifactRelease)

        then:
        assert [earlyArtifactRelease] == artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
    }

    def "should create historical log of release info on update"() {
        when:
        artifactReleaseInfoClient.update(foundation, space, earlyArtifactRelease)

        then:
        [earlyArtifactRelease] == artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)

        when:
        artifactReleaseInfoClient.update(foundation, space, recentArtifactRelease)

        then:
        [recentArtifactRelease] == artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
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
        assert [artifactReleaseInfo1] == artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
        assert [artifactReleaseInfo2] == artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, "space-two")
        assert [artifactReleaseInfo3] == artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation("foundation-three", space)
    }

    def "should retrieve most recent release info, by release version, for each artifact in a space and foundation"() {
        given:
        ArtifactRelease app1Early = RealArtifacts.getEarlyDeploymentTrackerRelease()
        ArtifactRelease app1Recent = RealArtifacts.getRecentDeploymentTrackerRelease()
        ArtifactRelease app2 = RealArtifacts.getRecentNotificationsRelease()

        when:
        artifactReleaseInfoClient.update(foundation, space, app1Early)
        artifactReleaseInfoClient.update(foundation, space, app1Recent)
        artifactReleaseInfoClient.update(foundation, space, app2)

        then:
        assert [app1Recent, app2] == artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
    }

    def "should get latest artifact based on release version"() {
        given:
        ArtifactRelease early = RealArtifacts.getEarlyDeploymentTrackerRelease()
        early.buildVersion = 2

        ArtifactRelease recent = RealArtifacts.getRecentDeploymentTrackerRelease()
        recent.buildVersion = 1

        when:
        artifactReleaseInfoClient.update(foundation, space, early)
        artifactReleaseInfoClient.update(foundation, space, recent)

        then:
        artifactReleaseInfoClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [recent]
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
        thrown(WebApplicationException)
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
