package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity
import com.blackbaud.deployment.core.domain.ArtifactInfoPrimaryKey
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.util.StringUtil
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

     def "Query sanity check (temporary)"() {
        given:
        String artifactId = 'arbitrary'
        ArtifactInfo oldestVersion = aRandom.artifactInfo().artifactId(artifactId).buildVersion('1').build();
        ArtifactInfo middleVersion = aRandom.artifactInfo().artifactId(artifactId).buildVersion('3').build();
        ArtifactInfo newestVersion = aRandom.artifactInfo().artifactId(artifactId).buildVersion('5').build();
        ArtifactInfoEntity oldEntity = converter.toEntity(oldestVersion)
        ArtifactInfoEntity middleEntity = converter.toEntity(middleVersion)
        ArtifactInfoEntity newestEntity = converter.toEntity(newestVersion)

        when:
        artifactInfoRepository.save(oldEntity)
        artifactInfoRepository.save(middleEntity)
        artifactInfoRepository.save(newestEntity)

        and:
        List<ArtifactInfoEntity> artifactList = artifactInfoRepository.findByArtifactIdAndBuildVersionGreaterThanAndBuildVersionLessThanEqual(artifactId, '1', '3')

        then:
        print artifactList.size()
        artifactList == [middleEntity]
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

    def "should update artifact with git information from previous artifact"() {
        given:
        ArtifactInfo old = aRandom.artifactInfo()
                .artifactId(artifactId)
                .buildVersion("1")
                .gitSha("ab116cce4181a283a1fb32c2d300770e7dc8f08b")
                .build()
        artifactInfoRepository.save(converter.toEntity(old))

        and:
        ArtifactInfo newArtifact = aRandom.artifactInfo().artifactId(artifactId)
            .buildVersion("2")
            .gitSha("76cf98e70a739ced8c610999b8814b9b2556071e")
            .build()

        when:
        artifactInfoResource.update(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == "LUM-7759,LUM-8045"
        newArtifactInfoEntity.authors == "Di Huynh,Ryan McKay"
    }

    def "should get every stories and authors for brand new artifacts"(){
        given:
        ArtifactInfo newArtifact = aRandom.artifactInfo().artifactId(artifactId)
                .buildVersion("2")
                .gitSha("ab116cce4181a283a1fb32c2d300770e7dc8f08b")
                .build()
        when:
        artifactInfoResource.update(artifactId, newArtifact.buildVersion, newArtifact)

        then:
        ArtifactInfoEntity newArtifactInfoEntity = artifactInfoRepository.findOne(new ArtifactInfoPrimaryKey(artifactId, newArtifact.buildVersion))
        newArtifactInfoEntity.storyIds == "LUM-7759"
        newArtifactInfoEntity.authors == "Blackbaud-DiHuynh,Mike Lueders,Ryan McKay"
    }
}
