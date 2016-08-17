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

@Entity(name = "artifact_release_info_log")
@Table(name = "artifact_release_info_log")
@IdClass(ArtifactReleaseInfoLogPrimaryKey.class)
@Data
@EqualsAndHashCode(of = {"artifactId", "releaseVersion"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtifactReleaseInfoLogEntity {

    @Id
    @Column(name = "artifact_id")
    private String artifactId;

    @Id
    @Column(name = "release_version")
    private String releaseVersion;

    @Column(name = "build_version")
    private String buildVersion;

    @Column(name = "prev_build_version")
    private String prevBuildVersion;

    @Column(name = "prev_release_version")
    private String prevReleaseVersion;

    private String deployer;

    private String space;

    private String foundation;


}
