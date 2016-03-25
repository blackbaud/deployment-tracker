--liquibase formatted sql

--changeset blackbaud:1
create sequence deployment_status_seq
--rollback drop sequence deployment_status_seq

--changeset blackbaud:2
create table deployment_status (
  space varchar(100) not null,
  foundation varchar(100) not null,
  artifact_id varchar(100) not null,
  build_version varchar(100) not null,
  release_version varchar(100) not null,
  git_sha varchar(100),
  constraint deployment_status_pk primary key (artifact_id, space, foundation)
)
--rollback drop table deployment_status