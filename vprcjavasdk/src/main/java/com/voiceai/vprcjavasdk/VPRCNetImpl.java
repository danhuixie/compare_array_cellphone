package com.voiceai.vprcjavasdk;

import android.os.Looper;
import android.util.Log;

import com.voiceai.utils.NetUtils;
import com.voiceai.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.voiceai.vprcjavasdk.Config.ASV_CHECK;

/**
 * 简单单例，在构造时初始化，当前实现从新线程切换到主线程
 */
public class VPRCNetImpl implements VPRCNet {

    MainHandle handler;
    private String app_id;
    private String access_token;
    private String baseu;

    @Override
    public MainHandle getHandler() {
        return handler;
    }

    @Override
    public String getApp_id() {
        return app_id;
    }

    @Override
    public String getAccess_token() {
        return access_token;
    }

    @Override
    public String getBaseu() {
        return baseu;
    }

    public VPRCNetImpl() {
        init();
    }


    public VPRCNetImpl init() {
        try {
            handler = new MainHandle(Looper.getMainLooper());
        } catch (Throwable e) {
            handler = new MainHandle();
        }

        return this;
    }

    @Override
    public void VPRCSdkLogin(String host, int port, final String appid, final String appsecret, final Response response) {
        String baseurl = "";
        if (port <= 0) {
            port = 443;
        }
        if (host.toLowerCase().contains("http")) {
            baseurl = host;
        } else {
            baseurl = "https://" + host + ":" + port;
        }
        baseu = baseurl;
        final String url = baseu + "/api/app/auth/get";
        final JSONObject map = new JSONObject();
        try {
            map.put("appid", appid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                try {
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRC nonce", re);
                    boolean firstre = new JSONObject(re).getBoolean("flag");
                    if(!firstre){
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    JSONObject jsonObject = new JSONObject(re).getJSONObject("data");
                    if (jsonObject == null) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    List<String> li = new ArrayList<>();
                    app_id = jsonObject.getString("appid");
                    String appid = jsonObject.getString("appid");
                    String nonce = jsonObject.getString("nonce");
                    long timestamp = jsonObject.getLong("timestamp");
                    li.add(appid);
                    li.add(nonce);
                    li.add(timestamp + "");
                    li.add(appsecret);
                    Collections.sort(li);
                    StringBuilder k = new StringBuilder();
                    for (String s : li) {
                        k.append(s);
                    }
                    System.out.println(k.toString());
                    String signature = MD5.getSHA256StrJava(k.toString());
                    JSONObject map1 = new JSONObject();
                    map1.put("appid", appid);
                    map1.put("signature", signature);
                    map1.put("nonce", nonce);
                    map1.put("timestamp", timestamp);
                    String url = baseu + "/api/app/auth/token/get";
                    re = NetUtils.httpsRequest(url, "POST", map1, "");
                    System.out.println("" + re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    JSONObject jsonObject1 = new JSONObject(re).getJSONObject("data");
                    re = jsonObject1.toString();
                    access_token = jsonObject1.getString("access_token");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCRefreshToke(String host, int port, final String app_id, final String access_token, final Response response) {
        String baseurl = "";
        if (port <= 0) {
            port = 443;
        }
        if (host.toLowerCase().contains("http")) {
            baseurl = host;
        } else {
            baseurl = "https://" + host + ":" + port;
        }
        baseu = baseurl;
        this.app_id = app_id;
        this.access_token = access_token;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/auth/token/refresh";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });
            }
        }).start();
    }

    @Override
    public void VPRCRefreshToke(final String app_id, final String access_token, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/auth/token/refresh";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });
            }
        }).start();
    }

