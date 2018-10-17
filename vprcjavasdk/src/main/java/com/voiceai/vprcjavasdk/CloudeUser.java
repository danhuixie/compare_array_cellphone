package com.voiceai.vprcjavasdk;

public class CloudeUser {

    /**
     * clientid : 4032bddd41ed42378762ec54bc1652aa
     * client_name : clienttest
     * describe : des of clienttest for android
     */

    private String clientid;
    private String client_name;
    private String describe;
    private String create_time;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
