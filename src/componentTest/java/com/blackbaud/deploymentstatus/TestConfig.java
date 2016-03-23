package com.blackbaud.deploymentstatus;

import com.blackbaud.deploymentstatus.client.DeploymentStatusClient;
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
    public DeploymentStatusClient roleClient() {
        return new DeploymentStatusClient(hostUri)
                .header(testTokenSupport.createTestTokenHeader());
    }

}
