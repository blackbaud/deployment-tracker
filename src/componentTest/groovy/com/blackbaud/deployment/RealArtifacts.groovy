package com.blackbaud.deployment

import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactRelease

class RealArtifacts {

    /* Artifact Release Infos */

    def static getRecentDeploymentTrackerRelease() {
        ArtifactRelease.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160606.194525")
                .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
                .releaseVersion("20160606_000000")
                .build()
    }

    def static getMiddleDeploymentTrackerRelease() {
        ArtifactRelease.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160603.161854")
                .gitSha("9ecbbedb36e9e9bd39f1781b6e4dcc0523da3e23")
                .releaseVersion("20160605_000000")
                .build()
    }

    def static getEarlyDeploymentTrackerRelease() {
        ArtifactRelease.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160525.221050")
                .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
                .releaseVersion("20160525_000000")
                .build()
    }

    def static getRecentNotificationsRelease() {
        ArtifactRelease.builder()
                .artifactId("notifications")
                .gitSha("90e59c3996f0027c2cb63e27c564566586ae29f8")
                .buildVersion("0.20160606.194525")
                .releaseVersion("20160606_194525")
                .build()
    }

    def static getBluemoonDojoRelease() {
        ArtifactRelease.builder()
                .artifactId("bluemoon-dojo")
                .buildVersion("0.20160606.194525")
                .releaseVersion("20160606_194525")
                .gitSha("5b9eade6e44b4ff7e9be94f5e7e97e5bfe282ff6")
                .build()
    }

    def static getRecentBluemoonUiRelease() {
        ArtifactInfo info = recentBluemoonUiArtifact
        ArtifactRelease.builder()
                .artifactId(info.artifactId)
                .buildVersion(info.buildVersion)
                .gitSha(info.gitSha)
                .releaseVersion("20160606_194525")
                .build()
    }

    def static getEarlyBluemoonUiRelease() {
        ArtifactInfo info = earlyBluemoonUiArtifact
        ArtifactRelease.builder()
                .artifactId(info.artifactId)
                .buildVersion(info.buildVersion)
                .gitSha(info.gitSha)
                .releaseVersion("20160303_194525")
                .build()
    }

    def static getEarlySegmentationComponentRelease() {
        ArtifactInfo info = earlySegmentationComponentArtifact
        ArtifactRelease.builder()
                .artifactId(info.artifactId)
                .buildVersion(info.buildVersion)
                .gitSha(info.gitSha)
                .releaseVersion("20160201_192125")
                .build()
    }

    def static getRecentSegmentationComponentRelease() {
        ArtifactInfo info = recentSegmentationComponentArtifact
        ArtifactRelease.builder()
                .artifactId(info.artifactId)
                .buildVersion(info.buildVersion)
                .gitSha(info.gitSha)
                .releaseVersion("20160606_194525")
                .build()
    }

    /* Artifact Infos */

    def static getRecentDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160606.194525")
                .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
                .build()
    }

    def static getMiddleDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160603.161854")
                .gitSha("9ecbbedb36e9e9bd39f1781b6e4dcc0523da3e23")
                .build()
    }

    def static getEarlyDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160525.221050")
                .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
                .build()
    }

    def static getRecentBluemoonCoreArtifact() {
        ArtifactInfo.builder()
                .artifactId("bluemoon-core")
                .buildVersion("2")
                .gitSha("b92937bcc183cb92f3f64abeca54a997d3de0c54")
                .build()
    }

    def static getEarlyBluemoonCoreArtifact() {
        ArtifactInfo.builder()
                .artifactId("bluemoon-core")
                .buildVersion("1")
                .gitSha("6f39d6f6f1b732439c9f1c45c3a2bc481ce8d314")
                .build()
    }

    def static getRecentSegmentationComponentArtifact() {
        ArtifactInfo.builder()
                .artifactId("segmentation-component")
                .buildVersion("0.20170525.088880")
                .gitSha("9b11539447083474a1ae850f10eac64b6fdf28d6")
                .build()
    }

    def static getEarlySegmentationComponentArtifact() {
        ArtifactInfo.builder()
                .artifactId("segmentation-component")
                .buildVersion("0.20170325.062840")
                .gitSha("e0bc85d1a5c932d51fcf5f69043d5c137bab2dc0")
                .build()
    }

    def static getEarlyBluemoonUiArtifact() {
        ArtifactInfo.builder()
                .artifactId("bluemoon-ui")
                .buildVersion("1.20170105.000004")
                .gitSha("f078056854df576299a5817fda6c60a2505d9732")
                .build()
    }

    def static getRecentBluemoonUiArtifact() {
        ArtifactInfo.builder()
                .artifactId("bluemoon-ui")
                .buildVersion("1.20170325.065544")
                .gitSha("7f34662785a46b042fa02ea96b4dd22428f23cf2")
                .build()
    }

}
