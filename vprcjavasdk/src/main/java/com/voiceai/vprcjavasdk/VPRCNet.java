package com.voiceai.vprcjavasdk;

import java.io.File;
import java.util.List;

public interface VPRCNet {
    MainHandle getHandler();

    String getApp_id();

    String getAccess_token();

    String getBaseu();

    /**
     * 登录接口 必须第一个调用。提供了host port appid appsecret accsess_token 等基础数据的封装
     *
     * @param host
     * @param port
     * @param appid
     * @param appsecret
     * @param response
     */
    void VPRCSdkLogin(String host, int port, String appid, String appsecret, Response response);
    /**
     * 登出接口
     *
     */
    void VPRCSdkLogout(Response response);

    void VPRCRefreshToke(String host, int port, String app_id, String access_token, Response response);

    /**
     * 刷新接口 ，可以不使用
     *
     * @param response
     */
    void VPRCRefreshToke(String app_id, String access_token, Response response);

    /**
     * @param groupName
     * @param describe  非必须
     * @param response
     */
    void VPRCSdkGroupAdd(String groupName, String describe, Response response);

    /**
     * 获取分组信息
     * 当前接口设计为主键作为唯一标识
     *
     * @param groupid
     * @param response
     */
    void VPRCSdkGroupGet(String groupid, Response response);

    /**
     * 查询分组信息
     *
     * @param groupname
     * @param response
     */
    void VPRCSdkGroupSearch(String groupname, Response response);

    /**
     * 批量删除组，id为唯一标识
     *
     * @param groupIds
     * @param response
     */
    void VPRCSdkGroupRemove(List<String> groupIds, Response response);

    /**
     * 获取分组下的用户信息,
     *
     * @param groupId
     * @param clientId 缺省为查询所有
     * @param response
     */
    void VPRCSdkClientGet(String groupId, String clientId, Response response);


    /**
     * 获取分组下的用户信息,
     *
     * @param groupId
     * @param clientname
     * @param response
     */
    void VPRCSdkClientSearch(String groupId, String clientname, Response response);

    /**
     * 在指定组中创建用户
     *
     * @param clientName
     * @param describe   非必须
     * @param groupId
     * @param response
     */
    void VPRCSdkClientAdd(String clientName, String describe, String groupId, Response response);

    /**
     * 批量删除用户，id为唯一标识
     *
     * @param clientIds
     * @param groupId
     * @param response
     */
    void VPRCSdkClientRemove(List<String> clientIds, String groupId, Response response);


    /**
     * 文件上传，上传后需要使用者自行保存fileid
     *
     * @param file
     * @param response
     */
    void VPRCSdkVoiceprintUpload(File file, Response response);

    void VPRCSdkVoiceprintUpload(File file, Response response, Progress progress);

    /**
     * 文本获取
     *
     * @param modeltype
     * @param modeltextcount
     * @param response
     */
    void VPRCSdkTrainingTextGet(String modeltype, int modeltextcount, Response response);

    /**
     * 注册声纹接口，需要提交用户组id，用户id，设备采样率，模型名称，文件id列表等信息
     *
     * @param clientId
     * @param groupId
     * @param src_sample_rate
     * @param modeltype
     * @param fileidlist
     * @param response
     */
    void VPRCSdkVoiceprintRegister(String clientId, String groupId, int src_sample_rate
            , String modeltype, List<String> fileidlist, Response response);

    void VPRCSdkAnaly(String clientId, String groupId, int src_sample_rate
            , String modeltype, List<String> fileidlist, Response response);

    void VPRCSdkAsvCheck(String clientId, String groupId, int src_sample_rate
            , String modeltype, List<String> fileidlist, Response response);

    /**
     * 验证声纹的接口，支持多文件验证
     *
     * @param clientId
     * @param groupId
     * @param src_sample_rate
     * @param modeltype
     * @param fileidlist
     * @param limitCount//    返回验证后相似结果集合的数目上限
     * @param response
     */
    void VPRCSdkVoiceprintVerify(String clientId, String groupId, int src_sample_rate, String modeltype,
                                 List<String> fileidlist, int limitCount, Response response);
}
