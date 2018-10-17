package com.voiceai.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.voiceai.vprcjavasdk.Config;
import com.voiceai.vprcjavasdk.Progress;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 网络相关辅助类 NetUtils<br>
 * 提供网络请求（在android中使用时请自行开辟线程），网络状态，和物理 ip地址等，
 *
 * @author 0.0
 */
public class NetUtils {
    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

//    /**
//     * 判断网络是否连接
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isConnected(Context context) {
//
//        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (null != connectivity) {
//
//            NetworkInfo info = connectivity.getActiveNetworkInfo();
//            if (null != info && info.isConnected()) {
//                if (info.getState() == NetworkInfo.State.CONNECTED) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    /**
//     * 判断是否是wifi连接
//     */
//    public static boolean isWifi(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (cm == null)
//            return false;
//        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
//
//    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
    private static X509TrustManager xtm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };
    private static X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};

    /**
     * 原生https请求
     *
     * @param path
     * @param params
     * @return
     */
    public static String sendPOSTRequestForInputStream(String path, Map<String, String> params) {
        try {
            // 1> 组拼实体数据
            // method=save&name=liming&timelength=100
            StringBuilder entityBuilder = new StringBuilder("");
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    entityBuilder.append(entry.getKey()).append('=');
                    entityBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    entityBuilder.append('&');
                }
                entityBuilder.deleteCharAt(entityBuilder.length() - 1);
            }
            byte[] entity = entityBuilder.toString().getBytes("UTF-8");
            URL url = new URL(path);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                // Trust all certificates
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(new KeyManager[0], xtmArray, new SecureRandom());
                SSLSocketFactory socketFactory = context.getSocketFactory();
                ((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);
                ((HttpsURLConnection) conn).setHostnameVerifier(HOSTNAME_VERIFIER);
            }
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);// 允许输出数据
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
            conn.setRequestProperty("Content-Encoding", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setRequestProperty("Charset", "UTF-8");

            OutputStream outStream = conn.getOutputStream();
            outStream.write(entity);
            outStream.flush();
            outStream.close();
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder buffer = new StringBuilder();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {

                buffer.append(str);
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("支付网络底层错误", "请务必及早修改");
        }
        return null;
    }

    /**
     * 原生http请求 请使用utf-8 编码
     *
     * @param path
     * @param params
     * @return
     */
    public static String sendPOSTRequestForInputStream2(String path, Map<String, String> params) {
        try {

            // 1> 组拼实体数据
            // method=save&name=liming&timelength=100
            StringBuilder entityBuilder = new StringBuilder("");
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    entityBuilder.append(entry.getKey()).append('=');
                    entityBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    entityBuilder.append('&');
                }
                entityBuilder.deleteCharAt(entityBuilder.length() - 1);
            }
            byte[] entity = entityBuilder.toString().getBytes("UTF-8");
            URL url = new URL(path + "?" + entityBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // if (conn instanceof HttpsURLConnection) {
            // // Trust all certificates
            // SSLContext context = SSLContext.getInstance("TLS");
            // context.init(new KeyManager[0], xtmArray, new SecureRandom());
            // SSLSocketFactory socketFactory = context.getSocketFactory();
            // ((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);
            // ((HttpsURLConnection)
            // conn).setHostnameVerifier(HOSTNAME_VERIFIER);
            // }
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
            conn.setRequestProperty("Content-Encoding", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.connect();
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder buffer = new StringBuilder();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {

                buffer.append(str);
            }
            return new JSONObject(buffer.toString()).getString("result");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("支付网络底层错误", "请务必及早修改");
        }
        return null;
    }

    /**
     * 获取本机ip，采用双重循环，但是不能再用户开启热点共享时正常返回
     *
     * @return
     */
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces(); enNetI
                    .hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static String getWifiaddress(Context context) {
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        // 判断wifi是否开启
//        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
//        }
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        String ip = intToIp(ipAddress);
//        return ip;
//    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    /**
     * 发送https请求<br>
     * String kString = getRequestXml(NetParamsUtils.dealJson2TM(kk));<br>
     * String kky = NetUtils.httpsRequest(URL4, "POST", kString);
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param param         提交的数据
     * @return 返回微信服务器响应的信息
     */
    @SuppressLint("TrulyRandom")
    public static String httpsRequest(String requestUrl, String requestMethod, Map<String, Object> param, String charset) {
        System.out.println(requestUrl);
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tm, new SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
            StringBuilder stringBuilder = new StringBuilder();
            if (param != null && !param.isEmpty()) {
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    try {
                        stringBuilder
                                .append(entry.getKey())
                                .append("=")
                                .append(URLEncoder.encode(entry.getValue().toString(), charset))
                                .append("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            String outputStr = stringBuilder.toString();
            System.out.println(outputStr);
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes(charset));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送https请求<br>
     * String kString = getRequestXml(NetParamsUtils.dealJson2TM(kk));<br>
     * String kky = NetUtils.httpsRequest(URL4, "POST", kString);
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     *                      *            提交的数据
     * @return 返回微信服务器响应的信息
     */
    @SuppressLint("TrulyRandom")
    public static String httpsRequest(String requestUrl, String requestMethod, JSONObject json, String charset) throws Exception {
        System.out.println(requestUrl);
        if (!requestUrl.contains("https"))
            return httpRequest(requestUrl, requestMethod, json.toString());
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, tm, new SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        conn.setSSLSocketFactory(ssf);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(Config.CONNECT_TIMEOUT);
        conn.setReadTimeout(Config.READ_TIMEOUT);
        // 设置请求方式（GET/POST）
        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
        String outputStr = json.toString();
        System.out.println(outputStr);
        // 当outputStr不为null时向输出流写数据
        if (null != outputStr) {
            OutputStream outputStream = conn.getOutputStream();
            // 注意编码格式
            outputStream.write(outputStr.getBytes(charset));
            outputStream.close();
        }
        // 从输入流读取返回内容
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        // 释放资源
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        conn.disconnect();
        return buffer.toString();
    }

    /**
     * 原生发送http请求<br>
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return 返回微信服务器响应的信息
     */
    @SuppressLint("TrulyRandom")
    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        conn.setRequestMethod(requestMethod);
        conn.setConnectTimeout(Config.CONNECT_TIMEOUT);
        conn.setReadTimeout(Config.READ_TIMEOUT);
        conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
        conn.connect();
        // 当outputStr不为null时向输出流写数据
        if (!StringUtils.isEmpty(outputStr)) {
            OutputStream outputStream = conn.getOutputStream();
            // 注意编码格式
            outputStream.write(outputStr.getBytes("UTF-8"));
            outputStream.close();
        }
        // 从输入流读取返回内容
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        // 释放资源
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        conn.disconnect();
        return buffer.toString();
    }

    public static String httpsFileRequest(String requestUrl, String requestMethod, Map<String, Object> param, String charset) throws Exception {
        System.out.println(requestUrl);
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符

        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, tm, new SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        conn.setSSLSocketFactory(ssf);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        conn.setRequestMethod(requestMethod);

        conn.setRequestProperty("user-agent", "PostmanRuntime/7.1.5");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//            conn.setRequestProperty("content-length", "146425");
        conn.setRequestProperty("Connection", "keep-alive");
        OutputStream outputStream = conn.getOutputStream();
//            StringBuilder stringBuilderl = new StringBuilder();
//            if (param != null && !param.isEmpty()) {
//                for (Map.Entry<String, Object> entry : param.entrySet()) {
//                    try {
//                        if(entry.getValue() instanceof String)
//                        stringBuilderl
//                                .append(entry.getKey())
//                                .append("=")
//                                .append(URLEncoder.encode(entry.getValue().toString(), charset))
//                                .append("&");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            stringBuilderl.deleteCharAt(stringBuilderl.length() - 1);
//            String outputStr = stringBuilderl.toString();
//            System.out.println(outputStr);
//            outputStream.write(outputStr.getBytes(charset));
        if (param != null && !param.isEmpty()) {
            Iterator<Map.Entry<String, Object>> iter = param.entrySet().iterator();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\r\n");
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();
                String inputName = (String) entry.getKey();
                Object inputValue = (Object) entry.getValue();
                if (inputValue instanceof String) {
                    stringBuilder.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    stringBuilder.append("Content-Disposition: form-data; name=\"" + inputName + "\"");
                    stringBuilder.append("\r\n\r\n");
                    stringBuilder.append(inputValue);
                }
            }
            System.out.println(stringBuilder.toString());
            System.out.println("1\r\n2");
            System.out.println("1\r\n\r\n2");
            Log.i("sout", stringBuilder.toString());
            outputStream.write(stringBuilder.toString().getBytes(charset));
            Iterator<Map.Entry<String, Object>> iter2 = param.entrySet().iterator();
            while (iter2.hasNext()) {
                Map.Entry<String, Object> entry = iter2.next();
                String inputName = (String) entry.getKey();
                Object inputValue = (Object) entry.getValue();
                StringBuilder stringBuilder2 = new StringBuilder();
                if (inputValue instanceof File) {
                    stringBuilder2.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    String filename = ((File) inputValue).getName();
//                    boolean i = false;
//                    if (filename.endsWith(".pcm")) {
//                        i = true;
//                        filename = filename.replaceAll(".pcm", ".wav");
//                    }
                    stringBuilder2.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                    stringBuilder2.append("Content-Type:" + "audio/wav");
                    stringBuilder2.append("\r\n\r\n");
                    System.out.println(stringBuilder2.toString());
                    FileInputStream p = new FileInputStream(((File) inputValue));
                    outputStream.write(stringBuilder2.toString().getBytes(charset));
//                    if (i) {
//                        outputStream.write(PcmToWav.addWaveHeader(p.getChannel().size()));
//                    }
                    DataInputStream in = new DataInputStream(p);
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        outputStream.write(bufferOut, 0, bytes);
                    }
                    outputStream.write("\r\n".getBytes());
                    p.close();
                    in.close();
                }
            }
            // 当outputStr不为null时向输出流写数据
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            outputStream.write(end_data);
            outputStream.flush();
            outputStream.close();
        }
        // 从输入流读取返回内容
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        // 释放资源
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        conn.disconnect();
        return buffer.toString();
    }

    public static String httpsFileRequest2(String requestUrl, String requestMethod, Map<String, Object> param, String charset, Progress progress) throws Exception {
        System.out.println(requestUrl);
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符

        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, tm, new SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        conn.setSSLSocketFactory(ssf);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
        conn.setRequestProperty("user-agent", "PostmanRuntime/7.1.5");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//            conn.setRequestProperty("content-length", "146425");
        conn.setRequestProperty("Connection", "keep-alive");
        OutputStream outputStream = conn.getOutputStream();
//            StringBuilder stringBuilderl = new StringBuilder();
//            if (param != null && !param.isEmpty()) {
//                for (Map.Entry<String, Object> entry : param.entrySet()) {
//                    try {
//                        if(entry.getValue() instanceof String)
//                        stringBuilderl
//                                .append(entry.getKey())
//                                .append("=")
//                                .append(URLEncoder.encode(entry.getValue().toString(), charset))
//                                .append("&");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            stringBuilderl.deleteCharAt(stringBuilderl.length() - 1);
//            String outputStr = stringBuilderl.toString();
//            System.out.println(outputStr);
//            outputStream.write(outputStr.getBytes(charset));
        if (param != null && !param.isEmpty()) {
            Iterator<Map.Entry<String, Object>> iter = param.entrySet().iterator();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\r\n");
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();
                String inputName = (String) entry.getKey();
                Object inputValue = (Object) entry.getValue();
                if (inputValue instanceof String) {
                    stringBuilder.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    stringBuilder.append("Content-Disposition: form-data; name=\"" + inputName + "\"");
                    stringBuilder.append("\r\n\r\n");
                    stringBuilder.append(inputValue);
                }
            }
            System.out.println(stringBuilder.toString());
            System.out.println("1\r\n2");
            System.out.println("1\r\n\r\n2");
            Log.i("sout", stringBuilder.toString());
            outputStream.write(stringBuilder.toString().getBytes(charset));
            Iterator<Map.Entry<String, Object>> iter2 = param.entrySet().iterator();
            while (iter2.hasNext()) {
                Map.Entry<String, Object> entry = iter2.next();
                String inputName = (String) entry.getKey();
                Object inputValue = (Object) entry.getValue();
                StringBuilder stringBuilder2 = new StringBuilder();
                if (inputValue instanceof File) {
                    stringBuilder2.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    String filename = ((File) inputValue).getName();
//                    boolean i = false;
//                    if (filename.endsWith(".pcm")) {
//                        i = true;
//                        filename = filename.replaceAll(".pcm", ".wav");
//                    }
                    stringBuilder2.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                    stringBuilder2.append("Content-Type:" + "audio/wav");
                    stringBuilder2.append("\r\n\r\n");
                    System.out.println(stringBuilder2.toString());
                    FileInputStream p = new FileInputStream(((File) inputValue));
                    outputStream.write(stringBuilder2.toString().getBytes(charset));
//                    if (i) {
//                        outputStream.write(PcmToWav.addWaveHeader(p.getChannel().size()));
//                    }
                    DataInputStream in = new DataInputStream(p);
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    int k = 0;

                    while ((bytes = in.read(bufferOut)) != -1) {
                        outputStream.write(bufferOut, 0, bytes);
                        k++;
                        if (k % 10 == 0) {
                            progress.notify(Long.valueOf(k * 1024 * 100 / p.getChannel().size()).intValue());
                        }

                    }
                    outputStream.write("\r\n".getBytes());
                    p.close();
                    in.close();
                }
            }
            // 当outputStr不为null时向输出流写数据
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            outputStream.write(end_data);
            outputStream.flush();
            outputStream.close();
        }
        // 从输入流读取返回内容
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        // 释放资源
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        conn.disconnect();
        return buffer.toString();
    }

}
