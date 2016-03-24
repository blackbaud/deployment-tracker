package com.blackbaud.deploymentstatus.core.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeploymentStatusPrimaryKey implements Serializable {

    private String appName;
    private String space;
    private String foundation;

}
