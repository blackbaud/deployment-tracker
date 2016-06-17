package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.deployment.ComponentTest
<<<<<<< HEAD
<<<<<<< HEAD
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.api.ArtifactReleaseInfo
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
<<<<<<< HEAD
=======
import com.blackbaud.deployment.api.DeploymentDiff
=======
import com.blackbaud.deployment.api.ArtifactReleaseDiff
>>>>>>> f678692... more renaming
import com.blackbaud.deployment.api.ArtifactReleaseInfo
import com.blackbaud.deployment.client.DeploymentInfoClient
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
>>>>>>> e82996a... so much renaming
import com.blackbaud.deployment.client.ReleaseClient
import com.blackbaud.deployment.core.domain.ReleaseService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import javax.ws.rs.WebApplicationException

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ReleaseResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseInfoClient artifactReleaseInfoClient

    @Autowired
    private ReleaseService releaseService

    @Autowired
    private ReleaseClient releaseClient

    private String artifactId = "deployment-tracker"

<<<<<<< HEAD
<<<<<<< HEAD
    private ArtifactReleaseInfo earlyInfo = aRandom.artifactReleaseInfo()
=======
    private ArtifactReleaseInfo earlyDeploymentInfo = aRandom.deploymentInfo()
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
    private ArtifactReleaseInfo earlyInfo = aRandom.artifactReleaseInfo()
>>>>>>> e82996a... so much renaming
            .artifactId(artifactId)
            .buildVersion("0.20160525.221050")
            .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
            .build()

<<<<<<< HEAD
<<<<<<< HEAD
    private ArtifactReleaseInfo recentInfo = aRandom.artifactReleaseInfo()
=======
    private ArtifactReleaseInfo recentDeploymentInfo = aRandom.deploymentInfo()
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
    private ArtifactReleaseInfo recentInfo = aRandom.artifactReleaseInfo()
