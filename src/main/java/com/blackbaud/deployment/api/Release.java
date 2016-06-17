package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Release {
<<<<<<< HEAD
<<<<<<< HEAD
    private Map<String, ArtifactReleaseDiff> releaseDiff = Collections.emptyMap();
=======
    private Map<String, ArtifactReleaseDiff> deploymentDiffs = Collections.emptyMap();
>>>>>>> f678692... more renaming
=======
    private Map<String, ArtifactReleaseDiff> releaseDiff = Collections.emptyMap();
>>>>>>> e82996a... so much renaming
}
