package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.api.ClientARandom.aRandom

@ComponentTest
class ArtifactReleaseResourceSpec extends Specification{

    @Autowired
    private ArtifactReleaseClient artifactReleaseClient
    @Autowired
    private ArtifactInfoClient artifactInfoClient

    private String foundation = "pivotal"
    private String space = "currentRelease"

    private final ArtifactRelease artifactRelease = RealArtifacts.getRecentDeploymentTrackerRelease()

    def "should add new deployment info"() {
        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactRelease in artifactReleaseClient.findAllInSpace(foundation, space)
    }

    def "should add new deployment info with same space and artifact id but different release version"() {
        given:
        ArtifactRelease releaseCopy = RealArtifacts.getRecentDeploymentTrackerRelease()
        releaseCopy.releaseVersion = aRandom.text(100)

        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)
        artifactReleaseClient.create(foundation, space, releaseCopy)

        then:
        assert [artifactRelease, releaseCopy] == artifactReleaseClient.findAllInSpace(foundation, space)
    }
}
