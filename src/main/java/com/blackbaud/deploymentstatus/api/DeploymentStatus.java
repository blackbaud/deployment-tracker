package com.blackbaud.deploymentstatus.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = {"appName", "buildVersion", "releaseVersion", "gitSha"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentStatus {

    private String appName;
    private String buildVersion;
    private String releaseVersion;
    private String gitSha;

}
