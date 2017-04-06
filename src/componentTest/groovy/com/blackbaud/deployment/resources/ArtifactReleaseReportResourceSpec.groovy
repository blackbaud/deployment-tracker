package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ArtifactReleaseDiffConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseClient
import com.blackbaud.deployment.client.ArtifactReleaseReportClient
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogEntity
import com.blackbaud.deployment.core.domain.ArtifactReleaseLogRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.blackbaud.deployment.core.CoreARandom.aRandom

@ComponentTest
class ArtifactReleaseReportResourceSpec extends Specification {

    @Autowired
    private ArtifactReleaseLogRepository artifactReleaseLogRepository

    @Autowired
    private ArtifactReleaseReportClient artifactReleaseReportClient;

    @Autowired
    private ArtifactInfoClient artifactInfoClient

    @Autowired
    ArtifactReleaseClient artifactReleaseClient

    @Autowired
    private ArtifactReleaseDiffConverter releaseDiffConverter

    private static String trackerArtifactId = 'deployment-tracker'
    private static String coreArtifactId = 'bluemoon-core'
    private static String foundation = 'foundation'
    private static String space = 'space'

    private ArtifactInfo earlyTrackerInfo = RealArtifacts.getEarlyDeploymentTrackerArtifact()
    private ArtifactInfo middleTrackerInfo = RealArtifacts.getMiddleDeploymentTrackerArtifact()
    private ArtifactInfo recentTrackerInfo = RealArtifacts.getRecentDeploymentTrackerArtifact()
    private ArtifactInfo recentBluemoonCoreInfo = RealArtifacts.getRecentBluemoonCoreArtifact()
    private ArtifactInfo olderBluemoonCoreInfo = RealArtifacts.getEarlyBluemoonCoreArtifact()

    private ArtifactRelease earlyTrackerRelease = RealArtifacts.earlyDeploymentTrackerRelease
    private ArtifactRelease middleTrackerRelease = RealArtifacts.middleDeploymentTrackerRelease
    private ArtifactRelease recentTrackerRelease = RealArtifacts.recentDeploymentTrackerRelease
    private ArtifactRelease emptyTrackerRelease = ArtifactRelease.builder().artifactId(trackerArtifactId).build()

    def "Should be able to get a list of all artifact diffs in a space"() {
        given:
        ArtifactReleaseLogEntity firstLogEntity = aRandom.releaseLogEntity().foundation(foundation).artifactId(trackerArtifactId).buildVersion(middleTrackerInfo.buildVersion).prevBuildVersion(earlyTrackerInfo.buildVersion).build()
        ArtifactReleaseLogEntity secondLogEntity = aRandom.releaseLogEntity().foundation(foundation).artifactId(trackerArtifactId).buildVersion(recentTrackerInfo.buildVersion).prevBuildVersion(middleTrackerInfo.buildVersion).build()
        ArtifactReleaseLogEntity thirdLogEntity = aRandom.releaseLogEntity().foundation(foundation).artifactId(coreArtifactId).buildVersion(recentBluemoonCoreInfo.buildVersion).prevBuildVersion(olderBluemoonCoreInfo.buildVersion).build()
        artifactInfoClient.update(trackerArtifactId, earlyTrackerInfo.buildVersion, earlyTrackerInfo)
        artifactInfoClient.update(trackerArtifactId, middleTrackerInfo.buildVersion, middleTrackerInfo)
        artifactInfoClient.update(trackerArtifactId, recentTrackerInfo.buildVersion, recentTrackerInfo)
        artifactInfoClient.update(coreArtifactId, recentBluemoonCoreInfo.buildVersion, recentBluemoonCoreInfo)
        artifactInfoClient.update(coreArtifactId, olderBluemoonCoreInfo.buildVersion, olderBluemoonCoreInfo)

        when:
        artifactReleaseLogRepository.save(firstLogEntity)
        artifactReleaseLogRepository.save(secondLogEntity)
        artifactReleaseLogRepository.save(thirdLogEntity)

        and:
        ArtifactRelease firstCurrentRelease = createCurrentArtifactRelease(firstLogEntity, middleTrackerInfo.gitSha)
        ArtifactRelease firstPreviousRelease = createPreviousArtifactRelease(firstLogEntity, earlyTrackerInfo.gitSha)
        ArtifactReleaseDiff firstExpected = createArtifactReleaseDiff(firstLogEntity,
                                                                      firstCurrentRelease,
                                                                      firstPreviousRelease,
                                                                      ["LUM-8045", "LUM-7759"] as Set,
                                                                      ["Ryan McKay"] as Set)

        ArtifactRelease secondCurrentRelease = createCurrentArtifactRelease(secondLogEntity, recentTrackerInfo.gitSha)
        ArtifactRelease secondPreviousRelease = createPreviousArtifactRelease(secondLogEntity, middleTrackerInfo.gitSha)
        ArtifactReleaseDiff secondExpected = createArtifactReleaseDiff(secondLogEntity,
                                                                       secondCurrentRelease,
                                                                       secondPreviousRelease,
                                                                       ["LUM-8045"] as Set,
                                                                       ["Blackbaud-JohnHolland", "Ryan McKay"] as Set)

        ArtifactRelease thirdCurrentRelease = createCurrentArtifactRelease(thirdLogEntity, recentBluemoonCoreInfo.gitSha);
        ArtifactRelease thirdPreviousRelease = createPreviousArtifactRelease(thirdLogEntity, olderBluemoonCoreInfo.gitSha)
        ArtifactReleaseDiff thirdExpected = createArtifactReleaseDiff(thirdLogEntity,
                                                                      thirdCurrentRelease,
                                                                      thirdPreviousRelease,
                                                                      ["LUM-9831"] as Set,
                                                                      ["Eric Slater", "Blackbaud-MikeDuVall"] as Set)

        then:
        List<ArtifactReleaseDiff> results = artifactReleaseReportClient.findAll(foundation)
        results.containsAll([firstExpected, secondExpected, thirdExpected])
        results.size() == 3
    }

