package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.GitLogConverter
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.GitLogInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.GitLogInfoClient
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import com.blackbaud.deployment.core.domain.git.GitLogEntity
import com.blackbaud.deployment.core.domain.git.GitLogRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
@ComponentTest
class GitLogInfoResourceSpec extends Specification {

    @Autowired
    GitLogRepository gitLogRepository;

    @Autowired
    GitLogInfoClient gitLogInfoClient;

    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    ArtifactInfoClient artifactInfoClient;

    @Autowired
    ArtifactInfoConverter converter;

    @Autowired
    GitLogConverter gitLogConverter;

    private final ArtifactInfo deploymentTrackerArtifact = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private final ArtifactInfo bluemoonCoreArtifact = RealArtifacts.getRecentBluemoonCoreArtifact()

    def "should return git logs for an artifact"() {
        given:
        artifactInfoClient.update(deploymentTrackerArtifact.artifactId, deploymentTrackerArtifact.buildVersion, deploymentTrackerArtifact)

        when:
        List<GitLogInfo> infos = gitLogInfoClient.find(deploymentTrackerArtifact.artifactId)

        then:
        infos == gitLogConverter.toApiList(gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId))
    }

    def "should backfill git log up to latest sha stored for artifact"() {
        given:
        artifactInfoRepository.save(converter.toEntity(deploymentTrackerArtifact));

        when:
        List<GitLogEntity> gitLogEntities = gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId)

        then:
        gitLogEntities.isEmpty()

        when:
        gitLogInfoClient.post(deploymentTrackerArtifact.artifactId)

        and:
        gitLogEntities = gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId)

        then:
        gitLogEntities.size() == 14
        gitLogEntities.last().gitSha == deploymentTrackerArtifact.gitSha
    }

    def "should backfill git log up to latest sha for every artifact stored"() {
        given:
        artifactInfoRepository.save(converter.toEntity(deploymentTrackerArtifact));
        artifactInfoRepository.save(converter.toEntity(bluemoonCoreArtifact));

        when:
        List<GitLogEntity> deploymentTrackerGitLog = gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId)
        List<GitLogEntity> bluemoonCoreGitLog = gitLogRepository.fetchOrderedGitLogForArtifactId(bluemoonCoreArtifact.artifactId)

        then:
        deploymentTrackerGitLog.isEmpty()
        bluemoonCoreGitLog.isEmpty()

        when:
        gitLogInfoClient.post()

        and:
        deploymentTrackerGitLog = gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId)
        bluemoonCoreGitLog = gitLogRepository.fetchOrderedGitLogForArtifactId(bluemoonCoreArtifact.artifactId)

        then:
        deploymentTrackerGitLog.size() == 14
        deploymentTrackerGitLog.last().gitSha == deploymentTrackerArtifact.gitSha

        bluemoonCoreGitLog.size() == 363
        bluemoonCoreGitLog.last().gitSha == bluemoonCoreArtifact.gitSha
    }

}
