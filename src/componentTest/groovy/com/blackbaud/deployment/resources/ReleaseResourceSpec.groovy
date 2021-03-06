package com.blackbaud.deployment.resources

import com.blackbaud.boot.exception.BadRequestException
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.api.Release
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseClient
import com.blackbaud.deployment.client.ReleaseClient
import com.blackbaud.deployment.client.ReleasePlanClient
import com.blackbaud.deployment.core.domain.ArtifactInfoService
import com.blackbaud.deployment.core.domain.ReleaseService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.RealArtifacts.earlySegmentationComponentRelease
import static com.blackbaud.deployment.RealArtifacts.recentSegmentationComponentRelease
import static com.blackbaud.deployment.RealArtifacts.recentSegmentationComponentArtifact
import static com.blackbaud.deployment.RealArtifacts.earlySegmentationComponentArtifact
import static com.blackbaud.deployment.RealArtifacts.earlyBluemoonUiArtifact
import static com.blackbaud.deployment.RealArtifacts.recentBluemoonUiArtifact
import static com.blackbaud.deployment.RealArtifacts.earlyBluemoonUiRelease
import static com.blackbaud.deployment.RealArtifacts.recentBluemoonUiRelease

@ComponentTest
class ReleaseResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseClient artifactReleaseClient

    @Autowired
    private ReleaseService releaseService

    @Autowired
    private ReleaseClient releaseClient

    @Autowired
    private ReleasePlanClient releasePlanClient

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    private String artifactId = "deployment-tracker"

    private ArtifactRelease earlyRelease = RealArtifacts.getEarlyDeploymentTrackerRelease()
    private ArtifactRelease middleRelease = RealArtifacts.getMiddleDeploymentTrackerRelease()
    private ArtifactRelease recentRelease = RealArtifacts.getRecentDeploymentTrackerRelease()
    private ArtifactInfo earlyInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private ArtifactInfo middleInfo = RealArtifacts.getMiddleDeploymentTrackerArtifact()
    private ArtifactInfo recentInfo = RealArtifacts.getRecentDeploymentTrackerArtifact()

    def "getCurrentRelease returns empty Release when there is no data"() {
        expect:
        releaseClient.getCurrentRelease().artifactReleaseDiffs == [:]
    }

    def "getCurrentRelease returns artifact with different versions in dev and prod"() {
        given:
        storeInProd(earlyRelease)

        and:
        storeInDev(recentRelease)

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .artifactId(artifactId)
                .currentRelease(recentRelease)
                .prevRelease(earlyRelease)
                .stories(["LUM-8045", "LUM-7759"] as Set)
                .developers(["Blackbaud-JohnHolland", "Ryan McKay"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentRelease returns artifact that is in prod but not dev"() {
        given:
        storeInProd(earlyRelease)

        and:
        nothingStoredInDev()

        String artifactId = earlyRelease.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .artifactId(artifactId)
                .currentRelease(null)
                .prevRelease(earlyRelease)
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
        storeInDev(earlyRelease)

        String artifactId = earlyRelease.artifactId
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .artifactId(artifactId)
                .currentRelease(earlyRelease)
                .prevRelease(null)
                .stories(["LUM-7759"] as SortedSet)
                .developers(["Ryan McKay", "Di Huynh", "Blackbaud-DiHuynh", "Mike Lueders"] as SortedSet)
                .build();

        expect:
        assert releaseClient.getCurrentRelease().artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns artifact with different versions in stored dev and provided prod"() {
        given:
        storeInDev(earlyRelease)
        storeInDev(recentRelease)

        and:
        def prodSnapshot = [earlyRelease]

        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .artifactId(artifactId)
                .currentRelease(recentRelease)
                .prevRelease(earlyRelease)
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay", "Blackbaud-JohnHolland"] as Set)
                .build();

        expect:
        assert releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot).artifactReleaseDiffs == [(artifactId): expected]
    }

    def "getCurrentReleaseForProdSnapshot returns user error when prod info is null"() {
        given:
        storeInDev(recentRelease)

        and:
        def prodSnapshot = null

        when:
        releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot)

        then:
        thrown(BadRequestException)
    }

    def "getCurrentRelease does not return non-releasable artifacts"() {
        given:
        ArtifactRelease artifactRelease = RealArtifacts.getBluemoonDojoRelease()
        storeInDev(artifactRelease)

        expect:
        releaseClient.getCurrentRelease().artifactReleaseDiffs == [:]
    }

    def "can get releaseDiffs for prod vs releasePlan artifacts"(){
        given: "a releasePlan and an artifact (middle) behind dev but later than prod"
        def currentReleasePlan = releasePlanClient.create(null);
        artifactInfoClient.update(recentInfo.artifactId, recentInfo.buildVersion, recentInfo)
        artifactInfoClient.update(middleInfo.artifactId, middleInfo.buildVersion, middleInfo)
        artifactInfoClient.update(earlyInfo.artifactId, earlyInfo.buildVersion, earlyInfo)
        storeInDev(recentRelease)

        and: "add the artifact behind dev to the releasePlan"
        releasePlanClient.addArtifact(currentReleasePlan.id, middleInfo)

        and: "a prod snapshot with the oldest artifact"
        def prodSnapShot = [earlyRelease]

        and: "an artifact release diff with no version for prev release"
        def middleReleaseNoVersion = middleRelease
        middleReleaseNoVersion.releaseVersion = null
        ArtifactReleaseDiff expected = ArtifactReleaseDiff.builder()
                .artifactId(artifactId)
                .currentRelease(middleReleaseNoVersion)
                .prevRelease(earlyRelease)
                .stories(["LUM-7759", "LUM-8045"] as Set)
                .developers(["Ryan McKay"] as Set)
                .build()

        expect: "releasePlan diffs show stories and developers for the releasePlan artifacts, not current dev"
        releaseClient.getCurrentReleasePlanDiffForProdSnapshot(prodSnapShot).artifactReleaseDiffs == [('deployment-tracker'): expected]
    }

    def "get releaseDiffs for releasePlan returns empty releaseDiffs when releasePlan does not exist"() {
        given:
        def prodSnapShot = [earlyRelease]

        expect:
        releaseClient.getCurrentReleasePlanDiffForProdSnapshot(prodSnapShot).artifactReleaseDiffs == [:]
    }

    def "current release should include segmentation component diffs"() {
        given: "segComp and bluemoonUi in dev"
        storeInDev(earlySegmentationComponentRelease)
        storeInDev(earlyBluemoonUiRelease)

        and: "deploy bluemoon-ui to production"
        storeInProd(earlyBluemoonUiRelease)
        def prodSnapshot = [earlyBluemoonUiRelease]

        and: "new version of bluemoon-ui and segComp in dev"
        storeInDev(recentSegmentationComponentRelease)
        storeInDev(recentBluemoonUiRelease)

        when:
        Release release = releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot)

        then:
        ArtifactReleaseDiff bluemoonUiDiff = release.artifactReleaseDiffs.get("bluemoon-ui")
        bluemoonUiDiff.currentRelease.dependencies == [recentSegmentationComponentArtifact]
        bluemoonUiDiff.prevRelease.dependencies == [earlySegmentationComponentArtifact]
        bluemoonUiDiff.developers == ["Blackbaud-AliRashed", "Blackbaud-AaronHensley", "Blackbaud-KyleMartinez", "Jenkins Blue Moon Dev"] as Set
        bluemoonUiDiff.stories == ["LUM-19173", "LUM-19178", "LUM-19217", "LUM-18798", "LUM-19215"] as Set
    }

    def "only show stories/developers for bluemoon-ui if the version in prod has no segCom dependency"() {
        given: "segComp and bluemoonUi in dev"
        storeInDev(earlyBluemoonUiRelease)

        and: "deploy bluemoon-ui to production"
        storeInProd(earlyBluemoonUiRelease)
        def prodSnapshot = [earlyBluemoonUiRelease]

        and: "new version of bluemoon-ui and segComp in dev"
        storeInDev(recentSegmentationComponentRelease)
        storeInDev(recentBluemoonUiRelease)

        when:
        Release release = releaseClient.getCurrentReleaseForProdSnapshot(prodSnapshot)

        then:
        ArtifactReleaseDiff bluemoonUiDiff = release.artifactReleaseDiffs.get("bluemoon-ui")
        bluemoonUiDiff.currentRelease.dependencies == [recentSegmentationComponentArtifact]
        bluemoonUiDiff.prevRelease.dependencies == []
        bluemoonUiDiff.developers == ["Blackbaud-AliRashed", "Blackbaud-AaronHensley"] as Set
        bluemoonUiDiff.stories == ["LUM-19173", "LUM-19178", "LUM-18798", "LUM-19215"] as Set
    }

    def "can get artifacts with dependencies for release plan"() {
        given: "segComp and bluemoonUi in dev"
        def currentPlan = releasePlanClient.create(null)
        storeInDev(earlySegmentationComponentRelease)
        storeInDev(earlyBluemoonUiRelease)

        and: "deploy bluemoon-ui to production"
        storeInProd(earlyBluemoonUiRelease)
        def prodSnapshot = [earlyBluemoonUiRelease]

        and: "new version of bluemoon-ui and segComp in dev"
        storeInDev(recentSegmentationComponentRelease)
        storeInDev(recentBluemoonUiRelease)

        and: "add latest version of bluemoon-ui to release plan"
        ArtifactInfo bluemoonUi = artifactInfoClient.find(recentBluemoonUiArtifact.artifactId, recentBluemoonUiArtifact.buildVersion)
        releasePlanClient.addArtifact(currentPlan.id, bluemoonUi)

        when:
        Release release = releaseClient.getCurrentReleasePlanDiffForProdSnapshot(prodSnapshot)

        then:
        ArtifactReleaseDiff bluemoonUiDiff = release.artifactReleaseDiffs.get("bluemoon-ui")
        bluemoonUiDiff.currentRelease.dependencies == [recentSegmentationComponentArtifact]
        bluemoonUiDiff.prevRelease.dependencies == [earlySegmentationComponentArtifact]
        bluemoonUiDiff.developers == ["Blackbaud-AliRashed", "Blackbaud-AaronHensley", "Blackbaud-KyleMartinez", "Jenkins Blue Moon Dev"] as Set
        bluemoonUiDiff.stories == ["LUM-19173", "LUM-19178", "LUM-19217", "LUM-18798", "LUM-19215"] as Set
    }

    def storeInDev(ArtifactRelease artifactReleaseInfo) {
        artifactReleaseClient.create(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE, artifactReleaseInfo)
    }

    def storeInProd(ArtifactRelease artifactReleaseInfo) {
        artifactReleaseClient.create(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE, artifactReleaseInfo)
    }

    def nothingStoredInDev() {}

    def nothingStoredInProd() {}
}
