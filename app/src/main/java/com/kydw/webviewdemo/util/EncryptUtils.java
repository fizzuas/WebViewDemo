package com.kydw.webviewdemo.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * <author>SimonLay</author>
 * <date>2017/2/13</date>
 * <time>16:54</time>
 */

public class EncryptUtils {

    private final static byte[] key = new byte[]{0x77, 0x0a, (byte) 0xf4, (byte) 0xd0
            , 0x2d, (byte) 0xf6, 0x0d, 0x78
            , 0x53, (byte) 0xed, (byte) 0xa9, (byte) 0xd5
            , (byte) 0xb2, (byte) 0x98, 0x4f, (byte) 0xde};


    public static String encryptData(Object param, Type clazz) {
        Gson gson = new Gson();
        String json = gson.toJson(param, clazz);
        LogUtils.d("加密的Json数据", json);
        byte[] en = null;
        try {
            en = AesCipher.encrypt(key, json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String base = null;
        if (null != en) {
            base = Base64Helper.encode(en);
        }
        return base;
    }

    public static String encryptData(String data) {
        byte[] en = null;
        try {
            en = AesCipher.encrypt(key, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String base = null;
        if (null != en) {
            base = Base64Helper.encode(en);
        }
        return base;
    }

    public static String encryptData(byte[] data) {

        byte[] en = null;
        try {
            en = AesCipher.encrypt(key, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ByteUtils.byteArray2HexString(en);
    }
}
