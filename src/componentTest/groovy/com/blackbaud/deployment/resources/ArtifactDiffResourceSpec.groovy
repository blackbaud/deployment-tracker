package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactDiffClient
import com.blackbaud.deployment.client.ArtifactInfoClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactDiffResourceSpec extends Specification {

    @Autowired
    private ArtifactInfoClient artifactInfoClient
    @Autowired
    private ArtifactDiffClient artifactDiffClient

    def "should get diff happy path"() {
        given:
        ArtifactInfo artifactInfoOlder = aRandom.artifactInfo().build();
        ArtifactInfo artifactInfoNewer = aRandom.artifactInfo()
                .artifactId(artifactInfoOlder.artifactId)
                .build();

        when:
        artifactInfoClient.update(artifactInfoOlder.artifactId, artifactInfoOlder.buildVersion, artifactInfoOlder)

        and:
        artifactInfoClient.update(artifactInfoNewer.artifactId, artifactInfoNewer.buildVersion, artifactInfoNewer)

        then:
        assert artifactDiffClient.find(artifactInfoOlder.artifactId, artifactInfoOlder.buildVersion, artifactInfoNewer.buildVersion) != null
    }

}
