package com.voiceai.vprcjavasdk;

/**
 *  网络访问后的回调，线程当前为主线程
 */
public interface Response {
    /**
     *  网络访问后的回调，线程当前为主线程
     */
    void response(String res, Throwable e);

}
