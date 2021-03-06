package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.NotFoundException
import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.core.domain.ArtifactDependencyRepository
import com.blackbaud.deployment.core.domain.ArtifactInfoEntity
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import com.blackbaud.deployment.core.domain.git.GitLogEntity
import com.blackbaud.deployment.core.domain.git.GitLogRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

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

    @Autowired
    private GitLogRepository gitLogRepository


    @Autowired
    private ArtifactDependencyRepository artifactDependencyRepository

    private final String artifactId = "deployment-tracker"

    private final ArtifactInfo newArtifact = RealArtifacts.getRecentDeploymentTrackerArtifact()
    private final ArtifactInfo oldArtifact = RealArtifacts.getEarlyDeploymentTrackerArtifact()

    def "should add new artifact info"() {
        when:
        artifactInfoClient.find(artifactId, oldArtifact.buildVersion)

        then:
        thrown(NotFoundException)

        when:
        artifactInfoClient.update(artifactId, oldArtifact.buildVersion, oldArtifact)

        then:
        assert artifactInfoClient.find(artifactId, oldArtifact.buildVersion) == oldArtifact
    }

    def "I can add the same artifact twice with no exception and duplicate git logs are not inserted"() {
        when:
        artifactInfoClient.update(artifactId, oldArtifact.buildVersion, oldArtifact)

        then:
        List<GitLogEntity> gitLogEntities = gitLogRepository.fetchOrderedGitLogForArtifactId(artifactId)

        when:
        artifactInfoClient.update(artifactId, oldArtifact.buildVersion, oldArtifact)

        then:
        notThrown(Exception)

        and:
        gitLogEntities == gitLogRepository.fetchOrderedGitLogForArtifactId(artifactId)
    }

    def "if i add a new version of an existing artifact the git history is filled in since the most recent sha in the database"() {
        when:
        artifactInfoClient.update(artifactId, oldArtifact.buildVersion, oldArtifact)

        then:
        List<GitLogEntity> gitLogSinceOldArtifact = gitLogRepository.fetchOrderedGitLogForArtifactId(artifactId)

        when:
        artifactInfoClient.update(artifactId, newArtifact.buildVersion, newArtifact)
        List<GitLogEntity> gitLogSinceNewArtifact = gitLogRepository.fetchOrderedGitLogForArtifactId(artifactId)

        then:
        List<GitLogEntity> newEntries = gitLogSinceNewArtifact.minus(gitLogSinceOldArtifact)
        newEntries.stream().collect { s -> s.gitSha } ==
                ["5c43c3629f19ef1d4ddf061b04c7439b7f14e8a7",
                 "9ecbbedb36e9e9bd39f1781b6e4dcc0523da3e23",
                 "76cf98e70a739ced8c610999b8814b9b2556071e",
                 "5bcf379e77dbcbc6641fbe1dfbab9b3f40c82c92",
                 "a9ad8deddf21c31c9e7e2ee716a5640366cce7b0",
                 "629e1734a9e6d44e83877d44cd15ffdbd99d27c8",
                 "712e5f87572874c3c779f3ecbbf1b3439f8ffdc2",
                 "e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7"]
    }

    def "should return all infos for artifact id"() {
        given:
        artifactInfoRepository.save(converter.toEntity(oldArtifact))
        artifactInfoRepository.save(converter.toEntity(newArtifact))

        when:
        List<ArtifactInfo> artifactInfoList = artifactInfoClient.findMany(artifactId)

        then:
        artifactInfoList.size() == 2
        artifactInfoList.containsAll([oldArtifact, newArtifact])
    }

    def "remediationCreate does not overwrite existing artifacts git sha"() {
        given:
        artifactInfoClient.create(oldArtifact)

        and:
        def oldArtifactNewSha = ArtifactInfo.builder()
                .artifactId(oldArtifact.artifactId)
                .buildVersion(oldArtifact.buildVersion)
                .gitSha(newArtifact.gitSha)
                .build()

        when:
        artifactInfoClient.remediationCreate([oldArtifactNewSha])

        then:
        oldArtifact == artifactInfoClient.find(oldArtifact.getArtifactId(), oldArtifact.getBuildVersion())
    }

    def "remediationCreate does not throws exception if the same artifact already exist"() {
        given:
        artifactInfoClient.create(oldArtifact)

        when:
        artifactInfoClient.remediationCreate([oldArtifact])

        then:
        notThrown()
    }

    def "remediationCreate updates gitsha if existing gitsha was null"() {
        given:
        def nullGitSha = ArtifactInfoEntity.builder()
                .artifactId(oldArtifact.artifactId)
                .buildVersion(oldArtifact.buildVersion)
                .gitSha(null)
                .build()
        artifactInfoRepository.save(nullGitSha)

        when:
        artifactInfoClient.remediationCreate([oldArtifact])

        then:
        artifactInfoClient.find(oldArtifact.artifactId, oldArtifact.buildVersion) == oldArtifact
    }

    def "saving an artifact should save its dependency"() {
        given:
        ArtifactInfo segComp = RealArtifacts.recentSegmentationComponentArtifact
        artifactInfoClient.create(segComp)

        and:
        ArtifactInfo artifactInfo = RealArtifacts.recentBluemoonUiArtifact
        artifactInfo.dependencies = [segComp]

        when:
        ArtifactInfo createdArtifactInfo = artifactInfoClient.create(artifactInfo)

        then:
        createdArtifactInfo.dependencies[0] == segComp

        and:
        ArtifactInfo info = artifactInfoClient.find(artifactInfo.artifactId, artifactInfo.buildVersion)
        info.dependencies[0] == segComp
    }
}