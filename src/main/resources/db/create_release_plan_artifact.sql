--liquibase formatted sql

--changeset blackbaud:1
create table release_plan_artifact (
  release_plan_id int not null REFERENCES release_plan(id),
  artifact_id varchar(100) not null REFERENCES artifact_info(artifact_id),
  build_version varchar(100) not null REFERENCES artifact_info(build_version),
  constraint release_plan_artifact_pk primary key (release_plan_id, artifact_id, build_version)
)
--rollback drop table release_plan_artifact
