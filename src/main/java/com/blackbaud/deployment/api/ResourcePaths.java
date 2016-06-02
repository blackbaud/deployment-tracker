package com.blackbaud.deployment.api;

public class ResourcePaths {
    public static final String DEPLOYMENT_TRACKER_PATH = "/deployment-tracker";
    public static final String DEPLOYMENT_INFO_PATH = DEPLOYMENT_TRACKER_PATH + "/deployment-info";
    public static final String ARTIFACT_INFO_PATH = DEPLOYMENT_TRACKER_PATH + "/artifact-info";

    public static final String RELEASE_PATH = DEPLOYMENT_TRACKER_PATH + "/release";
    public static final String CURRENT_PATH = "current";
    public static final String DEPRECATED_CURRENT_SUMMARY_PATH = "current/summary";
}
