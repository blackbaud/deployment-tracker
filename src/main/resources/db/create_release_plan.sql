--liquibase formatted sql

--changeset blackbaud:1
create sequence release_plan_seq;
--rollback drop sequence release_plan_seq

--changeset blackbaud:2
create table release_plan (
  id int not null constraint release_plan_pk primary key,
  created timestamp without time zone,
  closed timestamp without time zone,
  activated timestamp without time zone,
  notes TEXT
)
--rollback drop table release_plan
