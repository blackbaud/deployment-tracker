package com.blackbaud.deployment.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SqlResultSetMapping;
import java.util.SortedSet;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ArtifactReleaseLogPrimaryKey.class)
@Entity
@SqlResultSetMapping(
        name = "ArtifactReleaseLogReportResult",
        entities = @EntityResult(
                entityClass = ArtifactReleaseLogReportResult.class,
                fields = {
                        @FieldResult(name = "artifactId", column = "artifact_id"),
                        @FieldResult(name = "releaseVersion", column = "release_version"),
                        @FieldResult(name = "buildVersion", column = "build_version"),
                        @FieldResult(name = "gitSha", column = "git_sha"),
                        @FieldResult(name = "deployJobUrl", column = "deploy_job_url"),
                        @FieldResult(name = "prevBuildVersion", column = "prev_build_version"),
                        @FieldResult(name = "prevReleaseVersion", column = "prev_release_version"),
                        @FieldResult(name = "prevGitSha", column = "prev_git_sha"),
                        @FieldResult(name = "prevDeployJobUrl", column = "prev_deploy_job_url"),
                        @FieldResult(name = "deployer", column = "deployer"),
                        @FieldResult(name = "space", column = "space"),
                        @FieldResult(name = "foundation", column = "foundation"),
                        @FieldResult(name = "stories", column = "stories"),
                        @FieldResult(name = "developers", column = "developers")
                }
        )
)
public class ArtifactReleaseLogReportResult {
    @Id
    private String artifactId;
    @Id
    private String releaseVersion;
    private String buildVersion;
    private String gitSha;
    private String deployJobUrl;
    private String prevBuildVersion;
    private String prevReleaseVersion;
    private String prevGitSha;
    private String prevDeployJobUrl;
    private String deployer;
    private String space;
    private String foundation;
    @Convert(converter = SortedSetDelimitedStringConverter.class)
    private SortedSet<String> stories;
    @Convert(converter = SortedSetDelimitedStringConverter.class)
    private SortedSet<String> developers;
}
