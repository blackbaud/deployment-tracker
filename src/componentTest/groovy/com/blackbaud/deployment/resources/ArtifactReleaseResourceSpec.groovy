package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ArtifactReleaseInfoConverter
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
    private ArtifactReleaseLogRepository artifactReleaseLogRepository

    @Autowired
    private ArtifactReleaseInfoConverter converter

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
        ArtifactRelease laterRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
        artifactReleaseClient.create(foundation, space, laterRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [laterRelease]
    }

    def "findAll should find all releases"() {
        given:
        artifactReleaseClient.create(foundation, space, earlyRelease)
        ArtifactRelease laterRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
        artifactReleaseClient.create(foundation, space, laterRelease)

        expect:
        assert artifactReleaseClient.findAllBySpaceAndFoundation(foundation, space) == [laterRelease, earlyRelease]
    }

    def "remediationCreate binds new artifact release with null gitsha with existing artifact info"() {
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
        artifactReleaseClient.remediationCreate(foundation, space, [deploymentTrackerRelease])

        then:
        ArtifactRelease expectedArtifactRelease = ArtifactRelease.builder()
                .artifactId(deploymentTrackerRelease.artifactId)
                .buildVersion(deploymentTrackerRelease.buildVersion)
                .releaseVersion(deploymentTrackerRelease.releaseVersion)
                .gitSha(earlyInfo.gitSha)
                .build()
        expectedArtifactRelease == findArtifactRelease(deploymentTrackerRelease.artifactId, deploymentTrackerRelease.releaseVersion)
    }

    def "remediationCreate does not create new artifact info"() {
        when:
        artifactReleaseClient.remediationCreate(foundation, space, [earlyRelease])

        then:
        null == artifactInfoRepository.findOneByArtifactIdAndBuildVersion(earlyRelease.artifactId, earlyRelease.buildVersion)
    }

    def "remediateCreate update previous version correctly"() {
        given:
        artifactInfoClient.create(earlyInfo)
        artifactInfoClient.create(recentInfo)

        and:
        artifactReleaseClient.create(foundation, space, recentRelease)

        when:
        artifactReleaseClient.remediationCreate(foundation, space, [earlyRelease, recentRelease])

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
}
