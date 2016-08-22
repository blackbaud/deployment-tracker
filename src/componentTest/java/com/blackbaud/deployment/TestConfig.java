package com.blackbaud.deployment;

import com.blackbaud.deployment.client.ArtifactInfoClient;
import com.blackbaud.deployment.client.ArtifactReleaseClient;
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient;
import com.blackbaud.deployment.client.ArtifactReleaseDiffClient;
import com.blackbaud.deployment.client.GitLogInfoClient;
import com.blackbaud.deployment.client.ReleaseClient;
import com.blackbaud.deployment.client.ReleasePlanClient;
import com.blackbaud.testsupport.BaseTestConfig;
import com.blackbaud.testsupport.TestTokenSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig extends BaseTestConfig {

    @Autowired
    TestTokenSupport testTokenSupport;

    @Bean
    public ArtifactReleaseInfoClient deploymentTrackerClient() {
        return new ArtifactReleaseInfoClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

    @Bean
    public ReleaseClient releaseClient() {
        return new ReleaseClient(hostUri);
    }

    @Bean
    public ArtifactInfoClient artifactInfoClient() {
        return new ArtifactInfoClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

    @Bean
    public GitLogInfoClient backfillGitLogClient() {
        return new GitLogInfoClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

    @Bean
    public ReleasePlanClient releasePlanClient() {
        return new ReleasePlanClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

    @Bean
    public ArtifactReleaseDiffClient artifactReleaseLogClient() {
        return new ArtifactReleaseDiffClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

    @Bean
    public ArtifactReleaseClient artifactReleaseClient() {
        return new ArtifactReleaseClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }
}
