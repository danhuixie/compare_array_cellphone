package com.voiceai.recorder;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecorderImpl implements Recorder {
    private static final String TAG = RecorderImpl.class.getSimpleName();
    private static RecorderImpl ourInstance;
    private RecorderListener listener;
    private RecorderConfig config;
    private Context cotext;
    // 用于实时显示音量大小的图标
    private double voiceValue = 0.0; // 麦克风获取的音量值
    private ArrayList<Double> voiceList = new ArrayList<>();//存放音量变化列表，用户计算平均音量
    private AudioRecord mAudioRecord;
    private ArrayList<byte[]> data = new ArrayList<>();//用户保存用户数据

    int recorderStatue;
    private String wavPath;

    public static RecorderImpl getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new RecorderImpl(context);
        }
        return ourInstance;
    }

    @Override
    public void setRecorderListener(RecorderListener listener) {
        this.listener = listener;
    }

    @Override
    public void setRecorderConfig(RecorderConfig recorderConfig) {
        this.config = recorderConfig;
    }


    @Override
    public RecorderConfig getRecorderConfig() {
        if (config == null)
            config = new RecorderConfig(cotext);
        return config;
    }

    private RecorderImpl(Context context) {
        this.cotext = context.getApplicationContext();
        recorderHandle = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 4:
                        byte[] fi = (byte[]) msg.obj;
                        if (listener != null) {
                            listener.onRecordFrame(fi);
                        }
                        break;
                    case 3:
                        if (listener != null) {
                            String filePath = (String) msg.obj;
                            Log.d(TAG, "audio file path = " + filePath);
                            if (listener != null) {
                                listener.onRecordEnd(filePath);
                            }
                        }
                        break;
                    case 1:
                        Double value = (Double) msg.obj;
                        if (listener != null) {
                            listener.onVolumeChange(value);
                        }
                        break;
                    case 2: {
                        if (listener != null) {
                            listener.onAudioError(msg.arg1, (String) msg.obj);
                        }
                    }
                    break;
                    case 0:
                        if (listener != null) {
                            listener.onRecordStart();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    Thread mthread;

    public void stopRecorder() {
        changeRecorderStatue(STATUE_STOP);
        if (mAudioRecord != null) {
            if (mAudioRecord.getRecordingState()== AudioRecord.RECORDSTATE_RECORDING) {
                mAudioRecord.stop();
            }
        }
        if (mthread != null) {
            mthread.interrupt();
        }

    }


    public void changeRecorderStatue(int i) {
        Log.e(TAG, "changestatue" + i);
        this.recorderStatue = i;
    }




    public void cancelRecorder() {
        changeRecorderStatue(STATUE_CANCEL);
        voiceList.clear();
        data.clear();
        if (mAudioRecord != null) {
            mAudioRecord.stop();
        }
        if (mthread != null) {
            mthread.interrupt();
        }
    }

    @Override
    public void updataConfig() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
        }
        mAudioRecord = new AudioRecord(getRecorderConfig().getAudioSource(), getRecorderConfig().getSampleFreq(), getRecorderConfig().getChannel(), getRecorderConfig().getAudioFormat(), getRecorderConfig().getBufferSize());
    }

    ;

    public void startRecorder() {
        stopRecorder();
        File file1 = new File(getRecorderConfig().getContinuousFileSavePath());
        if (!file1.exists()) {
            file1.mkdirs();
        }
        if (mAudioRecord == null) {
            mAudioRecord = new AudioRecord(getRecorderConfig().getAudioSource(), getRecorderConfig().getSampleFreq(), getRecorderConfig().getChannel(), getRecorderConfig().getAudioFormat(), getRecorderConfig().getBufferSize());
        }
        changeRecorderStatue(STATUE_INIT);

        // 启动线程，录制音频文件
        mthread = new Thread(saveRunnable2);
        mthread.start();
    }


    private static final int MIN_VOLUMN = 1;
    private static final int VOL_GET_INTERVAL = 100;

    private Runnable saveRunnable2 = new Runnable() {
        @Override
        public void run() {
            try {
                if (mAudioRecord == null || recorderStatue != 0) {
                    return;
                }
                voiceValue = 0.0;
                mAudioRecord.startRecording();

                voiceList.clear();
                data.clear();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = 2;
                message.arg1 = ERROR_UNKNOWN;
                message.obj = e.getMessage();
                recorderHandle.sendMessage(message);
                return;
            }
            byte[] audiodata = new byte[getRecorderConfig().getBufferSize()];
            int readsize = 0;
            Log.d(TAG, "开始录制作音频！");
            Message startMsg = Message.obtain();
            startMsg.what = 0;
            recorderHandle.sendMessage(startMsg);
            changeRecorderStatue(STATUE_RUNNING);

            long start = System.currentTimeMillis();
            long yinliangtime = System.currentTimeMillis();
            while (recorderStatue == STATUE_RUNNING) {
                // 保存文件
                if (mAudioRecord == null) {
                    return;
                }

                readsize = mAudioRecord.read(audiodata, 0, getRecorderConfig().getBufferSize());
                byte[] thedata = Arrays.copyOf(audiodata, audiodata.length);
                if (readsize > 0) {
                    data.add(thedata);
                    //超长录音将分段保存。
                    if (data.size()>5000){
                        if(getRecorderConfig().isFileSaveFlag()){
                            saveList(data);
                            data.clear();
                        }
                    }
                };

                if (getRecorderConfig().isYinliangControl() && (readsize > 0) && (audiodata.length > 0)) {
                    voiceValue = getVolumeMax(readsize, audiodata);
                    voiceList.add(voiceValue);
                } else {
                    voiceValue = 0.0;
                }
                if (getRecorderConfig().isYinliangControl() && System.currentTimeMillis() - yinliangtime > 100) {
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = Math.abs(voiceValue / 1000);
                    recorderHandle.sendMessage(message);
                    yinliangtime = System.currentTimeMillis();
                }
                if (thedata != null && thedata.length > 0) {
                    if (listener != null) {
                        listener.onRecordFrame(thedata);
                    }
                }
            }
            voiceValue = 0.0;
            Log.d(TAG, "录制结束！");
            try {
                if (mAudioRecord != null) {
                    mAudioRecord.stop();
                    mAudioRecord.release();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            mAudioRecord = null;
            if (recorderStatue == STATUE_CANCEL) {
                return;
            }
            changeRecorderStatue(STATUE_FINISH);
//            long fileSize = data.size() * getRecorderConfig().getBufferSize() / 1024;
            if (getRecorderConfig().isTimeControl()&&System.currentTimeMillis() - start<getRecorderConfig().getMintime_msecond()) {
                Message msg = Message.obtain();
                msg.obj = "声音太短";
                msg.what = 2;
                msg.arg1 = ERROR_TOO_SHORT;
                recorderHandle.sendMessage(msg);
                return;
            } else if (getRecorderConfig().isTimeControl()&&System.currentTimeMillis() - start>getRecorderConfig().getLongtime_msecond()) {
                Message msg = Message.obtain();
                msg.obj = "声音太长";
                msg.arg1 = ERROR_TOO_LONG;
                msg.what = 2;
                recorderHandle.sendMessage(msg);
                return;
            }
            double average = getAverage(voiceList);
            if (getRecorderConfig().isYinliangControl() && average < MIN_VOLUMN) {
                Message msg = Message.obtain();
                msg.obj = "音量太小";
                msg.what = 2;
                msg.arg1 = ERROR_VOICE_TOO_LOW;
                recorderHandle.sendMessage(msg);
                return;
            }
            if (getRecorderConfig().isFileSaveFlag()) {
                saveList(data);
                if (listener != null) {
                    Message msg = Message.obtain();
                    msg.obj = wavPath;
                    msg.what = 3;
                    recorderHandle.sendMessage(msg);
                }
                data.clear();
            }
        }
    };
    private Handler recorderHandle;

    @Override
    public void onDestory() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
            listener=null;
        }
    }

    public static void write(byte[] bytes,String path ,String filename){
        File pre = new File(path);
        if (!pre.exists()) {
            pre.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = (new FileOutputStream(filename, true));
            out.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveList(List<byte[]> list) {
//        String pcmName = null;
        String wavName = null;
//        String audioPath = null;
        FileOutputStream fos = null;
        wavPath = null;
        try {
//            pcmName = System.currentTimeMillis() / 1000 + ".pcm";
            wavName = System.currentTimeMillis() / 1000 + ".wav";
//            audioPath = getRecorderConfig().getContinuousFileSavePath() + "/" + pcmName;
            wavPath = getRecorderConfig().getContinuousFileSavePath() + "/" + wavName;
            File file = new File(wavPath);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
            int i=0;
            for (byte[] bytes : list) {
               i+=bytes.length;
            }
            long totalAudioLen = i;
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = getRecorderConfig().getSampleFreq();
            int channels = 1;
            long byteRate = 16 * getRecorderConfig().getSampleFreq() * channels / 8;
            fos.write(getHeader(totalAudioLen, totalDataLen, longSampleRate, channels, byteRate));
            for (byte[] bytes : list) {
                fos.write(bytes);
            }
            Log.d(TAG, "开始压缩wav！");
//            copyWaveFile(audioPath, wavPath);// 给裸数据加上头
            Log.d(TAG, "压缩wav成功！");
//            deleteFile(new File(audioPath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private static int getVolumeMax(int r, byte[] bytes_pkg) {
        int mShortArrayLenght = r / 2;
        short[] short_buffer = byteArray2ShortArray(bytes_pkg,
                mShortArrayLenght);
        int max = 0;
        if (r > 0) {
            for (int i = 0; i < mShortArrayLenght; i++) {
                if (Math.abs(short_buffer[i]) > max) {
                    max = Math.abs(short_buffer[i]);
                }
            }
        }
        return max;
    }

    public static short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
        return retVal;
    }

    // 这里得到可播放的音频文件
    public static void copyWaveFile(String inFilename, int src_sample_rate,String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate =src_sample_rate;
        int channels = 1;
        long byteRate = 16 * src_sample_rate * channels / 8;
        byte[] data = new byte[1024];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                            long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {

        out.write(getHeader(totalAudioLen, totalDataLen, longSampleRate, channels, byteRate), 0, 44);
    }
    public static byte[] getHeader(long totalAudioLen,
                                   long totalDataLen, long longSampleRate, int channels, long byteRate) {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (1 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        char a = '\0';
        return header;
    }

    public static double getAverage(ArrayList<Double> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += (Double) list.get(i);
        }
        if (list.size() <= 0) {
            return 0;
        }
        double average = (double) (sum / list.size());
        return average / 1000;
    }
//    /**
//     * 删除文件
//     *
//     * @param file
//     */
//    public static void deleteFile(File file) {
//        if (file == null) return;
//        if (file.exists()) {
//            if (file.isFile()) {
//                file.delete();
//            } else if (file.isDirectory()) {
//                File files[] = file.listFiles();
//                if (files == null) return;
//                for (int i = 0; i < files.length; i++) {
//                    deleteFile(files[i]);
//                }
//            }
//            file.delete();
//        } else {
//            System.out.println("文件不存在！" + "\n");
//        }
//    }
}
