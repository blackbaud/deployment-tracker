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
