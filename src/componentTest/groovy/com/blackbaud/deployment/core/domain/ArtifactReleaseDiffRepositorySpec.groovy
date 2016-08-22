package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseDiffClient
import com.blackbaud.deployment.client.GitLogInfoClient
import com.blackbaud.deployment.core.domain.git.GitLogRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseDiffRepositorySpec extends Specification {

    @Autowired
    ArtifactReleaseLogRepository artifactReleaseDiffRepository

    @Autowired
    ArtifactReleaseDiffClient artifactReleaseDiffClient;

    @Autowired
    ArtifactInfoClient artifactInfoClient

    @Autowired
    GitLogRepository gitLogRepository;

    @Autowired
    GitLogInfoClient gitLogInfoClient;

    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    ArtifactInfoConverter converter;

    private ArtifactInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private ArtifactInfo middleInfo = RealArtifacts.getMiddleDeploymentTrackerArtifact()

    def "Should be able to get a list of artifact logs"() {
        given:
        ArtifactReleaseLogEntity diffEntity = aRandom.releaseLogEntity().artifactId(middleInfo.artifactId).buildVersion(middleInfo.buildVersion).prevBuildVersion(earlyInfo.buildVersion).build()
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
        artifactReleaseDiffRepository.save(diffEntity)

        and:
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .currentRelease(middleRelease)
                .prevRelease(earlyRelease)
                .deployer(diffEntity.deployer)
                .foundation(diffEntity.foundation)
                .space(diffEntity.space)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Ryan McKay"] as Set)
                .build();
        then:
        artifactReleaseDiffClient.findAll() == [expected]
    }
}
