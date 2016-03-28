package com.project.ingprog.gamecym;

/**
 * Created by danyh on 3/27/2016.
 */

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {

    public  static String encrypt(String key, String value)
    {
        return encrypt(key, key, value);
    }


    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            //System.out.println("encrypted string: "
            //        + Base64.encodeBase64String(encrypted));

            //return Base64.encodeBase64String(encrypted);

            return android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP);

            //return new String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public  static String decrypt(String key, String encrypted)
    {
        return decrypt(key, key, encrypted);
    }


    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            //byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
            byte[] original = cipher.doFinal(android.util.Base64.decode(encrypted.getBytes(), Base64.NO_WRAP));
            //byte[] original = cipher.doFinal(encrypted.getBytes("UTF-8"));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
