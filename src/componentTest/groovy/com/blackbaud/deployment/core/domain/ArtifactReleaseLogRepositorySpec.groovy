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
import org.junit.BeforeClass
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseLogRepositorySpec extends Specification {

    @Autowired
    ArtifactReleaseInfoLogRepository artifactReleaseLogRepository

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

    private final ArtifactInfo deploymentTrackerArtifact = RealArtifacts.getEarlyDeploymentTrackerArtifact()

    private ArtifactInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private ArtifactInfo middleInfo = RealArtifacts.getMiddleDeploymentTrackerArtifact()

    @BeforeClass
    void setUpOnce() {
//        artifactInfoRepository.save(converter.toEntity(deploymentTrackerArtifact));
//        gitLogInfoClient.post(deploymentTrackerArtifact.artifactId)
//        gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId)
    }

    def "test" () {
        given:
        ArtifactReleaseInfoLogEntity expected = aRandom.releaseLogEntity().build()

        when:
        artifactReleaseLogRepository.save(expected)

        then:
        notThrown(Exception)

        when:
        ArtifactReleaseInfoLogPrimaryKey key = new ArtifactReleaseInfoLogPrimaryKey(expected.artifactId, expected.releaseVersion)
        ArtifactReleaseInfoLogEntity result = artifactReleaseLogRepository.findOne(key)

        then:
        result == expected
    }

    def "Should be able do get a list of artifact logs" () {
        given:
        ArtifactReleaseInfoLogEntity logEntity = aRandom.releaseLogEntity().artifactId(middleInfo.artifactId).buildVersion(middleInfo.buildVersion).prevBuildVersion(earlyInfo.buildVersion).build()
        artifactInfoClient.update(earlyInfo.artifactId, earlyInfo.buildVersion, earlyInfo)
        artifactInfoClient.update(middleInfo.artifactId, middleInfo.buildVersion, middleInfo)
        artifactInfoRepository.save(converter.toEntity(deploymentTrackerArtifact));
        gitLogInfoClient.post(deploymentTrackerArtifact.artifactId)
        gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId)

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
        List<ArtifactReleaseLog> artifactReleaseLogList = artifactReleaseLogClient.findAll()
        assert expected in artifactReleaseLogList

    }
}
