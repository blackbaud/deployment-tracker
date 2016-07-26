package com.blackbaud.deployment;

import com.blackbaud.deployment.client.ArtifactInfoClient;
import com.blackbaud.deployment.client.ArtifactReleaseInfoClient;
import com.blackbaud.deployment.client.BackfillGitLogClient;
import com.blackbaud.deployment.client.ReleaseClient;
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
    public BackfillGitLogClient backfillGitLogClient() {
        return new BackfillGitLogClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

}
