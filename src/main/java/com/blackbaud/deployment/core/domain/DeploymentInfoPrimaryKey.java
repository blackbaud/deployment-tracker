package com.blackbaud.deployment.core.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeploymentInfoPrimaryKey implements Serializable {

    private String artifactId;
    private String space;
    private String foundation;

}
