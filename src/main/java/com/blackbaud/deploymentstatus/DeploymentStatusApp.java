package com.blackbaud.deploymentstatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan({"com.blackbaud.deploymentstatus", "com.blackbaud.boot.converters"})
@ComponentScan("com.blackbaud.deploymentstatus")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class DeploymentStatusApp {

    public static void main(String[] args) {
        SpringApplication.run(DeploymentStatusApp.class, args);
    }

}
