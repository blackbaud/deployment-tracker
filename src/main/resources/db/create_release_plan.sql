--liquibase formatted sql

--changeset blackbaud:1
create table release_plan (
  id int constraint release_plan_pk primary key,
  create_date DATE,
  close_date DATE,
  notes TEXT
)