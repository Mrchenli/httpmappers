package mrchenli.crypt.des;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DesServiceImpl extends AbstractDesService implements DesService {

    private static SecretKey keyGenerator(String keyStr) throws Exception {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        DESKeySpec keySpec = new DESKeySpec(hexString2Bytes(keyStr));
        keyFactory.generateSecret(keySpec);
        return keyFactory.generateSecret(keySpec);
    }

    @Override
    public int getKeyLength() {
        return 16;
    }

    @Override
    public String encrypt(String plain_data,String des_key_str) {
        try {
            Cipher cipher =  Cipher.getInstance("DES/ECB/PKCS5Padding");
            SecureRandom random = new SecureRandom();
            Key deskey = keyGenerator(des_key_str);
            cipher.init(Cipher.ENCRYPT_MODE,deskey,random);
            byte[] results = cipher.doFinal(plain_data.getBytes());
            return Base64.encodeBase64String(results);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String decrypt(String encry_data,String des_key_str) {
        try{
            Cipher cipher =  Cipher.getInstance("DES/ECB/PKCS5Padding");
            Key deskey = keyGenerator(des_key_str);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return new String(cipher.doFinal(Base64.decodeBase64(encry_data)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static int parse(char c) {
        if (c >= 'a') return (c - 'a' + 10) & 0x0f;
        if (c >= 'A') return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] hexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

}
