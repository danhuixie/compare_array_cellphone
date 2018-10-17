package com.voiceai;

import android.util.Log;

import com.jiyun.api.JiyunMicArrayImplement;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class BaiduInputStream extends InputStream{
    @Override
    public int read() throws IOException {
        return 0;
    }

    public int read(byte[] buffer, int offset, int length) {
            if(canzhen != null&&canzhen.length>length){
                int read_size=Math.min(length,Math.max(buffer.length-offset,0));
                System.arraycopy(canzhen,0,buffer,offset,read_size);
                byte[]canzhenl=new byte[canzhen.length-read_size];
                System.arraycopy(canzhen,read_size,canzhenl,0,canzhenl.length);
                canzhen=canzhenl;
                return read_size;
            }else{
                try {
                    Log.e("BaiduInputStream","queue.size"+queue.size());
                    byte[] ta=  queue.take();
                    if(canzhen!=null) {
                        byte[] canzhenz = new byte[canzhen.length +ta.length];
                        System.arraycopy(canzhen,0,canzhenz,0,canzhen.length);
                        System.arraycopy(ta,0,canzhenz,canzhen.length,ta.length);
                        canzhen=canzhenz;
                    }else{
                        canzhen=ta;
                    }
                    return read(buffer,offset,length);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return -1;
                }
        }
    }
    public static BaiduInputStream instance =new BaiduInputStream();
    public static BaiduInputStream getInstance(){
        return instance;
    }

    public ArrayBlockingQueue<byte[]> queue=new ArrayBlockingQueue<byte[]>(1000);
    public byte[] canzhen;
    public void push(byte[] data){
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