>>>>>>> e82996a... so much renaming
            .artifactId(artifactId)
            .buildVersion("0.20160606.194525")
            .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
            .build()

    def "getCurrentRelease returns empty Release when there is no data"() {
        expect:
        releaseClient.getCurrentRelease().releaseDiff == [:]
    }

    def "getCurrentRelease returns artifact with different versions in dev and prod"() {
        given:
        storeInDev(recentInfo)

        and:
        storeInProd(earlyInfo)

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
<<<<<<< HEAD
<<<<<<< HEAD
                .dev(recentInfo)
                .prod(earlyInfo)
=======
                .dev(recentDeploymentInfo)
                .prod(earlyDeploymentInfo)
>>>>>>> f678692... more renaming
=======
                .dev(recentInfo)
                .prod(earlyInfo)
>>>>>>> e82996a... so much renaming
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Blackbaud-JohnHolland", "Ryan McKay"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().releaseDiff == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in prod but not dev"() {
        given:
        nothingStoredInDev()

        and:
<<<<<<< HEAD
<<<<<<< HEAD
        ArtifactReleaseInfo prodInfo = aRandom.artifactReleaseInfo().build()
        storeInProd(prodInfo)
=======
        ArtifactReleaseInfo prodDeploymentInfo = aRandom.deploymentInfo().build()
        storeInProd(prodDeploymentInfo)
>>>>>>> 746a757... LUM-9138 first pass at renaming

<<<<<<< HEAD
        String artifactId = prodInfo.artifactId
=======
        String artifactId = prodDeploymentInfo.artifactId
>>>>>>> f678692... more renaming
=======
        ArtifactReleaseInfo prodInfo = aRandom.artifactReleaseInfo().build()
        storeInProd(prodInfo)

        String artifactId = prodInfo.artifactId
>>>>>>> e82996a... so much renaming
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(null)
                .prod(prodInfo)
                .stories([] as Set)
                .developers([] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().releaseDiff == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in dev but not prod"() {
        given:
        storeInDev(earlyInfo)

        and:
        nothingStoredInProd()

<<<<<<< HEAD
<<<<<<< HEAD
        String artifactId = earlyInfo.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(earlyInfo)
=======
        String artifactId = earlyDeploymentInfo.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(earlyDeploymentInfo)
>>>>>>> f678692... more renaming
=======
        String artifactId = earlyInfo.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(earlyInfo)
>>>>>>> e82996a... so much renaming
                .prod(null)
                .stories(["LUM-7759"] as Set)
                .developers(["Ryan McKay", "Mike Lueders", "Blackbaud-DiHuynh", "Di Huynh"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().releaseDiff == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact with different versions in stored dev and provided prod"() {
        given: "A version stored in dev"
        storeInDev(recentInfo)

        and: "A different version in prod snapshot"
        def prodSnapshot = [earlyInfo]

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
<<<<<<< HEAD
<<<<<<< HEAD
                .dev(recentInfo)
                .prod(earlyInfo)
=======
                .dev(recentDeploymentInfo)
                .prod(earlyDeploymentInfo)
>>>>>>> f678692... more renaming
=======
                .dev(recentInfo)
                .prod(earlyInfo)
>>>>>>> e82996a... so much renaming
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay", "Blackbaud-JohnHolland"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).releaseDiff == [(artifactId): expected]
    }

    def "getCurrentRelease does not return non-releasable artifacts"() {
        given:
<<<<<<< HEAD
<<<<<<< HEAD
        ArtifactReleaseInfo artifactReleaseInfo = aRandom.artifactReleaseInfo().artifactId("bluemoon-dojo").build();
        storeInDev(artifactReleaseInfo)
=======
        ArtifactReleaseInfo deploymentInfo = aRandom.deploymentInfo().artifactId("bluemoon-dojo").build();
        storeInDev(deploymentInfo)
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
        ArtifactReleaseInfo artifactReleaseInfo = aRandom.artifactReleaseInfo().artifactId("bluemoon-dojo").build();
        storeInDev(artifactReleaseInfo)
>>>>>>> e82996a... so much renaming

        expect:
        releaseClient.getCurrentRelease().releaseDiff == [:]
    }
    
    def "Missing commits throws exception"() {
        given:
<<<<<<< HEAD
<<<<<<< HEAD
        ArtifactReleaseInfo dev = aRandom.artifactReleaseInfo()
=======
        ArtifactReleaseInfo dev = aRandom.deploymentInfo()
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
        ArtifactReleaseInfo dev = aRandom.artifactReleaseInfo()
>>>>>>> e82996a... so much renaming
                .artifactId(artifactId)
                .buildVersion("0.20160606.194525")
                .gitSha("3e176dced48f7b888707337261ba5b97902cf5b8")
                .build()
        storeInDev(dev)

        when:
        releaseClient.getCurrentReleaseForProdSnapshot([earlyInfo])

        then:
        WebApplicationException ex = thrown()
    }

    def "Invalid github repo throws exception"() {
        given:
<<<<<<< HEAD
<<<<<<< HEAD
        ArtifactReleaseInfo artifactReleaseInfo = aRandom.artifactReleaseInfo().build();
        storeInDev(artifactReleaseInfo)
=======
        ArtifactReleaseInfo deploymentInfo = aRandom.deploymentInfo().build();
        storeInDev(deploymentInfo)
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
        ArtifactReleaseInfo artifactReleaseInfo = aRandom.artifactReleaseInfo().build();
        storeInDev(artifactReleaseInfo)
>>>>>>> e82996a... so much renaming

        when:
        releaseClient.getCurrentRelease()

        then:
        BadRequestException ex = thrown()
        ex.errorEntity.message == "Cannot find repository with name " + artifactReleaseInfo.artifactId
<<<<<<< HEAD
    }

<<<<<<< HEAD
    def storeInDev(ArtifactReleaseInfo artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, artifactReleaseInfo)
    }

    def storeInProd(ArtifactReleaseInfo artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, artifactReleaseInfo)
=======
    def storeInDev(ArtifactReleaseInfo deploymentInfo) {
        deploymentInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, deploymentInfo)
    }

    def storeInProd(ArtifactReleaseInfo deploymentInfo) {
        deploymentInfoClient.update(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, deploymentInfo)
>>>>>>> 746a757... LUM-9138 first pass at renaming
=======
    }

    def storeInDev(ArtifactReleaseInfo artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, artifactReleaseInfo)
    }

    def storeInProd(ArtifactReleaseInfo artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, artifactReleaseInfo)
>>>>>>> e82996a... so much renaming
    }

    def nothingStoredInDev() {}

    def nothingStoredInProd() {}
}
