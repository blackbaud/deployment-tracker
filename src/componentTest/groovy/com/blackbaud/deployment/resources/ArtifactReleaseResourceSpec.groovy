package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.deployment.ArtifactReleaseConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseClient
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogPrimaryKey
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogRepository
import com.blackbaud.deployment.core.domain.ReleaseService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import javax.ws.rs.WebApplicationException

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseClient artifactReleaseClient

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository

    @Autowired
    private ArtifactReleaseLogRepository artifactReleaseLogRepository

    @Autowired
    private ArtifactReleaseConverter converter

    private String foundation = "pivotal"
    private String space = "dev"

    private final ArtifactRelease earlyRelease = RealArtifacts.getEarlyDeploymentTrackerRelease()
    private final ArtifactRelease recentRelease = RealArtifacts.getRecentDeploymentTrackerRelease()

    private final ArtifactInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private final ArtifactInfo recentInfo = RealArtifacts.getRecentDeploymentTrackerArtifact()

    def "should add new artifact release"() {
        when:
        artifactReleaseClient.create(foundation, space, earlyRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [earlyRelease]
    }

    def "should not blow up when duplicate artifact release is posted"() {
        given:
        artifactReleaseClient.create(foundation, space, earlyRelease)

        when:
        artifactReleaseClient.create(foundation, space, earlyRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [earlyRelease]
    }

    def "findLatestOfEach should find the latest release"() {
        when:
        artifactReleaseClient.create(foundation, space, earlyRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [earlyRelease]

        when:
        artifactReleaseClient.create(foundation, space, recentRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [recentRelease]
    }

    def "should track distinct deployment info by foundation and space"() {
        given:
        String distinctSpace = aRandom.text(8)
        String distinctFoundation = aRandom.text(8)
        ArtifactRelease artifactReleaseInfo1 = RealArtifacts.getRecentDeploymentTrackerRelease()
        ArtifactRelease artifactReleaseInfo2 = RealArtifacts.getRecentNotificationsRelease()
        ArtifactRelease artifactReleaseInfo3 = RealArtifacts.getBluemoonDojoRelease()

        when:
        artifactReleaseClient.create(foundation, space, artifactReleaseInfo1)
        artifactReleaseClient.create(foundation, distinctSpace, artifactReleaseInfo2)
        artifactReleaseClient.create(distinctFoundation, space, artifactReleaseInfo3)

        then:
        assert [artifactReleaseInfo1] == artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
        assert [artifactReleaseInfo2] == artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, distinctSpace)
        assert [artifactReleaseInfo3] == artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(distinctFoundation, space)
    }

    def "should retrieve most recent release info, by release version, for each artifact in a space and foundation"() {
        given:
        ArtifactRelease app1Early = RealArtifacts.getEarlyDeploymentTrackerRelease()
        ArtifactRelease app1Recent = RealArtifacts.getRecentDeploymentTrackerRelease()
        ArtifactRelease app2 = RealArtifacts.getRecentNotificationsRelease()

        when:
        artifactReleaseClient.create(foundation, space, app1Early)
        artifactReleaseClient.create(foundation, space, app1Recent)
        artifactReleaseClient.create(foundation, space, app2)

        then:
        assert [app1Recent, app2] == artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
    }

    def "findAll should find all releases"() {
        given:
        artifactReleaseClient.create(foundation, space, earlyRelease)
        artifactReleaseClient.create(foundation, space, recentRelease)

        expect:
        assert artifactReleaseClient.findAllBySpaceAndFoundation(foundation, space) == [recentRelease, earlyRelease]
    }

    def "remediate binds new artifact release with null gitsha with existing artifact info"() {
        given:
        artifactInfoClient.create(earlyInfo)

        and:
        ArtifactRelease deploymentTrackerRelease = ArtifactRelease.builder()
                .artifactId(earlyInfo.artifactId)
                .buildVersion(earlyInfo.buildVersion)
                .releaseVersion("release version")
                .gitSha(null)
                .build()

        when:
        artifactReleaseClient.remediate(foundation, space, [deploymentTrackerRelease])

        then:
        ArtifactRelease expectedArtifactRelease = ArtifactRelease.builder()
                .artifactId(deploymentTrackerRelease.artifactId)
                .buildVersion(deploymentTrackerRelease.buildVersion)
                .releaseVersion(deploymentTrackerRelease.releaseVersion)
                .gitSha(earlyInfo.gitSha)
                .build()
        expectedArtifactRelease == findArtifactRelease(deploymentTrackerRelease.artifactId, deploymentTrackerRelease.releaseVersion)
    }

    def "remediate does not create new artifact info"() {
        when:
        artifactReleaseClient.remediate(foundation, space, [earlyRelease])

        then:
        null == artifactInfoRepository.findOneByArtifactIdAndBuildVersion(earlyRelease.artifactId, earlyRelease.buildVersion)
    }

    def "remediate updates previous version correctly"() {
        given:
        artifactInfoClient.create(earlyInfo)
        artifactInfoClient.create(recentInfo)

        and:
        artifactReleaseClient.create(foundation, space, recentRelease)

        when:
        artifactReleaseClient.remediate(foundation, space, [earlyRelease, recentRelease])

        then:
        ArtifactReleaseLogEntity earlyReleaseLog = artifactReleaseLogRepository.findOne(new ArtifactReleaseLogPrimaryKey(earlyRelease.artifactId, earlyRelease.releaseVersion))
        earlyReleaseLog.prevBuildVersion == null
        earlyReleaseLog.prevReleaseVersion == null

        ArtifactReleaseLogEntity recentReleaseLog = artifactReleaseLogRepository.findOne(new ArtifactReleaseLogPrimaryKey(recentRelease.artifactId, recentRelease.releaseVersion))
        recentReleaseLog.prevBuildVersion == earlyReleaseLog.buildVersion
        recentReleaseLog.prevReleaseVersion == earlyReleaseLog.releaseVersion
    }

    private ArtifactRelease findArtifactRelease(String artifactId, String releaseVersion) {
        ArtifactReleaseLogEntity entity = artifactReleaseLogRepository.findOne(new ArtifactReleaseLogPrimaryKey(artifactId, releaseVersion))
        converter.toApi(entity)
    }

    def "Missing commits throws exception"() {
        given:
        String invalidSha = aRandom.text(10)
        ArtifactRelease dev = aRandom.artifactReleaseInfo()
                .artifactId("deployment-tracker")
                .gitSha(invalidSha)
                .build()

        when:
        artifactReleaseClient.create(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, dev)

        then:
        thrown(WebApplicationException)
    }

    def "Invalid github repo throws exception"() {
        given:
        ArtifactRelease artifactReleaseInfo = aRandom.artifactReleaseInfo().build();

        when:
        artifactReleaseClient.create(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, artifactReleaseInfo)

        then:
        BadRequestException ex = thrown()
        ex.errorEntity.message == "Cannot find repository with name " + artifactReleaseInfo.artifactId
    }

    def "saving bluemoon-ui artifact release should save latest segmentation-component artifact as a dependency"() {
        given: "segmentation-component artifact info"
        ArtifactInfo segComp = RealArtifacts.recentSegmentationComponentArtifact

        and:
        ArtifactRelease bluemoonUi = RealArtifacts.recentBluemoonUiRelease
        bluemoonUi.dependencies = [segComp]

        when:
        artifactReleaseClient.create(foundation, space, bluemoonUi)

        then:
        ArtifactRelease bluemoonUiRelease = artifactReleaseClient.find(foundation, space, bluemoonUi.artifactId)
        bluemoonUiRelease.dependencies == [segComp]
    }
}
