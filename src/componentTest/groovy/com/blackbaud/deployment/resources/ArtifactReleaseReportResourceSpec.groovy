package com.blackbaud.deployment.resources

import com.blackbaud.deployment.ArtifactReleaseDiffConverter
import com.blackbaud.deployment.ComponentTest
import com.blackbaud.deployment.RealArtifacts
import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease
import com.blackbaud.deployment.api.ArtifactReleaseDiff
import com.blackbaud.deployment.client.ArtifactInfoClient
import com.blackbaud.deployment.client.ArtifactReleaseClient
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient
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
    private ArtifactReleaseDiffConverter releaseDiffConverter;

    private static String trackerArtifactId = 'deployment-tracker';
    private static String coreArtifactId = 'bluemoon-core';
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
        ArtifactReleaseLogEntity firstLogEntity = aRandom.releaseLogEntity().artifactId(trackerArtifactId).buildVersion(middleTrackerInfo.buildVersion).prevBuildVersion(earlyTrackerInfo.buildVersion).build()
        ArtifactReleaseLogEntity secondLogEntity = aRandom.releaseLogEntity().artifactId(trackerArtifactId).buildVersion(recentTrackerInfo.buildVersion).prevBuildVersion(middleTrackerInfo.buildVersion).build()
        ArtifactReleaseLogEntity thirdLogEntity = aRandom.releaseLogEntity().artifactId(coreArtifactId).buildVersion(recentBluemoonCoreInfo.buildVersion).prevBuildVersion(olderBluemoonCoreInfo.buildVersion).build()
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
                ["Blackbaud-JohnHolland","Ryan McKay"] as Set)

        ArtifactRelease thirdCurrentRelease = createCurrentArtifactRelease(thirdLogEntity, recentBluemoonCoreInfo.gitSha);
        ArtifactRelease thirdPreviousRelease = createPreviousArtifactRelease(thirdLogEntity, olderBluemoonCoreInfo.gitSha)
        ArtifactReleaseDiff thirdExpected = createArtifactReleaseDiff(thirdLogEntity,
                thirdCurrentRelease,
                thirdPreviousRelease,
                ["LUM-9831"] as Set,
                ["Eric Slater","Blackbaud-MikeDuVall"] as Set)

        then:
        List<ArtifactReleaseDiff> results = artifactReleaseReportClient.findAll()
        results.containsAll([firstExpected, secondExpected, thirdExpected])
        results.size() == 3
    }

    def "release of new artifact should have null previous release"() {
        given:
        artifactReleaseClient.create("foundation1", "int", earlyTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
        assert artifactReleaseDiffs[0].currentRelease == earlyTrackerRelease
        assert artifactReleaseDiffs[0].prevRelease == emptyTrackerRelease
    }

    def "new release in same space should have correct previous release"() {
        given:
        artifactReleaseClient.create("foundation1", "int", earlyTrackerRelease)
        artifactReleaseClient.create("foundation1", "int", middleTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

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
        artifactReleaseClient.create("foundation1", "int", earlyTrackerRelease)
        artifactReleaseClient.create("foundation1", "dev", recentTrackerRelease)

        when:
        def artifactReleaseDiffs = artifactReleaseReportClient.findAll()

        then:
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
        artifactReleaseClient.create("foundation", "space", old);
        artifactReleaseClient.create("foundation", "space", recent);
        artifactReleaseClient.create("foundation", "space", redeployedRecent);


        expect:
        artifactReleaseReportClient.findAll().size() == 3
    }

    private ArtifactRelease createCurrentArtifactRelease(ArtifactReleaseLogEntity logEntity, String gitSha) {
        return new ArtifactRelease(logEntity.artifactId, logEntity.buildVersion, logEntity.releaseVersion, gitSha);
    }

    private ArtifactRelease createPreviousArtifactRelease(ArtifactReleaseLogEntity logEntity, String gitSha) {
        return new ArtifactRelease(logEntity.artifactId, logEntity.prevBuildVersion, logEntity.prevReleaseVersion, gitSha);
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
