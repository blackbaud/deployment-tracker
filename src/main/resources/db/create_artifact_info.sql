--liquibase formatted sql

--changeset blackbaud:1
create table artifact_info (
  artifact_id varchar(100) not null,
  build_version varchar(100) not null,
  git_sha varchar(100),
  constraint artifact_info_pk primary key (artifact_id, build_version)
)
--rollback drop table artifact_info