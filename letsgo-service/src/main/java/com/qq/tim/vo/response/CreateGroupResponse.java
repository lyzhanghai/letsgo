package com.qq.tim.vo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2016/4/29.
 */
public class CreateGroupResponse extends CommonResponse {
    @JsonProperty(value = "GroupId")
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "{"
                + "                        \"groupId\":\"" + groupId + "\""
                + "}";
    }
}
