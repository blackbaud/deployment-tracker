package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseLog
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseLogClient
import com.blackbaud.deployment.client.GitLogInfoClient
import com.blackbaud.deployment.core.domain.git.GitLogRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseLogRepositorySpec extends Specification {

    @Autowired
    ArtifactReleaseLogRepository artifactReleaseLogRepository

    @Autowired
    ArtifactReleaseLogClient artifactReleaseLogClient;

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

    def "Should be able do get a list of artifact logs"() {
        given:
        ArtifactReleaseLogEntity logEntity = aRandom.releaseLogEntity().artifactId(middleInfo.artifactId).buildVersion(middleInfo.buildVersion).prevBuildVersion(earlyInfo.buildVersion).build()
        artifactInfoClient.update(earlyInfo.artifactId, earlyInfo.buildVersion, earlyInfo)
        artifactInfoClient.update(middleInfo.artifactId, middleInfo.buildVersion, middleInfo)
        gitLogInfoClient.post(earlyInfo.artifactId)

        when:
        artifactReleaseLogRepository.save(logEntity)

        and:
        ArtifactReleaseLog expected = ArtifactReleaseLog.builder()
                .artifactId(logEntity.artifactId)
                .buildVersion(logEntity.buildVersion)
                .prevBuildVersion(logEntity.prevBuildVersion)
                .releaseVersion(logEntity.releaseVersion)
                .prevReleaseVersion(logEntity.prevReleaseVersion)
                .deployer(logEntity.deployer)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Ryan McKay"] as Set)
                .build();
        then:
        artifactReleaseLogClient.findAll() == [expected]
    }
}
