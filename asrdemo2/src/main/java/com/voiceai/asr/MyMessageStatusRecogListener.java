package com.voiceai.asr;

import android.os.Handler;
import android.widget.TextView;

import com.baidu.aip.asrwakeup3.core.recog.RecogResult;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.voiceai.vprcjavasdk.ASRSDK;

public class MyMessageStatusRecogListener extends MessageStatusRecogListener {
    private final TextView textview;
    private final TextView statueTextview;
    public MyMessageStatusRecogListener(Handler handler, TextView textView, TextView statueTextview) {
        super(handler);
        this.textview = textView;
        this.statueTextview = statueTextview;
    }

    @Override
    public void onAsrReady() {
        super.onAsrReady();
        if (statueTextview!=null)
            statueTextview.setText("初始化完成");


    }

    @Override
    public void onAsrBegin() {
        super.onAsrBegin();
        if (statueTextview!=null)
            statueTextview.setText("说话中");
    }

    @Override
    public void onAsrEnd() {
        super.onAsrEnd();
        if (statueTextview!=null)
            statueTextview.setText("说话结束");
    }

    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL,
//                "临时识别结果，结果是“" + results[0] + "”；原始json：" + recogResult.getOrigalJson());
        super.onAsrPartialResult(results, recogResult);
        ASRSDK. dealTextView(textview, results[0], false);
    }

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        super.onAsrFinalResult(results, recogResult);
//        String message = "识别结束，结果是”" + results[0] + "”";
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL,
//                message + "；原始json：" + recogResult.getOrigalJson());
//
//        sendMessage(message, status, true);
        ASRSDK.dealTextView(textview, results[0], true);
    }



    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage,
                                 RecogResult recogResult) {
        super.onAsrFinishError(errorCode, subErrorCode, descMessage, recogResult);
//        String message = "【asr.finish事件】识别错误, 错误码：" + errorCode + " ," + subErrorCode + " ; " + descMessage;
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL, message);
//        if (speechEndTime > 0) {
//            long diffTime = System.currentTimeMillis() - speechEndTime;
//            message += "。说话结束到识别结束耗时【" + diffTime + "ms】";
//        }
//        speechEndTime = 0;
//        sendMessage(message, status, true);
//        speechEndTime = 0;
        statueTextview.setText("捕捉异常 errcode ：" +subErrorCode+"  "+descMessage);
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        super.onAsrOnlineNluResult(nluResult);
//        if (!nluResult.isEmpty()) {
//            sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL, "原始语义识别结果json：" + nluResult);
//        }

    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {
        super.onAsrFinish(recogResult);
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_FINISH, "识别一段话结束。如果是长语音的情况会继续识别下段话。");

    }

    /**
     * 长语音识别结束
     */
    @Override
    public void onAsrLongFinish() {
        super.onAsrLongFinish();
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH, "长语音识别结束。");
        if (statueTextview!=null)
            statueTextview.setText("结束");
    }


    /**
     * 使用离线命令词时，有该回调说明离线语法资源加载成功
     */
    @Override
    public void onOfflineLoaded() {
        super.onOfflineLoaded();
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_LOADED, "离线资源加载成功。没有此回调可能离线语法功能不能使用。");
    }

    /**
     * 使用离线命令词时，有该回调说明离线语法资源加载成功
     */
    @Override
    public void onOfflineUnLoaded() {
        super.onOfflineUnLoaded();
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED, "离线资源卸载成功。");
    }

    @Override
    public void onAsrExit() {
        super.onAsrExit();
//        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_EXIT, "识别引擎结束并空闲中");
    }
}
