package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.ComponentTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class GitLogRepositorySpec extends Specification {

    @Autowired
    GitLogRepository gitLogRepository

    def "i can retrieve commits in the order they were commited"() {
        given:
        GitLogParserFactory factory = new GitLogParserFactory();
        GitLogParser parser = factory.createGitLogParserForNewProject("bluemoon-core","b92937bcc183cb92f3f64abeca54a997d3de0c54")
        List<GitLogEntity> entities = parser.getGitLogEntities("bluemoon-core");

        when:
        gitLogRepository.save(entities);

        then:
        List<GitLogEntity> retrieved = gitLogRepository.fetchOrderedGitLogForArtifactId("bluemoon-core")

        retrieved == entities
    }

}
