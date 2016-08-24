package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseReportClient
import com.blackbaud.deployment.client.GitLogInfoClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.ZonedDateTime

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseLogRepositorySpec extends Specification {

    @Autowired
    ArtifactReleaseLogRepository artifactReleaseLogRepository

    @Autowired
    ArtifactReleaseReportClient artifactReleaseReportClient;

    @Autowired
    ArtifactInfoClient artifactInfoClient

    @Autowired
    ArtifactReleaseInfoClient artifactReleaseInfoClient

    @Autowired
    GitLogInfoClient gitLogInfoClient;

    private ArtifactRelease earlyRelease = RealArtifacts.earlyDeploymentTrackerRelease
    private ArtifactRelease middleRelease = RealArtifacts.middleDeploymentTrackerRelease
    private ArtifactRelease recentRelease = RealArtifacts.recentDeploymentTrackerRelease
    private static ArtifactRelease EMPTY_ARTIFACT_RELEASE = ArtifactRelease.builder()
            .artifactId(null)
            .gitSha(null)
            .buildVersion(null)
            .releaseVersion(null)
            .build()

    def "Should be able to get a list of artifact logs"() {
        given:
        ArtifactReleaseLogEntity logEntity = aRandom.releaseLogEntity()
                .artifactId(middleRelease.artifactId)
                .buildVersion(middleRelease.buildVersion)
                .prevBuildVersion(middleRelease.buildVersion)
                .build()
        artifactReleaseInfoClient.update(logEntity.foundation, logEntity.space, earlyRelease)
        artifactReleaseInfoClient.update(logEntity.foundation, logEntity.space, middleRelease)
        gitLogInfoClient.post(middleRelease.artifactId)

        when:
        artifactReleaseLogRepository.save(logEntity)

        and:
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .artifactId(middleRelease.artifactId)
                .currentRelease(middleRelease)
                .prevRelease(earlyRelease)
                .deployer("")
                .foundation(logEntity.foundation)
                .space(logEntity.space)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Ryan McKay"] as Set)
                .releaseDate(ZonedDateTime.parse("2016-06-05T00:00Z[UTC]"))
                .build();
                             then:
        expected in artifactReleaseReportClient.findAll()
    }

    def "release of new artifact should have null previous release"() {
        given:
        artifactReleaseInfoClient.update("foundation1", "int", earlyRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
        assert artifactReleaseDiffs[0].currentRelease == earlyRelease
        assert artifactReleaseDiffs[0].prevRelease == EMPTY_ARTIFACT_RELEASE
    }

    def "new release in same space should have correct previous release"() {
        given:
        artifactReleaseInfoClient.update("foundation1", "int", earlyRelease)
        artifactReleaseInfoClient.update("foundation1", "int", middleRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
        assert artifactReleaseDiffs[1].currentRelease == middleRelease
        assert artifactReleaseDiffs[1].prevRelease == earlyRelease
    }

    def "new release in different space should have correct previous release"() {
        given:
        artifactReleaseInfoClient.update("foundation1", "int", earlyRelease)
        artifactReleaseInfoClient.update("foundation1", "dev", recentRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
        assert artifactReleaseDiffs[0].prevRelease == EMPTY_ARTIFACT_RELEASE
        assert artifactReleaseDiffs[1].prevRelease == EMPTY_ARTIFACT_RELEASE
    }
}
