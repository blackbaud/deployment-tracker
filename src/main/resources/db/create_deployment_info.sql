--liquibase formatted sql

--changeset blackbaud:1
create sequence deployment_info_seq
--rollback drop sequence deployment_info_seq

--changeset blackbaud:2
create table deployment_info (
  space varchar(100) not null,
  foundation varchar(100) not null,
  artifact_id varchar(100) not null,
  build_version varchar(100) not null,
  release_version varchar(100) not null,
  git_sha varchar(100),
  constraint deployment_info_pk primary key (artifact_id, space, foundation)
)
--rollback drop table deployment_info