package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevProdDeploymentInfos {
    private DeploymentInfo dev;
    private DeploymentInfo prod;
    public Boolean sameVersion() {
        if (dev == null || prod == null){
            return false;
        }
        return dev.getBuildVersion().equals(prod.getBuildVersion());
    }
}
