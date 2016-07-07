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
import org.apache.commons.lang.StringUtils
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

    def "Query sanity check (temporary)"() {
        given:
        String artifactId = 'arbitrary'
        List<String> stories = StringUtils.split(aRandom.words(20));
        List<String> authors = StringUtils.split(aRandom.words(20));
        ArtifactInfo oldestVersion = aRandom.artifactInfo().artifactId(artifactId).buildVersion('1').build();
        ArtifactInfo middleVersion = aRandom.artifactInfo().artifactId(artifactId).buildVersion('3').build();
        ArtifactInfo newestVersion = aRandom.artifactInfo().artifactId(artifactId).buildVersion('5').build();
        ArtifactInfoEntity oldEntity = converter.toEntity(oldestVersion)
        oldEntity.storyIds = stories;
        oldEntity.authors = authors;
        ArtifactInfoEntity middleEntity = converter.toEntity(middleVersion)
        middleEntity.storyIds = stories;
        middleEntity.authors = authors;
        ArtifactInfoEntity newestEntity = converter.toEntity(newestVersion)
        newestEntity.storyIds = stories;
        newestEntity.authors = authors;

        when:
        artifactInfoRepository.save(oldEntity)
        artifactInfoRepository.save(middleEntity)
        artifactInfoRepository.save(newestEntity)

        and:
        List<ArtifactInfoEntity> artifactList = artifactInfoRepository.findByArtifactIdAndBuildVersionGreaterThanAndBuildVersionLessThanEqual(artifactId, '1', '3')
        ArtifactInfoEntity latestArtifact = artifactInfoRepository.findFirstByArtifactIdOrderByBuildVersionDesc(artifactId);

        then:
        print artifactList.size()
        artifactList == [middleEntity]
        latestArtifact == newestEntity;
    }

    def "should add new artifact info"() {
        when:
        artifactInfoClient.find(newArtifact.artifactId, newArtifact.buildVersion)

        then:
        thrown(NotFoundException)

        when:
        artifactInfoClient.update(newArtifact.artifactId, newArtifact.buildVersion, newArtifact)

        then:
        assert newArtifact == artifactInfoClient.find(newArtifact.artifactId, newArtifact.buildVersion)
    }

    def "should update existing artifact info"() {
        given:
        ArtifactInfo artifactInfoInitial = RealArtifacts.getEarlyDeploymentTrackerArtifact()

        ArtifactInfo artifactInfoUpdate = aRandom.artifactInfo()
                .artifactId(artifactInfoInitial.artifactId)
                .buildVersion(artifactInfoInitial.buildVersion)
                .gitSha("fb875ccafc4274edd2be556a391d4e074a3a350f")
                .build();

        when:
        artifactInfoClient.update(artifactInfoInitial.artifactId, artifactInfoInitial.buildVersion, artifactInfoInitial)

        and:
        artifactInfoClient.update(artifactInfoUpdate.artifactId, artifactInfoUpdate.buildVersion, artifactInfoUpdate)

        then:
        assert artifactInfoUpdate == artifactInfoClient.find(artifactInfoInitial.artifactId, artifactInfoInitial.buildVersion)
    }

    def "should update artifact with git information from previous artifact"() {
        given:
        artifactInfoRepository.save(converter.toEntity(oldArtifact))

        when:
        artifactInfoResource.put(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == ["LUM-7759", "LUM-8045"] as Set
        newArtifactInfoEntity.authors == ["Blackbaud-JohnHolland", "Ryan McKay"] as Set
    }

    def "should get every stories and authors for brand new artifacts"() {
        when:
        artifactInfoResource.put(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == ["LUM-7759", "LUM-8045"] as Set
        newArtifactInfoEntity.authors == ["Blackbaud-DiHuynh", "Blackbaud-JohnHolland", "Di Huynh", "Mike Lueders", "Ryan McKay"] as Set
    }
}
