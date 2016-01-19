/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    NiubCoreLibrary
 *
 *    SecurityUtil
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  2011-10-13
 *    @version: 1.0
 *
 ******************************************************************************/

package la.niub.util.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * SecurityUtil of NiubCoreLibrary.
 * 
 * @author dhu
 */
public class SecurityUtil {

    private static String LOG_TAG = "SecurityUtil";

    public static String toHexString(byte[] data) {
        if (null == data || 0 == data.length) {
            return "";
        }
        return IntegralToString.bytesToHexString(data, true);
    }

    public static byte[] sha1Hash(byte[] data) {
        if (null == data || data.length == 0) {
            return null;
        }
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("SHA-1");
            algorithm.reset();
            algorithm.update(data);
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            Log.e(e);
        }
        return null;
    }

    public static byte[] md5Hash(byte[] data) {
        if (null == data || data.length == 0) {
            return null;
        }
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(data);
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            Log.e(e);
        }
        return null;
    }

    public static String md5Hash(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            byte[] bytes = md5Hash(str.getBytes("UTF-8"));
            String result = toHexString(bytes).toLowerCase(java.util.Locale.US);
            return result;
        } catch (UnsupportedEncodingException e) {
            Log.e(e);
        }
        return null;
    }

    public static byte[] md5Hash(InputStream is) {
        if (null == is) {
            return null;
        }

        MessageDigest algorithm;
        byte[] buffer = new byte[4028];
        int length;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            while ((length = is.read(buffer)) != -1) {
                algorithm.update(buffer, 0, length);
            }
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (FileNotFoundException e) {
            Log.e(e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(e);
        } catch (IOException e) {
            Log.e(e);
        }
        return null;
    }

    public static String generateGUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static PublicKey getPublicKey(byte[] key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static byte[] rsaDecode(byte[] cipherData, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherData);
    }

    public static byte[] rsaEncode(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    private static final String DES_ALGORITHM = "DES";
    private static final String DES_TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final String DES_IV = "12345678";

    public static String desEncrypt(String key, byte[] data) throws Exception {
        DESKeySpec dks = new DESKeySpec(StringUtil.getAsciiBytes(key));
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance(DES_ALGORITHM);
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(
                StringUtil.getAsciiBytes(DES_IV));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
        byte[] bytes = cipher.doFinal(data);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static byte[] desDecrypt(String key, byte[] data) throws Exception {
        DESKeySpec dks = new DESKeySpec(StringUtil.getAsciiBytes(key));
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance(DES_ALGORITHM);
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(
                StringUtil.getAsciiBytes(DES_IV));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        return cipher.doFinal(data);
    }

    public static String desEncrypt(String key, String data) {
        String result = null;
        try {
            result = desEncrypt(key, StringUtil.getUtf8OrDefaultBytes(data));
        } catch (Exception e) {
            Log.e(e);
        }
        return result;
    }

    public static String desDecrypt(String key, String data) {
        String result = null;
        try {
            byte[] bytes = desDecrypt(key, Base64.decode(data, Base64.NO_WRAP));
            result = StringUtil.newUtf8OrDefaultString(bytes);
        } catch (Exception e) {
            Log.e(e);
        }
        return result;
    }

    /**
     * 计算给定数据的 MD5 签名。
     * 
     * @param data 要计算签名的数据。
     * @return data 的 MD5 签名。
     */
    public static byte[] md5(byte[] data) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(data);
            return msgDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算给定数据的 MD5 签名。
     * 
     * @param value 要计算签名的数据。
     * @return value 的 MD5 签名。
     */
    public static byte[] md5(String value) {
        byte[] data;
        try {
            data = value.getBytes("utf-8");
            return md5(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] md5(File file) {
        if (null == file) {
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return md5Hash(is);
        } catch (FileNotFoundException e) {
            Log.e(e);
        } finally {
            IoUtils.closeQuietly(is);
        }
        return null;
    }

    /**
     * 计算给定数据的 MD5 签名。
     * 
     * @param data 要计算签名的数据。
     * @return data 的 MD5 签名。
     */
    public static String md5String(byte[] data) {
        byte[] digest = md5(data);
        return asHexString(digest);
    }

    /**
     * 计算给定数据的 MD5 签名。
     * 
     * @param value 要计算签名的数据。
     * @return value 的 MD5 签名。
     */
    public static String md5String(String value) {
        byte[] digest = md5(value);
        return asHexString(digest);
    }

    public static String md5File(File file) {
        byte[] digest = md5(file);
        return asHexString(digest).toLowerCase();
    }

    /**
     * 将给定的二进制数据转换为十六进制字符串。
     * 
     * @param data 要转换的二进制数据。
     * @return data 的十六进制字符串表示。
     */
    public static String asHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
