package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.api.DeploymentDiff
import com.blackbaud.deployment.api.DeploymentInfo
import com.blackbaud.deployment.client.DeploymentInfoClient
import com.blackbaud.deployment.client.ReleaseClient
import com.blackbaud.deployment.core.domain.ReleaseService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import javax.ws.rs.WebApplicationException

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ReleaseResourceSpec extends Specification {

    @Autowired
    private DeploymentInfoClient deploymentInfoClient

    @Autowired
    private ReleaseService releaseService

    @Autowired
    private ReleaseClient releaseClient

    private String artifactId = "deployment-tracker"

    private DeploymentInfo earlyDeploymentInfo = aRandom.deploymentInfo()
            .artifactId(artifactId)
            .buildVersion("0.20160525.221050")
            .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
            .build()

    private DeploymentInfo recentDeploymentInfo = aRandom.deploymentInfo()
            .artifactId(artifactId)
            .buildVersion("0.20160606.194525")
            .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
            .build()

    def "getCurrentRelease returns empty Release when there is no data"() {
        expect:
        releaseClient.getCurrentRelease().deploymentDiffs == [:]
    }

    def "getCurrentRelease returns artifact with different versions in dev and prod"() {
        given:
        storeInDev(recentDeploymentInfo)

        and:
        storeInProd(earlyDeploymentInfo)

        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(recentDeploymentInfo)
                .prod(earlyDeploymentInfo)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Blackbaud-JohnHolland", "Ryan McKay"] as Set)
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
                .stories([] as Set)
                .developers([] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in dev but not prod"() {
        given:
        storeInDev(earlyDeploymentInfo)

        and:
        nothingStoredInProd()

        String artifactId = earlyDeploymentInfo.artifactId
        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(earlyDeploymentInfo)
                .prod(null)
                .stories(["LUM-7759"] as Set)
                .developers(["Ryan McKay", "Mike Lueders", "Blackbaud-DiHuynh", "Di Huynh"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact with different versions in stored dev and provided prod"() {
        given: "A version stored in dev"
        storeInDev(recentDeploymentInfo)

        and: "A different version in prod snapshot"
        def prodSnapshot = [earlyDeploymentInfo]

        DeploymentDiff expected = DeploymentDiff.builder()
                .dev(recentDeploymentInfo)
                .prod(earlyDeploymentInfo)
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay", "Blackbaud-JohnHolland"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).deploymentDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease does not return non-releasable artifacts"() {
        given:
        DeploymentInfo deploymentInfo = aRandom.deploymentInfo().artifactId("bluemoon-dojo").build();
        storeInDev(deploymentInfo)

        expect:
        releaseClient.getCurrentRelease().deploymentDiffs == [:]
    }

    def "Missing commits throws exception"() {
        given:
        DeploymentInfo dev = aRandom.deploymentInfo()
                .artifactId(artifactId)
                .buildVersion("0.20160606.194525")
                .gitSha("3e176dced48f7b888707337261ba5b97902cf5b8")
                .build()
        storeInDev(dev)

        when:
        releaseClient.getCurrentReleaseForProdSnapshot([earlyDeploymentInfo])

        then:
        WebApplicationException ex = thrown()
    }

    def "Invalid github repo throws exception"() {
        given:
        DeploymentInfo deploymentInfo = aRandom.deploymentInfo().build();
        storeInDev(deploymentInfo)

        when:
        releaseClient.getCurrentRelease()

        then:
        BadRequestException ex = thrown()
        ex.errorEntity.message == "Cannot find repository with name " + deploymentInfo.artifactId
    }

    def storeInDev(DeploymentInfo deploymentInfo) {
        deploymentInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, deploymentInfo)
    }

    def storeInProd(DeploymentInfo deploymentInfo) {
        deploymentInfoClient.update(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, deploymentInfo)
    }

    def nothingStoredInDev() {}

    def nothingStoredInProd() {}
}
