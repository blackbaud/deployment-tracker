package com.blackbaud.deployment

import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseInfo

class RealArtifacts {

    /* Artifact Release Infos */

    def static getRecentDeploymentTrackerRelease() {
        ArtifactReleaseInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160606.194525")
                .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
                .releaseVersion("0.20160606.194525")
                .build()
    }

    def static getMiddleDeploymentTrackerRelease() {
        ArtifactReleaseInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160603.193021")
                .gitSha("629e1734a9e6d44e83877d44cd15ffdbd99d27c8")
                .releaseVersion("0.20160603.750143")
                .build()
    }

    def static getEarlyDeploymentTrackerRelease() {
        ArtifactReleaseInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160525.221050")
                .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
                .releaseVersion("0.20160525.221050")
                .build()
    }

    def static getRecentNotificationsRelease() {
        ArtifactReleaseInfo.builder()
                .artifactId("notifications")
                .gitSha("90e59c3996f0027c2cb63e27c564566586ae29f8")
                .buildVersion("0.20160606.194525")
                .releaseVersion("0.20160606.194525")
                .build()
    }

    def static getBluemoonDojoRelease() {
        ArtifactReleaseInfo.builder()
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
                .build()
    }

    def static getMiddleDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160603.750143")
                .gitSha("629e1734a9e6d44e83877d44cd15ffdbd99d27c8")
                .build()
    }

    def static getEarlyDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160525.221050")
                .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
                .build()
    }

    def static getBluemoonCoreArtifact() {
        ArtifactInfo.builder()
                .artifactId("bluemoon-core")
                .buildVersion("1")
                .gitSha("b92937bcc183cb92f3f64abeca54a997d3de0c54")
                .build()
    }


}
