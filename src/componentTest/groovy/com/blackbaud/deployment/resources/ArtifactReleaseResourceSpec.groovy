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

    def "should add new artifact release"() {
        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [artifactRelease]
    }

    def "should not blow up when duplicate artifact release is posted"() {
        given:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [artifactRelease]
    }

    def "findLatestOfEach should find the latest release"() {
        when:
        artifactReleaseClient.create(foundation, space, artifactRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [artifactRelease]

        when:
        ArtifactRelease laterRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
        artifactReleaseClient.create(foundation, space, laterRelease)

        then:
        assert artifactReleaseClient.findLatestOfEachArtifactBySpaceAndFoundation(foundation, space) == [laterRelease]
    }


    def "findAll should find all releases"() {
        given:
        artifactReleaseClient.create(foundation, space, artifactRelease)
        ArtifactRelease laterRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
        artifactReleaseClient.create(foundation, space, laterRelease)

        expect:
        assert artifactReleaseClient.findAllBySpaceAndFoundation(foundation, space) == [laterRelease, artifactRelease]
    }
}
