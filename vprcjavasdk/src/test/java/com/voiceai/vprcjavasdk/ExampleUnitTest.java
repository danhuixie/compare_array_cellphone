package com.voiceai.vprcjavasdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws InterruptedException {
        assertEquals(4, 2 + 2);


        String k="15311152814657912a101223890612720e9a133bf9b70dfba20ec317353043c1a8aa2ad9fa48a54f|1531115281465791ce7c827f127a485ab158618592a7578d";
        System.out.println(MD5.getSHA256StrJava(k));
    }
}