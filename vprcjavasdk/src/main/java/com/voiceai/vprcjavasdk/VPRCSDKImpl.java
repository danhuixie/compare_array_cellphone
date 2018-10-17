package com.voiceai.vprcjavasdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VPRCSDKImpl implements VPRCSDK {
    private final VPRCNet vprcNet;
    private String access_token;
    private Context context;
    private String group_id;

    private List<CloudeUser> users;

    private Config config;

   public VPRCSDKImpl(VPRCNet vprcNet) {
        this.vprcNet = vprcNet;
    }

    @Override
    public List<CloudeUser> getUsers() {
        return users;
    }

    @Override
    public void VPRCSdkLogin(String var1, int var2, String var3, String var4, Response var5) {
        vprcNet.VPRCSdkLogin(var1, var2, var3, var4, var5);
    }

    @Override
    public void VPRCRefreshToke(String var1, String var2, Response var3) {
        vprcNet.VPRCRefreshToke(var1, var2, var3);
    }

    @Override
    public void VPRCSdkGroupAdd(String var1, String var2, Response var3) {
        vprcNet.VPRCSdkGroupAdd(var1, var2, var3);
    }

    @Override
    public void VPRCSdkGroupGet(String var1, Response var2) {
        vprcNet.VPRCSdkGroupGet(var1, var2);
    }

    @Override
    public void VPRCSdkGroupSearch(String var1, Response var2) {
        vprcNet.VPRCSdkGroupSearch(var1, var2);
    }

    @Override
    public void VPRCSdkGroupRemove(List<String> var1, Response var2) {
        vprcNet.VPRCSdkGroupRemove(var1, var2);
    }

    @Override
    public void VPRCSdkClientGet(String var1, String var2, Response var3) {
        vprcNet.VPRCSdkClientGet(var1, var2, var3);
    }

    @Override
    public void VPRCSdkClientSearch(String var1, String var2, Response var3) {
        vprcNet.VPRCSdkClientSearch(var1, var2, var3);
    }

    @Override
    public void VPRCSdkClientAdd(String var1, String var2, String var3, Response var4) {
        vprcNet.VPRCSdkClientAdd(var1, var2, var3, var4);
    }

    @Override
    public void VPRCSdkClientRemove(List<String> var1, String var2, Response var3) {
        vprcNet.VPRCSdkClientRemove(var1, var2, var3);
    }

    @Override
    public void VPRCSdkVoiceprintUpload(File var1, Response var2) {
        vprcNet.VPRCSdkVoiceprintUpload(var1, var2);
    }

    @Override
    public void VPRCSdkTrainingTextGet(String var1, int var2, Response var3) {
        vprcNet.VPRCSdkTrainingTextGet(var1, var2, var3);
    }

    @Override
    public void VPRCSdkVoiceprintRegister(String var1, String var2, int var3, String var4, List<String> var5, Response var6) {
        vprcNet.VPRCSdkVoiceprintRegister(var1, var2, var3, var4, var5, var6);
    }

    @Override
    public void VPRCSdkVoiceprintVerify(String var1, String var2, int var3, String var4, List<String> var5, int var6, Response var7) {
        vprcNet.VPRCSdkVoiceprintVerify(var1, var2, var3, var4, var5, var6, var7);
    }

    @Override
    public void VPRCSdkLogout(Response response) {
        vprcNet.VPRCSdkLogout(response);
    }

    @Override
    public void VPRCSdkAnaly(String clientId, String groupId, int src_sample_rate, String modeltype, List<String> fileidlist, Response response) {
        vprcNet.VPRCSdkAnaly(clientId, groupId, src_sample_rate, modeltype, fileidlist, response);
    }

    @Override
    public void VPRCSdkAsvCheck(String clientId, String groupId, int src_sample_rate, String modeltype, List<String> fileidlist, Response response) {
        vprcNet.VPRCSdkAsvCheck(clientId, groupId, src_sample_rate, modeltype, fileidlist, response);
    }

    @Override
    public boolean isValidateRegisterMaster() {
        if (users != null) {
            for (CloudeUser user : users) {
                if (MASTER.equalsIgnoreCase(user.getClient_name())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValidateUsername(String username) {
        if (users != null) {
            for (CloudeUser user : users) {
                if (user.getClient_name().equalsIgnoreCase(username)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void analygender(int src_sample_rate, List<String> list, Response response) {
        vprcNet.VPRCSdkAnaly(getMasterId(), group_id, src_sample_rate, config.getModeltype(), list, (res, e) -> {
            if (e == null) {
                try {
                    Gson gson = new Gson();
                    VoiceCloudRegisterInfo re = gson.fromJson(res, VoiceCloudRegisterInfo.class);
                    if (response != null) {
                        if (Config.productmode) {
                            response.response(res, e);
                        } else {
                            if (re.getFeature().getGender() >= 0.5) {
                                response.response("women", e);
                            } else {
                                response.response("men", e);
                            }
                        }
                    }
                } catch (Exception e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "analygender" + res + e.toString());
            }
        });
    }

    @Override
    public void analyhuoti(int src_sample_rate, List<String> list, Response response) {
        vprcNet.VPRCSdkAsvCheck(getMasterId(), group_id, src_sample_rate, config.getModeltype(), list, (res, e) -> {
            if (e == null) {
                try {
                    boolean bo = new JSONObject(res).getJSONObject("asv").getBoolean("flag");
                    if (response != null) {
                        if (Config.productmode) {
                            response.response(res, e);
                        } else {
                            if (bo) {
                                response.response("true", e);
                            } else {
                                response.response("false", e);
                            }
                        }
                    }
                } catch (Exception e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "analygender" + res + e.toString());
            }
        });
    }

    @Override
    public void registerMaster(int src_sample_rate, List<String> list, Response response) {
        regiseter(MASTER, src_sample_rate, list, response);
    }

    @Override
    public void regiseter(String username, int src_sample_rate, List<String> list, Response response) {
        vprcNet.VPRCSdkClientSearch(group_id, username, (res, e) -> {
            if (e == null) {
                try {
                    JSONArray p = new JSONArray(res);
                    if (p.length() > 0) {
                        String clientid = p.getJSONObject(0).getString("clientid");
                        continueliuchengUser(clientid, src_sample_rate, list, response);
                    } else {
                        toadduser(username, src_sample_rate, list, response);
                    }
                } catch (JSONException e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "regiseter" + res + e.toString());
            }

        });


    }

    private void toadduser(String username, int src_sample_rate, List<String> list, Response response) {
        vprcNet.VPRCSdkClientAdd(username, "来自星星的祝福，无bug的BUF加持", group_id, (res, e) -> {
            if (e == null) {
                try {
                    String client_id = new JSONObject(res).getString("clientid");
                    continueliuchengUser(client_id, src_sample_rate, list, response);
                } catch (JSONException e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "toadduser" + res + e.toString());
            }

        });
    }

    private void continueliuchengUser(String clientid, int src_sample_rate, List<String> list, Response response) {

        vprcNet.VPRCSdkVoiceprintRegister(clientid, group_id, src_sample_rate, config.getModeltype(), list, (res, e) -> {
            if (e == null) {
                try {
                    Gson gson = new Gson();
                    VoiceCloudRegisterInfo re = gson.fromJson(res, VoiceCloudRegisterInfo.class);
                    if (re.isResult()) {
                        continueliucheng(response);
                    } else {
                        if (response != null) {
                            response.response("", new NullPointerException(re.getMsg()));
                        }
                        removeuser(clientid, null);
                    }
                } catch (Exception e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                removeuser(clientid, null);
                Log.e("sdkutils", "error" + "continueliuchengUser" + res + e.toString());
            }

        });

    }

    @Override
    public void removeuser(String clientid, Response response) {
        vprcNet.VPRCSdkClientRemove(Arrays.asList(clientid), group_id, (res, e) -> {
            if (e == null) {
                try {
                    JSONArray l = new JSONObject(res).getJSONArray("succeed_list");
                    if (response != null) {
                        if (l.length() >= 0) {
                            continueliucheng(response);
                        } else {
                            continueliucheng(null);
                            response.response("false", new NullPointerException(""));
                        }
                    }

                } catch (JSONException e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "removeuser" + res + e.toString());
            }

        });
    }

    public static String getAccessToken(Context context) {
        @SuppressLint("WrongConstant") String ac = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).getString("access_token", "");
        @SuppressLint("WrongConstant") long time = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).getLong("expires_time", 0);
        if (time > System.currentTimeMillis()) return ac;
        return "";
    }

    public static void dealAccessToken(VPRCNet vprcNet, Config config, final Context context, String str) {
        if (context == null) {
            return;
        }
        @SuppressLint("WrongConstant") SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).edit();
        try {
            JSONObject re = new JSONObject(str);
            if (re.getString("access_token").length() > 2) {
                String accesstoken = re.getString("access_token");
                edit.putString("access_token", accesstoken);
                edit.commit();
                dealTime(vprcNet, config, context, getExpricesTime(str));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void dealTime(VPRCNet vprcNet, Config config, final Context context, long ti) {
        if (context == null) {
            return;
        }
        @SuppressLint("WrongConstant") SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).edit();
        edit.putLong("expires_time", System.currentTimeMillis() + ti);
        edit.commit();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                vprcNet.VPRCRefreshToke(config.getBaseurl(), -1, config.getAppId(), getAccessToken(context), (res, e) -> {
                    if (e == null)
                        dealTime(vprcNet, config, context, getExpricesTime(res));
                });
            }
        }, ti);
    }

    private static long getExpricesTime(String res) {
        JSONObject re = null;
        try {
            re = new JSONObject(res);
            long ti = re.getLong("expires");
            ti *= 1000;
            if (ti > 864000000) {
                ti = 864000000;
            }
            if (ti > 700) {
                ti -= 600;
            }
            return ti;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void init(Context context1, Config config1, Response response) {
        this.context = context1.getApplicationContext();
        if (config1 == null) {
            config = new Config(context);
        } else {
            config = config1;
        }
        if (getAccessToken(this.context).length() > 0) {
            vprcNet.VPRCRefreshToke(config.getBaseurl(), -1, config.getAppId(), getAccessToken(this.context), (res, e) -> {
                if (e == null) {
                    access_token = getAccessToken(this.context);
                    dealTime(vprcNet, config, this.context, getExpricesTime(res));
                    searchOrCreate(config.getGroup_name(), response);
                } else {
                    initlogin(response);
                }
            });
        } else {
            initlogin(response);
        }
    }

    @Override
    public Config getConfig() {
        return config;
    }

    private void initlogin(Response response) {
        vprcNet.VPRCSdkLogin(config.getBaseurl(), -1, config.getAppId(), config.getAppSecret(), (res, e) -> {
            if (e == null) {
                try {
                    access_token = new JSONObject(res).getString("access_token");
                    dealAccessToken(vprcNet, config, this.context, res);
                    searchOrCreate(config.getGroup_name(), response);
                } catch (Exception e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                Log.e("sdkutils", "error" + "init" + res + e.toString());
                if (response != null) response.response("", e);
            }
        });
    }

    private void searchOrCreate(String str, Response response) {
        vprcNet.VPRCSdkGroupSearch(str, (res, e) -> {
            if (e == null) {
                try {
                    Log.e("searchOrCreate", res);
                    JSONArray p = new JSONArray(res);
                    if (p.length() > 0) {
                        group_id = p.getJSONObject(0).getString("groupid");
                        continueliucheng(response);
                    } else {
                        tocreate(str, response);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Log.e("error禁止出错", e1.toString());
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "searchOrCreate" + res + e.toString());
            }


        });
    }

    private void tocreate(String str, Response response) {
        vprcNet.VPRCSdkGroupAdd(str, "VoiceAi android体验版 test 手机创建", (res, e) -> {
            if (e == null) {
                try {
                    group_id = new JSONObject(res).getString("groupid");
                    continueliucheng(response);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Log.e("error禁止出错", e1.toString());
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "tocreate" + res + e.toString());
            }
        });

    }

    @SuppressLint("WrongConstant")
    private void continueliucheng(Response response) {
        vprcNet.VPRCSdkClientGet(group_id, null, (res, e) -> {
            if (e == null) {
                try {
                    Gson gson = new Gson();
                    users = gson.fromJson(res, new TypeToken<List<CloudeUser>>() {
                    }.getType());
                    if (users != null && response != null) {
                        context.getSharedPreferences("init", Context.MODE_APPEND).edit().putString("groupname", config.getGroup_name()).commit();
                        if (response != null) {
                            response.response("true", null);
                        }
                    } else {
                        Log.e("error禁止出错", "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("error禁止出错", ex.toString());
                }
            } else {
                Log.e("sdkutils", "error" + "continueliucheng" + res + e.toString());
                if (response != null) if (response != null) response.response("", e);
            }
        });


    }

    @Override
    public void getText(String mode, Response response) {
        vprcNet.VPRCSdkTrainingTextGet(mode, 1, response);
    }

    @Override
    public void upload(File file, Response response, Progress progress) {
        vprcNet.VPRCSdkVoiceprintUpload(file, response, progress);
    }

    @Override
    public void verifyMaster(int src_sample_rate, List<String> list, Response response) {
        if (getMasterId() == null) {
            response.response("false", new NullPointerException("master not register"));
            return;
        }
        vprcNet.VPRCSdkVoiceprintVerify(getMasterId(), group_id, src_sample_rate, config.getModeltype(), list, 5, (res, e) -> {
            if (e == null) {
                try {
                    JSONArray array = null;
                    try {
                        System.out.println(res);
                        array = new JSONObject(res).getJSONArray("matching_list");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (response != null) {
                        if (Config.productmode) {
                            response.response(res, e);
                        } else {
                            if (array != null && array.length() > 0) {
                                response.response("true", null);
                            } else {
                                response.response("false", null);
                            }
                        }
                    }
                } catch (Exception e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "verifyAll" + res + e.toString());
            }

        });
    }

    private String getMasterId() {
        if (users != null) {
            for (CloudeUser user : users) {
                if (MASTER.equalsIgnoreCase(user.getClient_name())) {
                    return user.getClientid();
                }
            }
        }
        return null;
    }

    @Override
    public void verifyAll(int src_sample_rate, List<String> list, Response response) {
        vprcNet.VPRCSdkVoiceprintVerify(null, group_id, src_sample_rate, config.getModeltype(), list, 5, (res, e) -> {
            if (e == null) {
                try {
                    JSONArray array = null;
                    try {
                        array = new JSONObject(res).getJSONArray("matching_list");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (response != null) {
                        if (Config.productmode) {
                            response.response(res, e);
                        } else {
                            if (array != null && array.length() > 0) {
                                response.response((array.toString()), null);
                            } else {
                                response.response("[]", null);
                            }
                        }
                    }
                } catch (Exception e1) {
                    Log.e("error禁止出错", e1.toString());
                    e1.printStackTrace();
                }
            } else {
                if (response != null) response.response("", e);
                Log.e("sdkutils", "error" + "verifyAll" + res + e.toString());
            }

        });
    }

    @Override
    public VPRCNet getVPRCNet() {
        return vprcNet;
    }

}
