package com.blackbaud.deployment.core.domain;

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

@Entity(name = "deployment_info")
@Table(name = "deployment_info")
@IdClass(ArtifactReleaseInfoPrimaryKey.class)
@Data
@EqualsAndHashCode(of = {"artifactId", "space", "foundation"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtifactReleaseInfoEntity {

    @Id
    @Column(name = "artifact_id")
    private String artifactId;
    @Id
    private String space;
    @Id
    private String foundation;
    @Column(name = "build_version")
    private String buildVersion;
    @Column(name = "release_version")
    private String releaseVersion;
    @Column(name = "git_sha")
    private String gitSha;

}
