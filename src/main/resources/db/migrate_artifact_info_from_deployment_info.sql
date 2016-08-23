--liquibase formatted sql

--changeset blackbaud:1
insert into artifact_info (artifact_id, build_version, git_sha) select a.artifact_id, a.build_version, a.git_sha from deployment_info as a
LEFT JOIN artifact_info as b using(artifact_id, build_version)
where b.artifact_id is null

