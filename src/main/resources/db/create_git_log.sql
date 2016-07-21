--liquibase formatted sql

--changeset blackbaud:1
create sequence git_log_ordinal_seq;
create table git_log (
  artifact_id varchar(100) not null,
  git_sha varchar(100),
  author varchar(100),
  story_id varchar(100),
  ordinal smallint default nextval('git_log_ordinal_seq'),
  commit_time timestamp without time zone,
  constraint "git_log_pk" primary key ("artifact_id", "git_sha")
)
