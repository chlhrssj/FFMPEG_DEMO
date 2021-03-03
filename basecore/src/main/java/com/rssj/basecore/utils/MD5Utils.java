package com.rssj.basecore.utils;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2018/1/30.
 * MD5加密
 */

public class MD5Utils {

    /**
     * Md5加密
     * @param source
     * @return
     */
    public static String md5(String source) {
        String encode = source;
        StringBuilder stringbuilder = new StringBuilder();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(encode.getBytes());
            byte[] str_encoded = md5.digest();
            for (int i = 0; i < str_encoded.length; i++) {
                if ((str_encoded[i] & 0xff) < 0x10) {
                    stringbuilder.append("0");
                }
                stringbuilder.append(Long.toString(str_encoded[i] & 0xff, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringbuilder.toString();
    }

}
