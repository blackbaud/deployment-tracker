package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
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

    private final ArtifactInfo newArtifact = aRandom.artifactInfo()
            .artifactId(artifactId)
            .buildVersion("2")
            .gitSha("76cf98e70a739ced8c610999b8814b9b2556071e")
            .build()

    private ArtifactInfo oldArtifact = aRandom.artifactInfo()
            .artifactId(artifactId)
            .buildVersion("1")
            .gitSha("ab116cce4181a283a1fb32c2d300770e7dc8f08b")
            .build()

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

        then:
        assert artifactList == [middleEntity]
    }

    def "should add new artifact info"() {
        given:
        ArtifactInfo artifactInfo = ArtifactInfo.builder()
                .artifactId(artifactId)
                .buildVersion(aRandom.numberText(5))
                .gitSha("23f044ea9ee162c4f48e670fcc80e209b4c3ea92")
                .build();

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
        ArtifactInfo artifactInfoInitial = aRandom.artifactInfo()
                .artifactId(artifactId)
                .gitSha("23f044ea9ee162c4f48e670fcc80e209b4c3ea92").build();

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
        artifactInfoResource.update(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == "LUM-7759,LUM-8045"
        newArtifactInfoEntity.authors == "Di Huynh,Ryan McKay"
    }

    def "should get every stories and authors for brand new artifacts"() {
        when:
        artifactInfoResource.update(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == "LUM-7759,LUM-8045"
        newArtifactInfoEntity.authors == "Blackbaud-DiHuynh,Di Huynh,Mike Lueders,Ryan McKay"
    }
}
