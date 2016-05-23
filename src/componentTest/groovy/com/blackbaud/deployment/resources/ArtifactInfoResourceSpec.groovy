package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactInfoResourceSpec extends Specification {

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    def "should add new artifact info"() {
        given:
        ArtifactInfo artifactInfo = aRandom.artifactInfo().build();

        when:
        artifactInfoClient.find(artifactInfo.artifactId, artifactInfo.buildVersion)

        then:
        thrown(NotFoundException)

        when:
        artifactInfoClient.update(artifactInfo.artifactId, artifactInfo.buildVersion, artifactInfo)

        then:
        assert artifactInfo == artifactInfoClient.find(artifactInfo.artifactId, artifactInfo.buildVersion)
    }

    def "should update existing artifact info"() {
        given:
        ArtifactInfo artifactInfoInitial = aRandom.artifactInfo().build();
        ArtifactInfo artifactInfoUpdate = aRandom.artifactInfo()
                .artifactId(artifactInfoInitial.artifactId)
                .buildVersion(artifactInfoInitial.buildVersion)
                .build();

        when:
        artifactInfoClient.update(artifactInfoInitial.artifactId, artifactInfoInitial.buildVersion, artifactInfoInitial)

        and:
        artifactInfoClient.update(artifactInfoUpdate.artifactId, artifactInfoUpdate.buildVersion, artifactInfoUpdate)

        then:
        assert artifactInfoUpdate == artifactInfoClient.find(artifactInfoInitial.artifactId, artifactInfoInitial.buildVersion)
    }

}
