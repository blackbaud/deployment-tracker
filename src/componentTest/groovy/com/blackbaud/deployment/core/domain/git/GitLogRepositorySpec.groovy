package com.blackbaud.deployment.core.domain.git

import com.blackbaud.deployment.ArtifactInfoConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class GitLogRepositorySpec extends Specification {

    @Autowired
    GitLogRepository gitLogRepository

    @Autowired
    ArtifactInfoConverter artifactInfoConverter

    @Autowired
    ArtifactInfoClient artifactInfoClient

    private final ArtifactInfo earlyDeploymentTrackerArtifact = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private final ArtifactInfo recentDeploymentTrackerArtifact = RealArtifacts.getRecentDeploymentTrackerArtifact()
    private final ArtifactInfo bluemoonCoreArtifact = RealArtifacts.getBluemoonCoreArtifact()

    def "i can retrieve commits in the order they were commited"() {
        given:
        GitLogParserFactory factory = new GitLogParserFactory();
        GitLogParser parser = factory.createGitLogParserForNewProject("bluemoon-core", "b92937bcc183cb92f3f64abeca54a997d3de0c54")
        List<GitLogEntity> entities = parser.getGitLogEntities("bluemoon-core");

        when:
        gitLogRepository.save(entities);

        then:
        List<GitLogEntity> retrieved = gitLogRepository.fetchOrderedGitLogForArtifactId("bluemoon-core")

        retrieved == entities
    }

    def "the git log returns correct results when entries are interspersed"() {
        given:
        artifactInfoClient.update("deployment-tracker", earlyDeploymentTrackerArtifact.buildVersion, earlyDeploymentTrackerArtifact)
        artifactInfoClient.update("bluemoon-core", bluemoonCoreArtifact.buildVersion, bluemoonCoreArtifact)
        artifactInfoClient.update("deployment-tracker", recentDeploymentTrackerArtifact.buildVersion, recentDeploymentTrackerArtifact)

        when:
        List<GitLogEntity> retrieved = gitLogRepository.fetchGitLogUntilSha("deployment-tracker", recentDeploymentTrackerArtifact.gitSha)

        then:
        retrieved.size() == 22
    }
}
