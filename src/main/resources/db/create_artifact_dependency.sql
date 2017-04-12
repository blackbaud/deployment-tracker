--liquibase formatted sql

--changeset blackbaud:1
create table artifact_dependency(
  artifact_id varchar(100) not null,
  build_version varchar(100) not null,
  dependency_id varchar(100) not null,
  dependency_build_version varchar(100) not null,
  constraint artifact_dependency_pk primary key (artifact_id, build_version, dependency_id)
)
