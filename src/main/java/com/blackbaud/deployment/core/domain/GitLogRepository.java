package com.blackbaud.deployment.core.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GitLogRepository extends CrudRepository<GitLogEntity, GitLogPrimaryKey> {

    @Query(value = "select * from git_log where ordinal <= (select ordinal from git_log where git_sha = :currentSha) and ordinal > (select ordinal from git_log where git_sha = :previousSha)", nativeQuery = true)
    List<GitLogEntity> fetchGitLogForCurrentAndPreviousGitShas(@Param("currentSha") String currentSha, @Param("previousSha") String previousSha);

    @Query(value = "select * from git_log where ordinal <= (select ordinal from git_log where git_sha = :sha)", nativeQuery = true)
    List<GitLogEntity> fetchGitLogUntilSha(@Param("sha") String sha);

    @Query(value = "select * from git_log where artifact_id = :artifactId order by ordinal asc", nativeQuery = true)
    List<GitLogEntity> fetchOrderedGitLogForArtifactId(@Param("artifactId") String artifactId);

    @Query(value = "select git_sha from git_log where artifact_id = :artifactId order by ordinal desc limit 1", nativeQuery = true)
    String getMostRecentShaForArtifact(@Param("artifactId") String artifactId);

}
