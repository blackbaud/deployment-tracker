package com.blackbaud.deployment.core.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Component
@Slf4j
public class ArtifactReleaseLogReportQuery {

    @Inject
    private EntityManager entityManager;

    public List<ArtifactReleaseLogReportResult> getArtifactReleaseReport() {
        Query query = entityManager.createNativeQuery(
                "select arl.*, string_agg(distinct gl.story_id\\:\\:text, ',') as stories, " +
                        "string_agg(distinct gl.author\\:\\:text, ',') as developers, " +
                        "(select git_sha from artifact_info where build_version = arl.build_version and artifact_id = arl.artifact_id) as git_sha, " +
                        "(select git_sha from artifact_info where build_version = arl.prev_build_version and artifact_id = arl.artifact_id) as prev_git_sha " +
                        "from artifact_release_log arl left join " +
                        "fetch_git_log(arl.artifact_id, arl.build_version, arl.prev_build_version) gl " +
                        "on arl.artifact_id = gl.artifact_id " +
                        "group by arl.artifact_id, arl.foundation, arl.space, arl.deployer, arl.release_version, arl.build_version, arl.prev_release_version, arl.prev_build_version " +
                        "order by release_version desc;",
                ArtifactReleaseLogReportResult.class);
        return query.getResultList();
    }

    public List<ArtifactReleaseLogReportResult> getArtifactReleaseReport(String foundation) {
        Query query = entityManager.createNativeQuery(
                "select arl.*, string_agg(distinct gl.story_id\\:\\:text, ',') as stories, " +
                        "string_agg(distinct gl.author\\:\\:text, ',') as developers, " +
                        "(select git_sha from artifact_info where build_version = arl.build_version and artifact_id = arl.artifact_id) as git_sha, " +
                        "(select git_sha from artifact_info where build_version = arl.prev_build_version and artifact_id = arl.artifact_id) as prev_git_sha " +
                        "from artifact_release_log arl left join " +
                        "fetch_git_log(arl.artifact_id, arl.build_version, arl.prev_build_version) gl " +
                        "on arl.artifact_id = gl.artifact_id " +
                        "where arl.foundation = :foundation " +
                        "group by arl.artifact_id, arl.foundation, arl.space, arl.deployer, arl.release_version, arl.build_version, arl.prev_release_version, arl.prev_build_version " +
                        "order by release_version desc;",
                ArtifactReleaseLogReportResult.class);
        query.setParameter("foundation", foundation);
        return query.getResultList();
    }

}
