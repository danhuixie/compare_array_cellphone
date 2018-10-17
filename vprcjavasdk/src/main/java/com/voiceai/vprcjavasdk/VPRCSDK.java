package com.voiceai.vprcjavasdk;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.util.List;


/**
 * 改 sdk主要方向为1，处理刷新token的问题
 * 2，依照groupname 自动加载所有注册用户
 * 3，注册用户在注册声纹时自动添加，即所有用户都是声纹注册的用户
 * 4，自动填充Master用户名
 */
public interface VPRCSDK {

    List<CloudeUser> getUsers();

    boolean isValidateRegisterMaster();

    boolean isValidateUsername(String username);

    void analygender(int src_sample_rate, List<String> list, Response response);

    void analyhuoti(int src_sample_rate, List<String> list, Response response);

    void registerMaster(int src_sample_rate, List<String> list, Response response);

    void regiseter(String username, int src_sample_rate, List<String> list, Response response);

    void removeuser(String clientid, Response response);

    void init(Context context1, Config config1, Response response);
    Config getConfig();

    void getText(String mode, Response response);

    void upload(File file, Response response, Progress progress);

    void verifyMaster(int src_sample_rate, List<String> list, Response response);

    void verifyAll(int src_sample_rate, List<String> list, Response response);

    VPRCNet getVPRCNet();

    public static final String MODEL_ASR_NUMBER = "model_asr_number";// 	数字模型	1
    public static final String MODEL_ASR_POWER = "model_asr_power";//  model_asr_power	电力模型	1
    public static final String MODEL_COMMON_SHORT = "model_common_short";// model_common_short	测试分离模型	0
    public static final String MODEL_LONG = "model_long";//  model_long	长句模型	1
    public static final String MODEL_LONG_16K = "model_long_16k";//  model_long_16k	长句模型16k	1
    public static final String MODEL_NUMBER = "model_number";//  model_number	数字模型	1
    public static final String MODEL_SHORT_CN_DNN = "model_short_cn_dnn";//  model_short_cn_dnn	短语模型	1
    public static final String MASTER = "Master";


    //--------------------------以下接口为向前兼容接口


    void VPRCSdkLogin(String var1, int var2, String var3, String var4, Response var5);

    void VPRCRefreshToke(String var1, String var2, Response var3);

    void VPRCSdkGroupAdd(String var1, String var2, Response var3);

    void VPRCSdkGroupGet(String var1, Response var2);

    void VPRCSdkGroupSearch(String var1, Response var2);

    void VPRCSdkGroupRemove(List<String> var1, Response var2);

    void VPRCSdkClientGet(String var1, String var2, Response var3);

    void VPRCSdkClientSearch(String var1, String var2, Response var3);

    void VPRCSdkClientAdd(String var1, String var2, String var3, Response var4);

    void VPRCSdkClientRemove(List<String> var1, String var2, Response var3);

    void VPRCSdkVoiceprintUpload(File var1, Response var2);

    void VPRCSdkTrainingTextGet(String var1, int var2, Response var3);

    void VPRCSdkVoiceprintRegister(String var1, String var2, int var3, String var4, List<String> var5, Response var6);

    void VPRCSdkVoiceprintVerify(String var1, String var2, int var3, String var4, List<String> var5, int var6, Response var7);

    void VPRCSdkLogout(Response response);

    void VPRCSdkAnaly(String clientId, String groupId, int src_sample_rate
            , String modeltype, List<String> fileidlist, Response response);

    void VPRCSdkAsvCheck(String clientId, String groupId, int src_sample_rate
            , String modeltype, List<String> fileidlist, Response response);
}
