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
    private String space = "dev"

    private final ArtifactRelease artifactRelease = RealArtifacts.getEarlyDeploymentTrackerRelease()

    def "should add new deployment info"() {
        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactRelease in artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
    }

    def "should add new deployment info with same space and artifact id but different release version and build version"() {
        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert [artifactRelease] == artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)

        when:
        ArtifactRelease laterRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
        artifactReleaseClient.create(foundation, space, laterRelease)

        then:
        assert [laterRelease] == artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space)
    }
}
