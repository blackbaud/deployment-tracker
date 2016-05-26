package com.blackbaud.deployment;

import com.blackbaud.deployment.client.ArtifactInfoClient;
import com.blackbaud.deployment.client.DeploymentInfoClient;
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
    public DeploymentInfoClient deploymentTrackerClient() {
        return new DeploymentInfoClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

    @Bean
    public ArtifactInfoClient artifactInfoClient() {
        return new ArtifactInfoClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

}
