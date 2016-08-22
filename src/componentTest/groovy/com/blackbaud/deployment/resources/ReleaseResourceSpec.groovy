package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.api.ArtifactRelease
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

    private ArtifactRelease earlyReleaseInfo = RealArtifacts.getEarlyDeploymentTrackerRelease()
    private ArtifactRelease middleReleaseInfo = RealArtifacts.getMiddleDeploymentTrackerRelease()
    private ArtifactRelease recentReleaseInfo = RealArtifacts.getRecentDeploymentTrackerRelease()
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
                .currentRelease(recentReleaseInfo)
                .prevRelease(earlyReleaseInfo)
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
                .currentRelease(null)
                .prevRelease(earlyReleaseInfo)
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
                .currentRelease(earlyReleaseInfo)
                .prevRelease(null)
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
                .currentRelease(recentReleaseInfo)
                .prevRelease(earlyReleaseInfo)
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
        ArtifactRelease artifactReleaseInfo = RealArtifacts.getBluemoonDojoRelease()
        storeInDev(artifactReleaseInfo)

        expect:
        releaseClient.getCurrentRelease().artifactReleaseDiffs == [:]
    }

    def "can get releaseDiffs for prod vs releasePlan artifacts"(){
        given: "a releasePlan and an artifact (middle) behind dev but later than prod"
        def currentReleasePlan = releasePlanClient.create(null);
        artifactInfoClient.update(recentInfo.artifactId, recentInfo.buildVersion, recentInfo)
        artifactInfoClient.update(middleInfo.artifactId, middleInfo.buildVersion, middleInfo)
        artifactInfoClient.update(earlyInfo.artifactId, earlyInfo.buildVersion, earlyInfo)
        storeInDev(recentReleaseInfo)

        and: "add the artifact behind dev to the releasePlan"
        releasePlanClient.addArtifact(currentReleasePlan.id, middleInfo)

        and: "a prevRelease snapshot with the oldest artifact"
        def prodSnapShot = [earlyReleaseInfo]

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .currentRelease(middleReleaseInfo)
                .prevRelease(earlyReleaseInfo)
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay"] as Set)
                .build()

        expect: "releasePlan diffs show stories and developers for the releasePlan artifacts, not current dev"
        releaseClient.getCurrentReleasePlanDiffForProdSnapshot(prodSnapShot).artifactReleaseDiffs == [('deployment-tracker'): expected]
    }

    def "get releaseDiffs for releasePlan returns empty releaseDiffs when releasePlan does not exist"() {
        given:
        def prodSnapShot = [earlyReleaseInfo]

        expect:
        releaseClient.getCurrentReleasePlanDiffForProdSnapshot(prodSnapShot).artifactReleaseDiffs == [:]
    }

    def storeInDev(ArtifactRelease artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, artifactReleaseInfo)
    }

    def storeInProd(ArtifactRelease artifactReleaseInfo) {
        artifactReleaseInfoClient.update(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, artifactReleaseInfo)
    }

    def nothingStoredInDev() {}

    def nothingStoredInProd() {}
}
