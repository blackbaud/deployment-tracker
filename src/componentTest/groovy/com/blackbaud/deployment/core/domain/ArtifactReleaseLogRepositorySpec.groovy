package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseReportClient
import com.blackbaud.deployment.client.GitLogInfoClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

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

    private ArtifactInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private ArtifactInfo middleInfo = RealArtifacts.getMiddleDeploymentTrackerArtifact()
    private static ArtifactRelease EMPTY_ARTIFACT_RELEASE = ArtifactRelease.builder()
            .artifactId(null)
            .gitSha(null)
            .buildVersion(null)
            .releaseVersion(null)
            .build()

    def "Should be able to get a list of artifact logs"() {
        given:
        ArtifactReleaseLogEntity diffEntity = aRandom.releaseLogEntity()
                .artifactId(middleInfo.artifactId)
                .buildVersion(middleInfo.buildVersion)
                .prevBuildVersion(earlyInfo.buildVersion)
                .build()
        artifactInfoClient.update(earlyInfo.artifactId, earlyInfo.buildVersion, earlyInfo)
        artifactInfoClient.update(middleInfo.artifactId, middleInfo.buildVersion, middleInfo)
        gitLogInfoClient.post(earlyInfo.artifactId)

        and:
        ArtifactRelease earlyRelease = ArtifactRelease.builder()
                .artifactId(earlyInfo.artifactId)
                .buildVersion(earlyInfo.buildVersion)
                .releaseVersion(diffEntity.prevReleaseVersion)
                .gitSha(earlyInfo.gitSha).build()
        ArtifactRelease middleRelease = ArtifactRelease.builder()
                .artifactId(middleInfo.artifactId)
                .buildVersion(middleInfo.buildVersion)
                .releaseVersion(diffEntity.releaseVersion)
                .gitSha(middleInfo.gitSha).build()

        when:
        artifactReleaseLogRepository.save(diffEntity)

        and:
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .artifactId(middleInfo.artifactId)
                .currentRelease(middleRelease)
                .prevRelease(earlyRelease)
                .deployer(diffEntity.deployer)
                .foundation(diffEntity.foundation)
                .space(diffEntity.space)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Ryan McKay"] as Set)
                .build();
        then:
        artifactReleaseReportClient.findAll() == [expected]
    }

    def "release of new artifact should have null previous release"() {
        given:
        artifactReleaseInfoClient.update("foundation1", "int", RealArtifacts.earlyDeploymentTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
        assert artifactReleaseDiffs[0].currentRelease == RealArtifacts.earlyDeploymentTrackerRelease
        assert artifactReleaseDiffs[0].prevRelease == EMPTY_ARTIFACT_RELEASE
    }

    def "new release in same space should have correct previous release"() {
        given:
        artifactReleaseInfoClient.update("foundation1", "int", RealArtifacts.earlyDeploymentTrackerRelease)
        artifactReleaseInfoClient.update("foundation1", "int", RealArtifacts.middleDeploymentTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
        assert artifactReleaseDiffs[1].currentRelease == RealArtifacts.middleDeploymentTrackerRelease
        assert artifactReleaseDiffs[1].prevRelease == RealArtifacts.earlyDeploymentTrackerRelease
    }

    @Ignore("LUM-11663 should make this test pass")
    def "new release in different space should have correct previous release"() {
        given:
        artifactReleaseInfoClient.update("foundation1", "int", RealArtifacts.earlyDeploymentTrackerRelease)
        artifactReleaseInfoClient.update("foundation1", "dev", RealArtifacts.earlyDeploymentTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
        assert artifactReleaseDiffs.size() == 2
        assert artifactReleaseDiffs[1].currentRelease == RealArtifacts.earlyDeploymentTrackerRelease
        assert artifactReleaseDiffs[1].prevRelease == EMPTY_ARTIFACT_RELEASE
    }
}
