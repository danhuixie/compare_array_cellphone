package com.voiceai.vprcjavasdk;


//外部注入错误吗解释器。
public abstract class ErrorCodeInterpreter {
    public abstract String getMessage(MThrowable mThrowable);

    public static ErrorCodeInterpreter e;

}