    @Override
    public void VPRCSdkGroupAdd(final String groupName, final String describe, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/group/create";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("group_name", groupName);
                    map.put("describe", describe);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });
            }
        }).start();
    }

    @Override
    public void VPRCSdkGroupSearch(final String groupname, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/group/get";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("group_name", groupname);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkGroupGet(final String groupid, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/group/get";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("groupid", groupid);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkGroupRemove(final List<String> groupIds, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/group/delete";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    JSONArray a = new JSONArray();
                    for (String groupId : groupIds) {
                        JSONObject b = new JSONObject();
                        b.put("groupid", groupId);
                        a.put(b);
                    }
                    map.put("group_list", a);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkClientSearch(final String groupId, final String clientname, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/client/get";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    map.put("client_name", clientname);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkClientGet(final String groupId, final String clientId, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/client/get";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    if (clientId != null) {
                        map.put("clientid", clientId);
                    }
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkClientAdd(final String clientName, final String describe, final String groupId, final Response response) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/client/create";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    map.put("client_name", clientName);
                    map.put("describe", describe);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();

    }

    @Override
    public void VPRCSdkClientRemove(final List<String> clientIds, final String groupId, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/client/delete";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    JSONArray a = new JSONArray();
                    for (String client_id : clientIds) {
                        JSONObject b = new JSONObject();
                        b.put("clientid", client_id);
                        a.put(b);
                    }
                    map.put("client_list", a);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkLogout(Response response) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/auth/token/remove";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });
            }
        }).start();

    }

    @Override
    public void VPRCSdkVoiceprintUpload(final File file, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/voiceprint/upload";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    Map<String, Object> map = new HashMap<>();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("file1", file);

                    re = NetUtils.httpsFileRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkVoiceprintUpload(final File file, final Response response, final Progress progress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/voiceprint/upload";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    Map<String, Object> map = new HashMap<>();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("file1", file);

                    re = NetUtils.httpsFileRequest2(url, "POST", map, "", (i) -> handler.post(() -> progress.notify(i)));
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkTrainingTextGet(final String modeltype, final int modeltextcount, final Response response) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/training/text/get";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("model_type", modeltype);
                    map.put("mode_text_count", modeltextcount);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkVoiceprintRegister(final String clientId, final String groupId, final int src_sample_rate
            , final String modeltype, final List<String> fileidlist, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/voiceprint/register";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("asv_check", ASV_CHECK);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    map.put("clientid", clientId);
                    map.put("src_sample_rate", src_sample_rate);
                    map.put("model_type", modeltype);
                    JSONArray a = new JSONArray();
                    for (String fileid : fileidlist) {
                        a.put(fileid);
                    }
                    map.put("filelist", a);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkAnaly(final String clientId, final String groupId, final int src_sample_rate
            , final String modeltype, final List<String> fileidlist, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/voiceprint/analyze";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    map.put("clientid", clientId);
                    map.put("src_sample_rate", src_sample_rate);
                    map.put("model_type", modeltype);
                    JSONArray a = new JSONArray();
                    for (String fileid : fileidlist) {
                        a.put(fileid);
                    }
                    map.put("filelist", a);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    err = MThrowable.create(j);
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkAsvCheck(String clientId, String groupId, int src_sample_rate, String modeltype, List<String> fileidlist, Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/voiceprint/asvcheck";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("asv_check", true);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    if (clientId != null) {
                        map.put("clientid", clientId);
                    }
                    map.put("src_sample_rate", src_sample_rate);
                    map.put("model_type", modeltype);
                    JSONArray a = new JSONArray();
                    for (String fileid : fileidlist) {
                        a.put(fileid);
                    }
                    map.put("filelist", a);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }

    @Override
    public void VPRCSdkVoiceprintVerify(final String clientId, final String groupId, final int src_sample_rate, final String modeltype,
                                        final List<String> fileidlist, final int limitCount, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String re = null;
                MThrowable err = null;
                String url = baseu + "/api/app/voiceprint/verify";
                try {
                    if (StringUtils.isEmpty(app_id) || StringUtils.isEmpty(access_token))
                        throw new NullPointerException("appid or access_token is null");
                    JSONObject map = new JSONObject();
                    map.put("appid", app_id);
                    map.put("asv_check", ASV_CHECK);
                    map.put("access_token", access_token);
                    map.put("groupid", groupId);
                    if (clientId != null) {
                        map.put("clientid", clientId);
                    }
                    map.put("src_sample_rate", src_sample_rate);
                    map.put("model_type", modeltype);
                    JSONArray a = new JSONArray();
                    for (String fileid : fileidlist) {
                        a.put(fileid);
                    }
                    map.put("filelist", a);
                    map.put("limit_count", limitCount);
                    re = NetUtils.httpsRequest(url, "POST", map, "");
                    Log.i("VPRCsdk", re);
                    Boolean res = new JSONObject(re).getBoolean("flag");
                    if (!res) {
                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                    }
                    re = new JSONObject(re).getString("data");
                } catch (MThrowable e) {
                    err = e;
                } catch (Exception j) {
                    j.printStackTrace();
                }
                if (handler == null) {
                    init();
                }
                String finalRe = re;
                MThrowable finalErr = err;
                handler.post(() -> {
                    if (response != null) response.response(finalRe, finalErr);
                });

            }
        }).start();
    }


}
