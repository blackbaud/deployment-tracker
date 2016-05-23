package com.blackbaud.deployment.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.URL;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ArtifactDiff {
    private ArtifactInfo fromArtifactInfo;
    private ArtifactInfo toArtifactInfo;
    private String gitCommits;
    private List<URL> stories;
}
