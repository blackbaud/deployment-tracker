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

--changeset blackbaud:2 endDelimiter:#
#
CREATE OR REPLACE FUNCTION fetch_git_log(artifactId varchar, curBuildVersion varchar, prevBuildVersion varchar) RETURNS setof git_log as $$
DECLARE
  curVersionOrdinal integer;
  prevVersionOrdinal integer;
BEGIN
  begin
    select into strict curVersionOrdinal ordinal from git_log where git_sha = (select git_sha from artifact_info where artifact_id = artifactId and build_Version = curBuildVersion);
  exception
      when no_data_found then
        raise notice 'current version has no associated git sha!!!';
  end;

  begin
    select into strict prevVersionOrdinal ordinal from git_log where git_sha = (select git_sha from artifact_info where artifact_id = artifactId and build_Version = prevBuildVersion);
  exception
      when no_data_found then
        prevVersionOrdinal := 0;
  end;

  return query select * from git_log where ordinal <= curVersionOrdinal and ordinal > prevVersionOrdinal and artifact_id = artifactId;
END;
$$ language plpgsql;
#
--rollback drop function fetch_git_log(varchar,varchar,varchar)

--changeset blackbaud:3
create unique index git_log_artifact_id_ordinal_idx on git_log(artifact_id, ordinal);
--rollback drop index if exists git_log_artifact_id_ordinal_idx;

--changeset blackbaud:4 endDelimiter:#
#
CREATE OR REPLACE FUNCTION fetch_git_log(artifactId varchar, curBuildVersion varchar, prevBuildVersion varchar) RETURNS setof git_log as $$
DECLARE
  curVersionOrdinal integer;
  prevVersionOrdinal integer;
BEGIN
  begin
    select into strict curVersionOrdinal ordinal from git_log where artifact_id = artifactId and git_sha = (select git_sha from artifact_info where artifact_id = artifactId and build_Version = curBuildVersion);
  exception
      when no_data_found then
        raise notice 'current version has no associated git sha!!!';
  end;

  begin
    select into strict prevVersionOrdinal ordinal from git_log where artifact_id = artifactId and git_sha = (select git_sha from artifact_info where artifact_id = artifactId and build_Version = prevBuildVersion);
  exception
      when no_data_found then
        prevVersionOrdinal := 0;
  end;

  return query select * from git_log where ordinal <= curVersionOrdinal and ordinal > prevVersionOrdinal and artifact_id = artifactId;
END;
$$ language plpgsql;
#
--rollback drop function fetch_git_log(varchar,varchar,varchar)
