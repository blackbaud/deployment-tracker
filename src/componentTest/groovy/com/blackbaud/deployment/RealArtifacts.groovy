package com.blackbaud.deployment

import com.blackbaud.deployment.api.ArtifactInfo
import com.blackbaud.deployment.api.ArtifactReleaseInfo

class RealArtifacts {

    def static getRecentDeploymentTrackerRelease() {
        ArtifactReleaseInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160606.194525")
                .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
                .releaseVersion("0.20160606.194525")
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

    def static getRecentDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160606.194525")
                .gitSha("e36ec0e653bb77dd20a6ac2c200d4a82a962e6e7")
                .storyIds(["LUM-8045", "LUM-7759"] as SortedSet<String>)
                .authors(["Blackbaud-JohnHolland", "Ryan McKay", "Mike Lueders", "Blackbaud-DiHuynh", "Di Huynh"] as SortedSet<String>)
                .build()
    }

    def static getEarlyDeploymentTrackerArtifact() {
        ArtifactInfo.builder()
                .artifactId("deployment-tracker")
                .buildVersion("0.20160525.221050")
                .gitSha("bb0ce6f142d3c52e48c914768f3174278bfa035b")
                .storyIds([] as SortedSet)
                .authors([] as SortedSet)
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

}
