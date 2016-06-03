package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.DeploymentDiff
import com.blackbaud.deployment.api.DeploymentInfo
import com.blackbaud.deployment.client.DeploymentInfoClient
import com.blackbaud.deployment.client.ReleaseClient
import com.blackbaud.deployment.core.domain.GithubRepositoryService
import com.blackbaud.deployment.core.domain.ReleaseService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ReleaseResourceSpec extends Specification {

    @Autowired
    private DeploymentInfoClient deploymentInfoClient

    @Autowired
    private ReleaseService releaseService

    @Autowired
    private ReleaseClient releaseClient

    private GithubRepositoryService mockGithubRepoService = Mock()

    def setup() {
        releaseService.repositoryService = mockGithubRepoService
    }

    def "getCurrentRelease returns empty Release when there is no data"() {
        expect:
        releaseClient.getCurrentRelease().deploymentDiffs == [:]
    }

    def "getCurrentRelease returns artifact with different versions in dev and prod"() {
        given: "A version in dev"
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        String artifactId = devDeploymentInfo.artifactId
        storeInDev(devDeploymentInfo)

        and: "A different version in prod"
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().artifactId(artifactId).build()
        storeInProd(prodDeploymentInfo)

        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(devDeploymentInfo)
                .prod(prodDeploymentInfo)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in prod but not dev"() {
        given:
        nothingStoredInDev()

        and:
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().build()
        storeInProd(prodDeploymentInfo)

        String artifactId = prodDeploymentInfo.artifactId
        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(null)
                .prod(prodDeploymentInfo)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in dev but not prod"() {
        given:
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        storeInDev(devDeploymentInfo)

        and:
        nothingStoredInProd()

        String artifactId = devDeploymentInfo.artifactId
        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(devDeploymentInfo)
                .prod(null)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns empty Release when there is no data provided and no data stored"() {
        expect:
        releaseClient.getCurrentReleaseForProdSnapshot([]).deploymentDiffs == [:]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact with different versions in stored dev and provided prod"() {
        given: "A version stored in dev"
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        String artifactId = devDeploymentInfo.artifactId
        storeInDev(devDeploymentInfo)

        and: "A different version in prod snapshot"
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().artifactId(artifactId).build()
        def prodSnapshot = [prodDeploymentInfo]

        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(devDeploymentInfo)
                .prod(prodDeploymentInfo)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact that is in provided prod but not stored dev"() {
        given:
        nothingStoredInDev()

        and:
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().build()
        def prodSnapshot = [prodDeploymentInfo]

        String artifactId = prodDeploymentInfo.artifactId
        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(null)
                .prod(prodDeploymentInfo)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact that is in stored dev but not provided prod"() {
        given:
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        String artifactId = devDeploymentInfo.artifactId
        storeInDev(devDeploymentInfo)

        and:
        def prodSnapshot = []

        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(devDeploymentInfo)
                .prod(null)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).deploymentDiffs == [(artifactId): expected]
    }

    def storeInDev(DeploymentInfo deploymentInfo) {
        deploymentInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, deploymentInfo)
    }

    def storeInProd(DeploymentInfo deploymentInfo) {
        deploymentInfoClient.update(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, deploymentInfo)
    }

    // TODO: a real git test

    def nothingStoredInDev() {}

    def nothingStoredInProd() {}
}
