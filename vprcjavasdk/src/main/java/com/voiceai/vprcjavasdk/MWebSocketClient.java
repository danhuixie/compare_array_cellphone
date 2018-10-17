package com.voiceai.vprcjavasdk;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MWebSocketClient implements ASRSDK {
    private Handler handler;
    private WebSocketClient mSocketClient;
    private String modeltype;
    int sample_rate;
    private String U = "/api/app/asr/streaming";
    private Response crr;
    private boolean closeable;
    private Listener listener;
    private boolean isrunnig;


    public MWebSocketClient() {
        try {
            handler = new Handler(Looper.getMainLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VPRCSDK vprcsdk;

    private byte[] save;

    @Override
    public void onDestory() {
        closeable = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mSocketClient != null) {
            try {
                mSocketClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocketClient = null;
        }
    }

    //socket检测函数，0，为初始状态，4为关闭状态，1，为运行状态，2，为异常断掉，需要重连的状态,或者正在重连的状态
    private int checkSocket() {
        if (closeable) {
            return 4;
        } else if (listener == null) return 0;
        else if (mSocketClient.getConnection().isOpen()) return 1;
        else return 2;
    }

    Response starreponse;

    @Override
    public synchronized void start(VPRCSDK vprcsdk, String modeltype, int src_sample_rate, Response reresponse) {
        if (vprcsdk.getVPRCNet().getAccess_token() == null || vprcsdk.getVPRCNet().getAccess_token().length() == 0)
            return;
        this.vprcsdk = vprcsdk;
        this.modeltype = modeltype;
        closeable = false;
        sample_rate = src_sample_rate;
        isrunnig = true;
        if (reresponse != null) {
            this.starreponse = reresponse;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSocketClient != null) {
                        try {
                            mSocketClient.close();
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }
                    }
                    mSocketClient = new WebSocketClient(new URI(ASRSDK.getWebUrl(vprcsdk.getVPRCNet().getBaseu() + U)), new Draft_17()) {

                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            Log.e("asr", "start");
                            startsend(sample_rate);
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.e("asr ori str ", message);
                            socketnomal = true;
                            handler.post(() -> {
                                String re = message;
                                Throwable err = null;
                                try {
                                    Boolean res = new JSONObject(re).getBoolean("flag");
                                    if (!res) {
                                        throw MThrowable.create(new JSONObject(re).getJSONObject("error"));
                                    }
                                    re = new JSONObject(re).getString("data");
                                    timer = new MTimer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            try {
                                                Log.e("wwww", "心跳");
                                                mSocketClient.send("VPRC".getBytes());
                                            } catch (Exception e) {
                                                timer.i++;
                                                if (timer.i == 5) {
                                                    timer.cancel();
                                                    timer = null;
                                                    recoverSocket();
                                                }
                                            }

                                        }
                                    }, 2000, 2000);
                                } catch (MThrowable e) {
                                    err = e;
                                } catch (Exception j) {
                                    err = MThrowable.create(j);
                                    j.printStackTrace();
                                }
                                if (starreponse != null) {
                                    if (err != null) {
                                        Log.e("asr", "" + err.getMessage());
                                    }
                                    starreponse.response(re, err);
                                }
                            });
                        }

                        @Override
                        public void run() {
                            try {
                                super.run();
                            } catch (Exception e) {
                                recoverSocket();
                                Log.e("socketinit_error", "unknow error");
                            }
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            Log.e("socketinit_close", reason);
                        }

                        @Override
                        public void onMessage(ByteBuffer bytes) {
                            super.onMessage(bytes);
                            for (byte[] bytes1 : chaifen(bytes.array())) {
                                onebyte(bytes1);
                            }
                        }

                        @Override
                        public void onError(Exception ex) {
                            ex.printStackTrace();
                            Log.e("socketinit", ex.toString());
                            if (!(ex instanceof NumberFormatException)) {
                                handler.post(() -> {
                                    if (starreponse != null) {
                                        if (ex != null) {
                                            Log.e("asr", "" + ex.getMessage());
                                        }
                                        starreponse.response("", MThrowable.create(ex, "0", ex.toString()));
                                    }
                                });
                                recoverSocket();
                            }
                        }
                    };

                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }

                        public void checkClientTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }
                    }};
                    SSLContext sc = null;
                    sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCerts, new SecureRandom());
                    // Otherwise the line below is all that is needed.
                    // sc.init(null, null, null);
                    mSocketClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
                    mSocketClient.connect();
                    isrunnig = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    static List<byte[]> chaifen(byte[] bytes) {
        List<byte[]> re = new ArrayList<>();
        int star = 0;
        int end = 0;
        if (bytes.length > 43) {
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 'C' && bytes[i + 1] == 'R' && bytes[i + 2] == 'P' && bytes[i + 3] == 'V') {
                    star = i;
                    byte[] u8a_number = new byte[4];
                    System.arraycopy(bytes, star + 22, u8a_number, 0, 4);
                    int numb = ((u8a_number[0] & 0xFF) |
                            ((u8a_number[1] & 0xFF) << 8) |
                            ((u8a_number[2] & 0xFF) << 16) |
                            ((u8a_number[3] & 0xFF) << 24));
                    end = star + 43 + numb;
                    byte[] dest = new byte[end - star];
                    System.arraycopy(bytes, star, dest, 0, dest.length);
                    re.add(dest);
                    i = end - 1;
                }
            }
        }
        return re;
    }

    void onebyte(byte[] or) {

        byte[] u8a_offset = new byte[4];
        System.arraycopy(or, 18, u8a_offset, 0, 4);

        int offset = ((u8a_offset[0] & 0xFF) |
                ((u8a_offset[1] & 0xFF) << 8) |
                ((u8a_offset[2] & 0xFF) << 16) |
                ((u8a_offset[3] & 0xFF) << 24));
        byte[] u8a_number = new byte[4];
        System.arraycopy(or, 22, u8a_number, 0, 4);
        int numb = ((u8a_number[0] & 0xFF) |
                ((u8a_number[1] & 0xFF) << 8) |
                ((u8a_number[2] & 0xFF) << 16) |
                ((u8a_number[3] & 0xFF) << 24));
        int isfinal = or[26];
        System.out.println("offset:" + offset + " number:" + numb + " isfinal:" + isfinal);

        byte[] sss = new byte[or.length - 43];
        System.arraycopy(or, 43, sss, 0, sss.length);
        if (numb > 0) {
            if (save != null && offset > 0 && offset < save.length) {
                byte[] savt = new byte[offset];
                System.arraycopy(save, 0, savt, 0, savt.length);
                save = new byte[savt.length + sss.length];
                System.arraycopy(savt, 0, save, 0, savt.length);
                System.arraycopy(sss, 0, save, savt.length, sss.length);
            } else {
                save = sss;
            }
        } else {
            save = null;
        }
        if (save != null) {
            if (crr != null) {
                String r = new String(Arrays.copyOf(save, save.length));
                Log.e("asr str", "" + (r));
                handler.post(() -> {
                    crr.response(isfinal + "|" + r, null);
                });
                if (isfinal == 1) {
                    save = null;
                }
            }
        }
    }

    private void startsend(int rate) {
        JSONObject a = new JSONObject();
        try {
            a.put("appid", vprcsdk.getVPRCNet().getApp_id());
            a.put("access_token", vprcsdk.getVPRCNet().getAccess_token());
            a.put("clientid", "");
            a.put("sample_rate", rate);
            a.put("model_type", modeltype);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mSocketClient.send(a.toString());
        } catch (WebsocketNotConnectedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSendByteListener(Listener listener) {
        this.listener = listener;
        crr = new Response() {
            @Override
            public void response(String res, Throwable e) {
                if (res != null && res.length() > 1) {
                    int dex = res.indexOf("|");
                    if ("|".equals(String.valueOf(res.charAt(1)))) {
                        if (e != null) {
                            Log.e("asr", "" + e.getMessage());
                        }
                        if (listener != null) {
                            listener.accept(res.substring(2), "1".equals(res.substring(0, 1)), e);
                        }

                    }
                }
            }
        };
    }

    public void recoverSocket() {
        Log.e("ASR 断线重连", "不稳定的ASR" + checkSocket() + isrunnig);
        if (checkSocket() == 2) {
            if (!isrunnig) {
                try {
                    onDestory();
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                            start(vprcsdk, modeltype, sample_rate, null);
                        } catch (Exception e) {
                        }
                    }).start();

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    boolean socketnomal = false;

    @Override
    public void send(byte[] bytes) {
        if (!socketnomal) return;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        byte[] re = new byte[32 + bytes.length];
        System.arraycopy(ASRSDK.head(false, bytes.length), 0, re, 0, 32);
        System.arraycopy(bytes, 0, re, 32, bytes.length);
        try {
            mSocketClient.send(re);
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(() -> {
                if (e != null) {
                    Log.e("asr", "" + e.getMessage());
                }
                if (listener != null)
                    listener.accept("", false, MThrowable.create(new NullPointerException("error  ------recovering")));
            });
            if (!socketnomal) return;
            socketnomal = false;
            recoverSocket();

        }
    }

    MTimer timer;

    static class MTimer extends Timer {
        int i = 0;
    }

    @Override
    public void sendfinal() {
        try {
            if (!socketnomal) return;
            System.out.println("isfinal:" + 1);
            if (timer != null) {
                timer.cancel();
            }
            new Thread(() -> {
                try {
                    mSocketClient.send(ASRSDK.head(true, 0));
                    timer = new MTimer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                Log.e("wwww", "心跳");
                                mSocketClient.send("VPRC".getBytes());
                            } catch (Exception e) {
                                timer.i++;
                                if (timer.i == 5) {
                                    timer.cancel();
                                    timer = null;
                                    recoverSocket();
                                }
                            }
                        }
                    }, 3000, 3000);
                } catch (Exception e) {
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
