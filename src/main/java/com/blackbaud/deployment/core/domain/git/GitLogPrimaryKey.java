package com.blackbaud.deployment.core.domain.git;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitLogPrimaryKey implements Serializable {

    private String artifactId;
    private String gitSha;

}
