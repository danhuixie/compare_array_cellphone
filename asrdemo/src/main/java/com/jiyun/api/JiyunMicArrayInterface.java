package com.jiyun.api;

public interface JiyunMicArrayInterface {

  /**
   * 初始化函数。
   * 配置麦克风阵列和降噪算法，提供文件路径。具体参数可再调整。
   * 此外，需要在初始化的过程中检查设备是否存在、是否可以打开等，如果有问题应当立刻返回，初始化失败。
   * @param modelFile 模型文件（如降噪算法模型）的路径
   * @param configFile 配置文件的路径。配置文件可以用来配置其他参数，格式可由极云决定
   * @param recordDirectory 录音文件夹路径。调用者需保证对文件的读写权限
   * @return 初始化是否成功。
   */
  boolean initialize(String modelFile, String configFile, String recordDirectory);

  /**
   * 释放native资源。
   * 在initialize之前或release之后调用除setLogLevel以外的函数会导致错误（IllegalStateException）
   */
  void release();

  /**
   * @return 是否在抓取。
   */
  boolean isCapturing();

  /**
   * 开始抓取。开始后返回。已经开始后可反复调用，没有影响。
   */
  void startCapturing();

  /**
   * 结束抓取。已经结束后可反复调用，没有影响。
   */
  void stopCapturing();

  /**
   * 读取音频数据。
   * native层负责填写buffer的内容，提供经beamforming和降噪处理后的数据。
   * @param buffer byte[]，长度大于offset + length
   * @param offset 起始位置
   * @param length 预计的读取长度。对于长度可做特殊要求（如必须为1024等），不符合要求则返回错误代码
   * @return 实际的读取长度，或者错误代码（<0）
   */
  int read(byte[] buffer, int offset, int length);

  /**
   * 设置当前beamforming的角度。
   * 调用此函数可能重置音频采集，设置成功后音频应当恢复。如果参数超出合理范围，按边缘值处理。
   * 采用角度degree（0-180），在4麦克风条形阵列里，麦克风1-4连线，麦克风1方向为180度，
   * 麦克风4方向为0度，正面为90度。
   * @param angle 角度degree（int）
   */
  void setBeamformingAngle(int angle);

  /**
   * 获取当前beamforming角度。此调用不应干扰音频采集。
   * @return 当前角度，degree（int）
   */
  int getBeamformingAngle();

  /**
   * 获取可设置的最大角度。
   */
  int getMaxBeamformingAngle();

  /**
   * 获取可设置的最小角度。
   */
  int getMinBeamformingAngle();

  /**
   * 获取声源定位信息。此调用不应干扰音频采集。
   * @return 当前声源角度，采用角度单位degree（int[]）。如果支持多声源则返回长度大于1的int[]，
   * 不然则返回长度为1的int[]；没有检测到声源时返回长度为0的int[]
   */
  int[] getSoundLocalization();
}
