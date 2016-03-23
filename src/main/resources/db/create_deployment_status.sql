--liquibase formatted sql

--changeset blackbaud:1
create table deployment_status (
  id uuid constraint deployment_status_pk primary key
)
--rollback drop table deployment_status