package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ComponentTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseLogRepositorySpec extends Specification {

    @Autowired
    ArtifactReleaseLogRepository artifactReleaseLogRepository

    def "test" () {
        given:
        ArtifactReleaseLogEntity expected = aRandom.releaseLogEntity().build()

        when:
        artifactReleaseLogRepository.save(expected)

        then:
        notThrown(Exception)

        when:
        ArtifactReleaseLogPrimaryKey key = new ArtifactReleaseLogPrimaryKey(expected.artifactId, expected.releaseVersion)
        ArtifactReleaseLogEntity result = artifactReleaseLogRepository.findOne(key)

        then:
        result == expected

    }
}
