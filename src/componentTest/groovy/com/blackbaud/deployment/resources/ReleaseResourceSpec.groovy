package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.api.ArtifactReleaseInfo
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
import com.blackbaud.deployment.client.ReleaseClient
import com.blackbaud.deployment.client.ReleasePlanClient
import com.blackbaud.deployment.core.domain.ReleaseService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class ReleaseResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseInfoClient artifactReleaseInfoClient

    @Autowired
    private ReleaseService releaseService

    @Autowired
    private ReleaseClient releaseClient

    @Autowired
    private ReleasePlanClient releasePlanClient

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    private String artifactId = "deployment-tracker"

    private ArtifactReleaseInfo earlyReleaseInfo = RealArtifacts.getEarlyDeploymentTrackerRelease()
    private ArtifactReleaseInfo middleReleaseInfo = RealArtifacts.getMiddleDeploymentTrackerRelease()
    private ArtifactReleaseInfo recentReleaseInfo = RealArtifacts.getRecentDeploymentTrackerRelease()
    private ArtifactInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private ArtifactInfo middleInfo = RealArtifacts.getMiddleDeploymentTrackerArtifact()
    private ArtifactInfo recentInfo = RealArtifacts.getRecentDeploymentTrackerArtifact()

    def "getCurrentRelease returns empty Release when there is no data"() {
        expect:
        releaseClient.getCurrentRelease().artifactReleaseDiffs == [:]
    }

    def "getCurrentRelease returns artifact with different versions in dev and prod"() {
        given:
        storeInProd(earlyReleaseInfo)

        and:
        storeInDev(recentReleaseInfo)

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(recentReleaseInfo)
                .prod(earlyReleaseInfo)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Blackbaud-JohnHolland", "Ryan McKay"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in prod but not dev"() {
        given:
        storeInProd(earlyReleaseInfo)

        and:
        nothingStoredInDev()

        String artifactId = earlyReleaseInfo.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(null)
                .prod(earlyReleaseInfo)
                .stories([] as Set)
                .developers([] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in dev but not prod"() {
        given:
        nothingStoredInProd()

        and:
        storeInDev(earlyReleaseInfo)

        String artifactId = earlyReleaseInfo.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(earlyReleaseInfo)
                .prod(null)
                .stories(["LUM-7759"] as SortedSet)
                .developers(["Ryan McKay", "Di Huynh", "Blackbaud-DiHuynh", "Mike Lueders"] as SortedSet)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact with different versions in stored dev and provided prod"() {
        given:
        storeInDev(earlyReleaseInfo)
        storeInDev(recentReleaseInfo)

        and:
        def prodSnapshot = [earlyReleaseInfo]

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(recentReleaseInfo)
                .prod(earlyReleaseInfo)
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay", "Blackbaud-JohnHolland"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns user error when prod info is null"() {
        given:
        storeInDev(recentReleaseInfo)

        and:
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

    def "can get releaseDiffs for prod vs releasePlan artifacts"(){
        given: "a releasePlan with an early artifact"
        def currentReleasePlan = releasePlanClient.create(null);
        artifactInfoClient.update(recentInfo.artifactId, recentInfo.buildVersion, recentInfo)
        artifactInfoClient.update(middleInfo.artifactId, middleInfo.buildVersion, middleInfo)
        artifactInfoClient.update(earlyInfo.artifactId, earlyInfo.buildVersion, earlyInfo)
        artifactReleaseInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, recentReleaseInfo)
        releasePlanClient.addArtifact(currentReleasePlan.id, middleInfo)

        and:
        def prodSnapShot = [earlyReleaseInfo]

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .dev(middleReleaseInfo)
                .prod(earlyReleaseInfo)
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay", "Blackbaud-JohnHolland"] as Set)
                .build()

        expect:
        releaseClient.getCurrentReleasePlanDiffForProdSnapshot(prodSnapShot).artifactReleaseDiffs == [('deployment-tracker'): expected]
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
