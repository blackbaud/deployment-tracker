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
                .releaseVersion("0.20160606.194525")
                .gitSha("5b9eade6e44b4ff7e9be94f5e7e97e5bfe282ff6")
                .build()
    }

    /* Artifact Infos */

    def static getRecentDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160606.194525")
                .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
                .releasePlanOrder(1)
                .build()
    }

    def static getMiddleDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160603.161854")
                .gitSha("9ecbbedb36e9e9bd39f1781b6e4dcc0523da3e23")
                .releasePlanOrder(2)
                .build()
    }

    def static getEarlyDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160525.221050")
                .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
                .releasePlanOrder(3)
                .build()
    }

    def static getRecentBluemoonCoreArtifact() {
        ArtifactInfo.builder()
                .artifactId("bluemoon-core")
                .buildVersion("2")
                .gitSha("b92937bcc183cb92f3f64abeca54a997d3de0c54")
                .releasePlanOrder(4)
                .build()
    }

    def static getEarlyBluemoonCoreArtifact() {
        ArtifactInfo.builder()
                .artifactId("bluemoon-core")
                .buildVersion("1")
                .gitSha("6f39d6f6f1b732439c9f1c45c3a2bc481ce8d314")
                .releasePlanOrder(5)
                .build()
    }

}
