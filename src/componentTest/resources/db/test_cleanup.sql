truncate table deployment_info cascade;
truncate table artifact_info cascade;
truncate table git_log cascade;
alter sequence git_log_ordinal_seq restart with 1;