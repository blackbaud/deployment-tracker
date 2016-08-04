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

@Entity(name = "release_plan")
@Table(name = "release_plan")
@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReleasePlanEntity {

    @Id
    private int id;
}
