package com.redridgeapps.pagingdemo.model;

import com.squareup.moshi.Json;

public class License {

    @Json(name = "key")
    private String key;

    @Json(name = "name")
    private String name;

    @Json(name = "spdx_id")
    private String spdxId;

    @Json(name = "url")
    private String url;

    @Json(name = "node_id")
    private String nodeId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpdxId() {
        return spdxId;
    }

    public void setSpdxId(String spdxId) {
        this.spdxId = spdxId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

}
