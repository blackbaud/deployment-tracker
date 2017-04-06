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

@Entity(name = "artifact_dependency")
@Table(name = "artifact_dependency")
@IdClass(ArtifactDependencyPrimaryKey.class)
@Data
@EqualsAndHashCode(of = {"artifactId", "buildVersion", "dependencyId", "dependencyBuildVersion"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtifactDependencyEntity {

    @Id
    @Column(name = "artifact_id")
    private String artifactId;

    @Id
    @Column(name = "build_version")
    private String buildVersion;

    @Id
    @Column(name = "dependency_id")
    private String dependencyId;

    @Id
    @Column(name = "dependency_build_version")
    private String dependencyBuildVersion;

}
