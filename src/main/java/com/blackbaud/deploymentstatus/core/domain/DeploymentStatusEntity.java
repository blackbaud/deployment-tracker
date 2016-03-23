package com.blackbaud.deploymentstatus.core.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name = "deployment_status")
@Table(name = "deployment_status")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeploymentStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "deployment_status_seq_gen")
    @SequenceGenerator(name = "deployment_status_seq_gen", sequenceName = "deployment_status_seq")
    private Long id;
    @Column(name = "app_name")
    private String appName;
    private String space;
    private String foundation;
    @Column(name = "build_version")
    private String buildVersion;
    @Column(name = "release_version")
    private String releaseVersion;
    @Column(name = "git_sha")
    private String gitSha;

}
