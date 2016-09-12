package com.blackbaud.deployment.core.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "release_plan")
@Table(name = "release_plan")
@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReleasePlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "release_plan_seq_gen")
    @SequenceGenerator(name = "release_plan_seq_gen", sequenceName = "release_plan_seq")
    private Long id;

    private ZonedDateTime created;
    private ZonedDateTime activated;
    private String notes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "release_plan_artifact",
            joinColumns = @JoinColumn(name = "release_plan_id", referencedColumnName = "id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "artifact_id", referencedColumnName = "artifact_id"),
                    @JoinColumn(name = "build_version", referencedColumnName = "build_version")
            }
    )
    private List<ArtifactInfoEntity> artifacts;

    public void addArtifact(ArtifactInfoEntity newArtifact) {
        if (artifacts == null) {
            artifacts = new ArrayList<>();
        }
        deleteArtifact(newArtifact.getArtifactId());
        artifacts.add(newArtifact);
    }

    public void deleteArtifact(String artifactId) {
        if (artifacts == null) {
            return;
        }
        artifacts = artifacts.stream().filter(
                artifact -> !artifact.getArtifactId().equals(artifactId)
        ).collect(Collectors.toList());
    }
}
