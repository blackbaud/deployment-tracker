package com.blackbaud.deployment;

import com.blackbaud.boot.BlackbaudSpringApplication;
import com.blackbaud.boot.config.CommonSpringConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan({"com.blackbaud.deployment", "com.blackbaud.boot.converters"})
@ComponentScan("com.blackbaud.deployment")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class DeploymentTrackerApp extends CommonSpringConfig {

    public static void main(String[] args) {
        BlackbaudSpringApplication.run(DeploymentTrackerApp.class, args);
    }

}