    def "release of new artifact should have null previous release"() {
        given:
        artifactReleaseClient.create(foundation, "int", earlyTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll(foundation)

        then:
        assert artifactReleaseDiffs[0].currentRelease == earlyTrackerRelease
        assert artifactReleaseDiffs[0].prevRelease == emptyTrackerRelease
    }

    def "new release in same space should have correct previous release"() {
        given:
        artifactReleaseClient.create(foundation, space, earlyTrackerRelease)
        artifactReleaseClient.create(foundation, space, middleTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll(foundation)

        then:
        def middleReleaseDiff = artifactReleaseDiffs[0]
        assert middleReleaseDiff.currentRelease == middleTrackerRelease
        assert middleReleaseDiff.prevRelease == earlyTrackerRelease

        def earlyReleaseDiff = artifactReleaseDiffs[1]
        assert earlyReleaseDiff.currentRelease == earlyTrackerRelease
        assert earlyReleaseDiff.prevRelease == emptyTrackerRelease
    }

    def "new release in different space should have correct previous release"() {
        given:
        artifactReleaseClient.create(foundation, space, earlyTrackerRelease)
        artifactReleaseClient.create(foundation, aRandom.name(), recentTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll(foundation)

        then:
        assert artifactReleaseDiffs.size() == 2
        assert artifactReleaseDiffs[0].prevRelease == emptyTrackerRelease
        assert artifactReleaseDiffs[1].prevRelease == emptyTrackerRelease
    }

    def "should include releases with with older or same build versions"() {
        given:
        def releaseBuilder = ArtifactRelease.builder().artifactId('bluemoon-core').gitSha('b92937bcc183cb92f3f64abeca54a997d3de0c54')
        ArtifactRelease old = releaseBuilder.buildVersion('0.00000000.000000').releaseVersion('00000000_000000').build();
        ArtifactRelease recent = releaseBuilder.buildVersion('0.00000000.000001').releaseVersion('00000000_000001').build();
        ArtifactRelease redeployedRecent = releaseBuilder.buildVersion('0.00000000.000001').releaseVersion('00000000_000002').build();

        and:
        artifactReleaseClient.create(foundation, space, old);
        artifactReleaseClient.create(foundation, space, recent);
        artifactReleaseClient.create(foundation, space, redeployedRecent);

        expect:
        artifactReleaseReportClient.findAll(foundation).size() == 3
    }

    def "report should respect foundation"() {
        given:
        artifactReleaseClient.create(foundation, space, earlyTrackerRelease)
        artifactReleaseClient.create(aRandom.name(), space, recentTrackerRelease)

        expect:
        List<ArtifactReleaseDiff> releaseLogs = artifactReleaseReportClient.findAll(foundation)
        releaseLogs.size() == 1
        releaseLogs[0].currentRelease == earlyTrackerRelease
        releaseLogs[0].prevRelease == emptyTrackerRelease
    }

    private ArtifactRelease createCurrentArtifactRelease(ArtifactReleaseLogEntity logEntity, String gitSha) {
        ArtifactRelease.builder()
                .artifactId(logEntity.artifactId)
                .buildVersion(logEntity.buildVersion)
                .releaseVersion(logEntity.releaseVersion)
                .gitSha(gitSha)
                .deployJobUrl(logEntity.deployJobUrl)
                .build();
    }

    private ArtifactRelease createPreviousArtifactRelease(ArtifactReleaseLogEntity logEntity, String gitSha) {
        ArtifactRelease.builder()
                .artifactId(logEntity.artifactId)
                .buildVersion(logEntity.prevBuildVersion)
                .releaseVersion(logEntity.prevReleaseVersion)
                .gitSha(gitSha)
                .deployJobUrl(null)
                .build();
    }

    private ArtifactReleaseDiff createArtifactReleaseDiff(ArtifactReleaseLogEntity logEntity,
                                                          ArtifactRelease curRelease, ArtifactRelease prevRelease,
                                                          Set<String> stories, Set<String> developers) {
        return ArtifactReleaseDiff.builder()
                .artifactId(logEntity.artifactId)
                .currentRelease(curRelease)
                .prevRelease(prevRelease)
                .space(logEntity.space)
                .foundation(logEntity.foundation)
                .releaseDate(releaseDiffConverter.convertReleaseVersionToDate(logEntity.releaseVersion))
                .deployer(logEntity.deployer)
                .stories(stories)
                .developers(developers)
                .build();

    }
}
