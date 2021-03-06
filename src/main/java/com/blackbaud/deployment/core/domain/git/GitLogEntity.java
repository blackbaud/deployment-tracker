package com.blackbaud.deployment.core.domain.git;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "git_log")
@IdClass(GitLogPrimaryKey.class)
@Data
@EqualsAndHashCode(of = {"artifactId", "gitSha", "author", "storyId", "commitTime"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GitLogEntity {

    @Id
    @Column(name = "artifact_id")
    public String artifactId;

    @Id
    @Column(name = "git_sha")
    public String gitSha;

    @Column(name = "author")
    public String author;

    @Column(name = "story_id")
    public String storyId;

    @Column(name = "commit_time")
    public ZonedDateTime commitTime;

}
