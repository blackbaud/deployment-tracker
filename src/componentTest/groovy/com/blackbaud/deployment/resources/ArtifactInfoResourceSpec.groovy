package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity
import com.blackbaud.deployment.core.domain.ArtifactInfoPrimaryKey
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import org.hibernate.annotations.Sort
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactInfoResourceSpec extends Specification {

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    @Autowired
    private ArtifactInfoConverter converter

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository

    @Autowired
    private ArtifactInfoResource artifactInfoResource

    private final String artifactId = "deployment-tracker"

    private final ArtifactInfo newArtifact = RealArtifacts.getRecentDeploymentTrackerArtifact()
    private final ArtifactInfo oldArtifact = RealArtifacts.getEarlyDeploymentTrackerArtifact()

    def "should add new artifact info"() {
        when:
        artifactInfoClient.find(newArtifact.artifactId, newArtifact.buildVersion)

        then:
        thrown(NotFoundException)

        when:
        artifactInfoClient.update(newArtifact.artifactId, newArtifact.buildVersion, newArtifact)

        then:
        assert artifactInfoClient.find(newArtifact.artifactId, newArtifact.buildVersion) == newArtifact
    }

    def "should update artifact with git information since previous artifact"() {
        given:
        artifactInfoRepository.save(converter.toEntity(oldArtifact))

        when:
        artifactInfoClient.update(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == ["LUM-7759", "LUM-8045"] as Set
        newArtifactInfoEntity.authors == ["Blackbaud-JohnHolland", "Ryan McKay"] as Set
    }

    def "should get every stories and authors for brand new artifacts"() {
        when:
        artifactInfoClient.update(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == ["LUM-7759", "LUM-8045"] as Set
        newArtifactInfoEntity.authors == ["Blackbaud-DiHuynh", "Blackbaud-JohnHolland", "Di Huynh", "Mike Lueders", "Ryan McKay"] as Set
    }

    def "should update git info for any version"(){
        given:
        ArtifactInfo oldArtifact = ArtifactInfo.builder().artifactId("bluemoon-admin-ui")
                .buildVersion("1")
                .gitSha("5a615a7933a40e06b4f0e6a54ef496414d932eb7")
                .build()
        artifactInfoRepository.save(converter.toEntity(oldArtifact))

        and: "latest artifact with no git info"
        ArtifactInfo latestArtifact = ArtifactInfo.builder().artifactId("bluemoon-admin-ui")
                .buildVersion("2")
                .gitSha("453c5834d4d9f791d8600e9fc4698e9f5805c34f")
                .build()
        artifactInfoRepository.save(converter.toEntity(latestArtifact))

        when: "update the latest artifact again through the api"
        ArtifactInfo updatedInfo = artifactInfoClient.update("bluemoon-admin-ui", latestArtifact.buildVersion, latestArtifact)

        then:
        assert updatedInfo.authors == ["Di Huynh"] as SortedSet
        assert updatedInfo.storyIds == ["LUM-8853"] as SortedSet
    }
}
