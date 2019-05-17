package com.sap.cloud.lm.sl.cf.client.lib.domain;

import org.cloudfoundry.client.lib.domain.CloudInfo;

public class CloudInfoExtended extends CloudInfo {

    private boolean portBasedRouting;
    private String deployServiceUrl;
    private boolean hasTasksSupport;

    // Required by Jackson.
    protected CloudInfoExtended() {
    }

    public CloudInfoExtended(String name, String support, String authorizationEndpoint, String build, String version, String user,
        String description, Limits limits, Usage usage, boolean allowDebug, String loggingEndpoint, boolean portBasedRouting,
        String deployServiceUrl, boolean hasTasksSupport) {
        super(name, support, authorizationEndpoint, build, version, user, description, limits, usage, allowDebug, loggingEndpoint);
        this.portBasedRouting = portBasedRouting;
        this.deployServiceUrl = deployServiceUrl;
        this.hasTasksSupport = hasTasksSupport;
    }

    public boolean isPortBasedRouting() {
        return portBasedRouting;
    }

    public String getDeployServiceUrl() {
        return deployServiceUrl;
    }

    public boolean hasTasksSupport() {
        return hasTasksSupport;
    }

    public String getServiceUrl(String serviceName) {
        return null;
    }

}
