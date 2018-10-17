package com.jiyun.api;

import android.media.AudioFormat;

public class JiyunSpicax {
  static {
    System.loadLibrary("jiyun-spicax");
  }

  public native boolean initialize(String modelFile, String recordDirectory);
  public native void release();
  public native void startCapturing();
  public native void stopCapturing();
  public native boolean isCapturing();
  public native int read(byte[]buffer, int required_size);
  public native void setBeamformingAngle(int angle);
  public native int getBeamformingAngle();
  public native int[] getAngle();
  public native int getMaxBeamformingAngle();
  public native int getMinBeamformingAngle();
  public native int[] getSoundLocalization();
}