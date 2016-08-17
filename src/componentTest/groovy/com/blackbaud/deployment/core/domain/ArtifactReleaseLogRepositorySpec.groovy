package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ComponentTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseLogRepositorySpec extends Specification {

    @Autowired
    ArtifactReleaseInfoLogRepository artifactReleaseLogRepository

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
}
