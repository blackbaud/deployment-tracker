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

@Entity(name = "artifact_info")
@Table(name = "artifact_info")
@IdClass(ArtifactInfoPrimaryKey.class)
@Data
@EqualsAndHashCode(of = {"artifactId", "buildVersion"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtifactInfoEntity {

    @Id
    @Column(name = "artifact_id")
    private String artifactId;

    @Id
    @Column(name = "build_version")
    private String buildVersion;

    @Column(name = "git_sha")
    private String gitSha;

    @Column(name = "list_order")
    private Integer listOrder;

}
