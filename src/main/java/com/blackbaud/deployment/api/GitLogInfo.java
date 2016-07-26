package com.blackbaud.deployment.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitLogInfo {

    private String artifactId;
    private String gitSha;
    private String author;
    private String storyId;
    private ZonedDateTime commitTime;

}
