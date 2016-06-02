package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevProdDeploymentInfos {
    private DeploymentInfo dev;
    private DeploymentInfo prod;
    private Set<String> stories;

    public Boolean sameVersion() {
        if (!hasBothDeploymentInfos()) {
            return false;
        }
        return dev.getBuildVersion().equals(prod.getBuildVersion());
    }

    public String getDevSha() {
        return dev == null ? null : dev.getGitSha();
    }

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
