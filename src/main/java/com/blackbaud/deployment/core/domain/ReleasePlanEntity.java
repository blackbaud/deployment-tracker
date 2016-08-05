package com.blackbaud.deployment.core.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;

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
    private ZonedDateTime closed;
    private String notes;

    @ManyToMany
    @JoinTable(
            name = "release_plan_artifact",
            joinColumns = @JoinColumn(name = "release_plan_id", referencedColumnName = "id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "artifact_id", referencedColumnName = "artifact_id"),
                    @JoinColumn(name = "build_version", referencedColumnName = "build_version")
            }
    )
    private List<ArtifactInfoEntity> artifacts;
}
