package com.project.ingprog.gamecym;

/**
 * Created by danyh on 3/27/2016.
 */

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {

    public static String generateIV()
    {
        String iv = new String();
        Random generator = new Random();

        for (int i = 0; i < 16; i++)
        {
            int a = generator.nextInt(3);

            switch (a)
            {
                //int
                case 0:
                    int k1 = generator.nextInt(10);
                    iv += Integer.toString(k1);
                    break;
                //lowercase
                case 1:
                    int k2 = generator.nextInt(26) + (int)'a';
                    iv += (char)(k2);
                    break;
                //uppercase
                case 2:
                    int k3 = generator.nextInt(26) + (int)'A';
                    iv += (char)k3;
                    break;
            }
        }

        return iv;
    }

    public  static String encrypt(String key, String value)
    {
        return encrypt(key, generateIV(), value);
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

            return initVector +
                    android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP & android.util.Base64.NO_PADDING);

            //return new String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public  static String decrypt(String key, String encrypted)
    {
        String iv = encrypted.substring(0, 16);
        String encText = encrypted.substring(16);

        return decrypt(key, iv, encText);
    }


    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            //byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
            byte[] original = cipher.doFinal(android.util.Base64.decode(encrypted.getBytes(), android.util.Base64.NO_WRAP & android.util.Base64.NO_PADDING));
            //byte[] original = cipher.doFinal(encrypted.getBytes("UTF-8"));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
