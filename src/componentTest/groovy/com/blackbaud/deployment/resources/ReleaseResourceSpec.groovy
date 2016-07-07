package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.api.ArtifactReleaseInfo
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
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

    private ArtifactReleaseInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerRelease()

    private ArtifactReleaseInfo recentInfo = RealArtifacts.getRecentDeploymentTrackerRelease()

    def "getCurrentRelease returns empty Release when there is no data"() {
        expect:
        releaseClient.getCurrentRelease().artifactReleaseDiffs == [:]
    }

    def "getCurrentRelease returns artifact with different versions in dev and prod"() {
        given:
        storeInDev(recentInfo)

        and:
        storeInProd(earlyInfo)

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(recentInfo)
                .prod(earlyInfo)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Blackbaud-JohnHolland", "Ryan McKay"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in prod but not dev"() {
        given:
        nothingStoredInDev()

        and:
        storeInProd(earlyInfo)

        String artifactId = earlyInfo.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(null)
                .prod(earlyInfo)
                .stories([] as Set)
                .developers([] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in dev but not prod"() {
        given:
        storeInDev(earlyInfo)

        and:
        nothingStoredInProd()

        String artifactId = earlyInfo.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(earlyInfo)
                .prod(null)
                .stories(["LUM-7759"] as Set)
                .developers(["Ryan McKay", "Mike Lueders", "Blackbaud-DiHuynh", "Di Huynh"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact with different versions in stored dev and provided prod"() {
        given: "A version stored in dev"
        storeInDev(recentInfo)

        and: "A different version in prod snapshot"
        def prodSnapshot = [earlyInfo]

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(recentInfo)
                .prod(earlyInfo)
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay", "Blackbaud-JohnHolland"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns user error when prod info is null"() {
        given: "A version stored in dev"
        storeInDev(recentInfo)

        and: "A different version in prod snapshot"
        def prodSnapshot = null

        when:
        releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot)

        then:
        thrown(BadRequestException)
    }

    def "getCurrentRelease does not return non-releasable artifacts"() {
        given:
        ArtifactReleaseInfo artifactReleaseInfo = RealArtifacts.getBluemoonDojoRelease()
        storeInDev(artifactReleaseInfo)

        expect:
        releaseClient.getCurrentRelease().artifactReleaseDiffs == [:]
    }
    
    def "Missing commits throws exception"() {
        given:
        ArtifactReleaseInfo dev = aRandom.artifactReleaseInfo()
                .artifactId(artifactId)
                .buildVersion("0.20160606.194525")
                .gitSha("3e176dced48f7b888707337261ba5b97902cf5b8")
                .build()

        when:
        storeInDev(dev)

        then:
        WebApplicationException ex = thrown()
    }

    def storeInDev(ArtifactReleaseInfo artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, artifactReleaseInfo)
    }

    def storeInProd(ArtifactReleaseInfo artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, artifactReleaseInfo)
    }

    def nothingStoredInDev() {}

    def nothingStoredInProd() {}
}
