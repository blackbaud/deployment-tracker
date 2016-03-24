package com.blackbaud.deploymentstatus.core.domain;

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

@Entity(name = "deployment_status")
@Table(name = "deployment_status")
@IdClass(DeploymentStatusPrimaryKey.class)
@Data
@EqualsAndHashCode(of = {"appName", "space", "foundation"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeploymentStatusEntity {

    @Id
    @Column(name = "app_name")
    private String appName;
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
