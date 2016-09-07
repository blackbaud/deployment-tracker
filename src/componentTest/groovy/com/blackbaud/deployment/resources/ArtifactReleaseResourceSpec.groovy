package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ArtifactReleaseInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseClient
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogPrimaryKey
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class ArtifactReleaseResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseClient artifactReleaseClient

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository

    @Autowired
    private ArtifactReleaseInfoClient artifactReleaseInfoClient

    @Autowired
    private ArtifactReleaseLogRepository artifactReleaseLogRepository

    @Autowired
    private ArtifactReleaseInfoConverter converter

    private String foundation = "pivotal"
    private String space = "dev"

    private final ArtifactRelease artifactRelease = RealArtifacts.getEarlyDeploymentTrackerRelease()
    private final ArtifactInfo deploymentTrackerInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()

    def "should add new artifact release"() {
        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [artifactRelease]
    }

    def "should not blow up when duplicate artifact release is posted"() {
        given:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [artifactRelease]
    }

    def "findLatestOfEach should find the latest release"() {
        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [artifactRelease]

        when:
        ArtifactRelease laterRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
        artifactReleaseClient.create(foundation, space, laterRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [laterRelease]
    }


    def "findAll should find all releases"() {
        given:
        artifactReleaseClient.create(foundation, space, artifactRelease)
        ArtifactRelease laterRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
        artifactReleaseClient.create(foundation, space, laterRelease)

        expect:
        assert artifactReleaseClient.findAllBySpaceAndFoundation(foundation, space) == [laterRelease, artifactRelease]
    }

    def "remediationCreate binds new artifact release with null gitsha with existing artifact info if exists"() {
        given:
        artifactInfoClient.create(deploymentTrackerInfo)

        and:
        ArtifactRelease deploymentTrackerRelease = ArtifactRelease.builder()
                .artifactId(deploymentTrackerInfo.artifactId)
                .buildVersion(deploymentTrackerInfo.buildVersion)
                .releaseVersion("release version")
                .gitSha(null)
                .build()

        when:
        artifactReleaseClient.remediationCreate(foundation, space, [deploymentTrackerRelease])

        then:
        ArtifactRelease expectedArtifactRelease = ArtifactRelease.builder()
                .artifactId(deploymentTrackerRelease.artifactId)
                .buildVersion(deploymentTrackerRelease.buildVersion)
                .releaseVersion(deploymentTrackerRelease.releaseVersion)
                .gitSha(deploymentTrackerInfo.gitSha)
                .build()
        ArtifactReleaseLogEntity actual = artifactReleaseLogRepository.findOne(new ArtifactReleaseLogPrimaryKey(deploymentTrackerRelease.artifactId, deploymentTrackerRelease.releaseVersion))
        converter.toApi(actual) == expectedArtifactRelease
    }

    def "remediationCreate does not create new artifact info if artifactRelease has no corresponding artifactInfo"() {
        when:
        artifactReleaseClient.remediationCreate(foundation, space, [artifactRelease])

        then:
        null == artifactInfoRepository.findOneByArtifactIdAndBuildVersion(artifactRelease.artifactId, artifactRelease.buildVersion)
    }
}
