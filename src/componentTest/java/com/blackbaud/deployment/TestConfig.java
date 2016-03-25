package com.blackbaud.deployment;

import com.blackbaud.deployment.client.DeploymentTrackerClient;
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
    public DeploymentTrackerClient roleClient() {
        return new DeploymentTrackerClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

}
