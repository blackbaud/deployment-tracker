package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactInfoResourceSpec extends Specification {

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    @Autowired
    private ArtifactInfoConverter converter;

    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

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
