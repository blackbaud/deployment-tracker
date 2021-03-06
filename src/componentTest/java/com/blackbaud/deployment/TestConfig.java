package com.blackbaud.deployment;

import com.blackbaud.deployment.client.ArtifactInfoClient;
import com.blackbaud.deployment.client.ArtifactReleaseClient;
import com.blackbaud.deployment.client.ArtifactReleaseReportClient;
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
    public ArtifactReleaseReportClient artifactReleaseLogClient() {
        return new ArtifactReleaseReportClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

    @Bean
    public ArtifactReleaseClient artifactReleaseClient() {
        return new ArtifactReleaseClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }
}
