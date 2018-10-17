package com.voiceai.vprcjavasdk;

import android.media.AudioFormat;
import android.media.AudioRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by qing on 11/01/2018.
 */

public class PcmToWav {
    private static int SAMPLE_RATE_IN_HZ = 16000;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
            AudioFormat.ENCODING_PCM_16BIT);


    public static void writePcm(String pcmfile, String wavfile) {
        copyWaveFile(pcmfile, wavfile);
    }

    /**
     * pcm转wav
     *
     * @param inFilename
     * @param outFilename
     */
    private static void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = SAMPLE_RATE_IN_HZ;
        int channels = 1;
        long byteRate = 16 * SAMPLE_RATE_IN_HZ * channels / 8;
        byte[] data = new byte[BUFFER_SIZE];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copyWaveFile(BlockingQueue<byte[]> spQueue, String outFilename) throws Exception {
        FileOutputStream fos = null;
        fos = new FileOutputStream(new File(outFilename));
        byte[] spMsg = null;
        int i = 0;
        while ((spMsg = spQueue.poll()) != null) {
            fos.write(spMsg);
        }
        fos.close();
    }

    public static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long longSampleRate) throws IOException {
        int channels = 1;
        long byteRate = 16 * longSampleRate * channels / 8;
        writeWaveFileHeader(out, totalAudioLen, totalAudioLen + 36, longSampleRate, channels, byteRate);
    }

    /**
     * 增加wav的头文件
     */
    public static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                           long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
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
        out.write(header, 0, 44);
    }

    public static byte[] addWaveHeader(long bufflen) {
        byte[] out = new byte[44];
        // RIFF/WAVE out
        try {
            out[0] = 'R';
            out[1] = 'I';
            out[2] = 'F';
            out[3] = 'F';
            long len1 = bufflen + 36;
            out[4] = (byte) (len1 & 0xff);
            out[5] = (byte) ((len1 >> 8) & 0xff);
            out[6] = (byte) ((len1 >> 16) & 0xff);
            out[7] = (byte) ((len1 >> 24) & 0xff);
            out[8] = 'W';
            out[9] = 'A';
            out[10] = 'V';
            out[11] = 'E';
            // 'fmt ' chunk
            out[12] = 'f';
            out[13] = 'm';
            out[14] = 't';
            out[15] = ' ';
            out[16] = 16; // 4 bytes: size of 'fmt ' chunk
            out[17] = 0;
            out[18] = 0;
            out[19] = 0;
            out[20] = 1; // format = 1
            out[21] = 0;
            int channels = 1;
            out[22] = (byte) channels;
            out[23] = 0;
            int longSampleRate = 16000;
            out[24] = (byte) (longSampleRate & 0xff);
            out[25] = (byte) ((longSampleRate >> 8) & 0xff);
            out[26] = (byte) ((longSampleRate >> 16) & 0xff);
            out[27] = (byte) ((longSampleRate >> 24) & 0xff);
            int byteRate = longSampleRate * 2;
            out[28] = (byte) (byteRate & 0xff);
            out[29] = (byte) ((byteRate >> 8) & 0xff);
            out[30] = (byte) ((byteRate >> 16) & 0xff);
            out[31] = (byte) ((byteRate >> 24) & 0xff);
            out[32] = (byte) (1 * 16 / 8); // block align
            out[33] = 0;
            out[34] = 16; // bits per sample
            out[35] = 0;
            out[36] = 'd';
            out[37] = 'a';
            out[38] = 't';
            out[39] = 'a';
            out[40] = (byte) (bufflen & 0xff);
            out[41] = (byte) ((bufflen >> 8) & 0xff);
            out[42] = (byte) ((bufflen >> 16) & 0xff);
            out[43] = (byte) ((bufflen >> 24) & 0xff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

}
