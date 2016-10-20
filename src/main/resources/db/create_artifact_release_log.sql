--liquibase formatted sql

--changeset blackbaud:1
create table artifact_release_log (
  space varchar(100) not null,
  foundation varchar(100) not null,
  artifact_id varchar(100) not null,
  deployer varchar(100),
  build_version varchar(100) not null,
  release_version varchar(100) not null,
  prev_build_version varchar(100),
  prev_release_version varchar(100),

  constraint artifact_release_log_pk primary key (artifact_id, release_version)
)
--rollback drop table artifact_release_log

--changeset blackbaud:2
insert into artifact_release_log (space, foundation, artifact_id, build_version, release_version) select space, foundation, artifact_id, build_version, release_version from deployment_info;
-- truncate table artifact_release_log

--changeset blackbaud:3
alter table artifact_release_log add constraint artifact_release_log_fk foreign key (artifact_id, build_version) references artifact_info (artifact_id, build_version);
--rollback alter table artifact_release_log drop constraint artifact_release_log_fk

--changeset blackbaud:4
alter table artifact_release_log add column deploy_job_url varchar(512);
--rollback alter table artifact_release_log drop column deploy_job_url
