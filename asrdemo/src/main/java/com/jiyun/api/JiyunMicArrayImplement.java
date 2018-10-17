package com.jiyun.api;
import com.jiyun.api.JiyunSpicax;
import com.voiceai.vprcjavasdk.ASRSDK;

import java.io.IOException;
import java.io.InputStream;

public class JiyunMicArrayImplement extends InputStream implements JiyunMicArrayInterface {
  JiyunSpicax processor = new JiyunSpicax();

  public boolean initialize(String modelFile, String configFile, String recordDirectory) {
    return processor.initialize(modelFile, recordDirectory);
  }
  public static JiyunMicArrayImplement getInstance(){
    return new JiyunMicArrayImplement();
  }
  public void release() {
    processor.release();
  }

  public boolean isCapturing() {
    return processor.isCapturing();
  }

  public void startCapturing() {
    processor.startCapturing();
  }

  public void stopCapturing() {
    processor.stopCapturing();
  };

  @Override
  public int read() throws IOException {
    return 0;
  }

  //buffer: buffer address
  //length: required bytes
  //return : actual read bytes
  public int read(byte[] buffer, int offset, int length) {
    byte [] temp = new byte[length];
    int read_size = processor.read(temp, length);
//    for (int i = 0; i < read_size; i++) {
//      buffer[offset + i] = temp[i];
//    }
    System.arraycopy(temp,0,buffer,offset,Math.min(read_size,length));
//    ASRSDK.getInstance().send(temp);
    return read_size;
  };

  public void setBeamformingAngle(int angle) {
    processor.setBeamformingAngle(angle);
  };

  public int getBeamformingAngle() {
    return processor.getBeamformingAngle();
  };

  public int getMaxBeamformingAngle() {
    return processor.getMaxBeamformingAngle();
  };

  public int getMinBeamformingAngle() {
    return processor.getMinBeamformingAngle();
  };

  public int[] getSoundLocalization() {
    return processor.getSoundLocalization();
  }
}
