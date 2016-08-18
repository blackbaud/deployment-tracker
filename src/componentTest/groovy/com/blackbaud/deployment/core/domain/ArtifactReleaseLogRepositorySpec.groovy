package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseLog
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseLogClient
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
    private ArtifactInfoClient artifactInfoClient

    private ArtifactInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private ArtifactInfo middleInfo = RealArtifacts.getMiddleDeploymentTrackerArtifact()
    private ArtifactInfo recentInfo = RealArtifacts.getRecentDeploymentTrackerArtifact()

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
        ArtifactReleaseInfoLogEntity entry1 = aRandom.releaseLogEntity().artifactId(earlyInfo.artifactId).buildVersion(earlyInfo.buildVersion).prevBuildVersion(middleInfo.buildVersion).build()
        ArtifactReleaseInfoLogEntity entry2 = aRandom.releaseLogEntity().artifactId(middleInfo.artifactId).buildVersion(middleInfo.buildVersion).prevBuildVersion(recentInfo.buildVersion).build()
        artifactInfoClient.update(earlyInfo.artifactId, earlyInfo.buildVersion, earlyInfo)
        artifactInfoClient.update(middleInfo.artifactId, middleInfo.buildVersion, middleInfo)
        artifactInfoClient.update(recentInfo.artifactId, recentInfo.buildVersion, recentInfo)

        when:
        artifactReleaseLogRepository.save(entry1)
        artifactReleaseLogRepository.save(entry2)

        then:
        List<ArtifactReleaseLog> expected = artifactReleaseLogClient.findAll()
        assert expected != null

    }
}
