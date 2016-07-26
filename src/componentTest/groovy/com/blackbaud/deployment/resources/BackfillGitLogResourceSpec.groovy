package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.BackfillGitLogClient
import com.blackbaud.deployment.core.domain.ArtifactInfoRepository
import com.blackbaud.deployment.core.domain.GitLogEntity
import com.blackbaud.deployment.core.domain.GitLogRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class BackfillGitLogResourceSpec extends Specification {

    @Autowired
    GitLogRepository gitLogRepository;

    @Autowired
    BackfillGitLogClient backfillGitLogClient;

    @Autowired
    ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    ArtifactInfoConverter converter;

    private final ArtifactInfo deploymentTrackerArtifact = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private final ArtifactInfo bluemoonCoreArtifact = RealArtifacts.getBluemoonCoreArtifact()

    def "should backfill git log up to latest sha stored for artifact"() {
        given:
        artifactInfoRepository.save(converter.toEntity(deploymentTrackerArtifact));

        when:
        List<GitLogEntity> gitLogEntities = gitLogRepository.fetchOrderedGitLogForArtifactId(deploymentTrackerArtifact.artifactId)

        then:
        gitLogEntities.isEmpty()

        when:
        backfillGitLogClient.post(deploymentTrackerArtifact.artifactId)

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
        backfillGitLogClient.post()

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
