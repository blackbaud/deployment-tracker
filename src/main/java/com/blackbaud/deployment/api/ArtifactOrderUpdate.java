package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactOrderUpdate {

    private String moveSha;
    private String anchorSha;
    private String position;
}
