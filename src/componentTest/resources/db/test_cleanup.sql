truncate table deployment_info cascade;
truncate table artifact_info cascade;
truncate table git_log cascade;
truncate table release_plan cascade;
truncate table artifact_release_info_log cascade;
alter sequence git_log_ordinal_seq restart with 1;