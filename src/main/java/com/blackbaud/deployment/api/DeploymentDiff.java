package com.blackbaud.deployment.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentDiff {
    private DeploymentInfo dev;
    private DeploymentInfo prod;
    private Set<String> stories = Collections.emptySet();

    public Boolean sameVersion() {
        if (!hasBothDeploymentInfos()) {
            return false;
        }
        return dev.getBuildVersion().equals(prod.getBuildVersion());
    }

    @JsonIgnore
    public String getDevSha() {
        return dev == null ? null : dev.getGitSha();
    }

    @JsonIgnore
    public String getProdSha() {
        return prod == null ? null : prod.getGitSha();
    }

    public Boolean hasBothDeploymentInfos() {
        return dev != null && prod != null;
    }

    public boolean hasBothShas() {
        return hasBothDeploymentInfos() &&
                getDevSha() != null && getProdSha() != null;

    }
}
