package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReleasePlan {
    private Long id;
    private String notes;
    private ZonedDateTime created;
    private ZonedDateTime activated;
    private ZonedDateTime archived;
    private List<ArtifactInfo> artifacts;
}
