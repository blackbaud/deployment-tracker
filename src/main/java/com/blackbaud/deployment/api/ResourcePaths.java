package com.blackbaud.deployment.api;

public class ResourcePaths {
    public static final String DEPLOYMENT_TRACKER_PATH = "/deployment-tracker";
    public static final String ARTIFACT_INFO_PATH = DEPLOYMENT_TRACKER_PATH + "/artifact-info";
    public static final String ARTIFACT_RELEASE_INFO_PATH = DEPLOYMENT_TRACKER_PATH + "/artifact-release-info";
    public static final String ARTIFACT_RELEASE_PATH = DEPLOYMENT_TRACKER_PATH + "/artifact-release";
    public static final String RELEASE_PATH = DEPLOYMENT_TRACKER_PATH + "/release";
    public static final String GIT_LOG_INFO_PATH = DEPLOYMENT_TRACKER_PATH + "/gitlog";
    public static final String CURRENT_PATH = "current";
    public static final String RELEASE_PLAN_PATH = DEPLOYMENT_TRACKER_PATH + "/release-plan";
    public static final String NOTES_PATH = "notes";
    public static final String ACTIVATE_PATH = "activate";
    public static final String ARTIFACT_PATH = "artifacts";
    public static final String RELEASE_PLAN_DIFF_PATH = CURRENT_PATH + "/release-plan-diff";
    public static final String ARTIFACT_RELEASE_REPORT = DEPLOYMENT_TRACKER_PATH + "/artifact-release-report";
    public static final String RELEASE_PLAN_ARTIFACT_REORDER = DEPLOYMENT_TRACKER_PATH + "/release-plan-artifact-reorder";
    public static final String REMEDIATE_PATH = "remediate";
    public static final String RELEASE_PLAN_ARTIFACT_REORDER = DEPLOYMENT_TRACKER_PATH + "/release-plan-artifact-reorder";
}
