package mrchenli.crypt.rsa;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaServiceImpl implements RsaService {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private PKCS8EncodedKeySpec pkeySpec;
    private X509EncodedKeySpec xkeySpec;

    private static final ThreadLocal<Cipher> cipherThreadLocal = ThreadLocal.withInitial(() -> {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    });

    public RsaServiceImpl(String privateKey, String publicKey) {
        try{
            this.privateKey = getPrivateKey(privateKey);
            this.publicKey = getPublicKey(publicKey);
            this.pkeySpec =  new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            this.xkeySpec =  new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        }  catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private RSAPublicKey getPublicKey(String publicKeyStr) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] buffer = Base64.decodeBase64(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private RSAPrivateKey getPrivateKey(String privateKeyStr) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] buffer = Base64.decodeBase64(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }


    /**
     * 加密
     * @param plain_data
     * @return
     */
    @Override
    public String encrypt(String plain_data) {
        try {
            Cipher cipher = cipherThreadLocal.get();
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            byte[] output = cipher.doFinal(plain_data.getBytes());
            return Base64.encodeBase64String(output);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * @param encry_data
     * @return
     */
    @Override
    public String decrypt(String encry_data) {
        try {
            Cipher cipher = cipherThreadLocal.get();
            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            byte[] output = cipher.doFinal(Base64.decodeBase64(encry_data));
            return new String(output);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 校验签名
     * @param sign
     * @return
     */
    @Override
    public boolean verifySign(String sign,String param) {
        try{
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PublicKey key = keyFactory.generatePublic(xkeySpec);
            //生成签名的类 生成签名的算法
            //生成签名需要 私钥
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(key);
            //生成签名 参数
            signature.update(param.getBytes());
            return signature.verify(Base64.decodeBase64(sign));
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成签名
     * @param params
     * @return
     */
    @Override
    public String generateSign(String params) {
        try {
            return generateSignWithspec(params,pkeySpec);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这个是某家特殊情况 把他们的私钥 给我们生成签名给他们
     * @param params
     * @param privateKey
     * @return
     */
    @Override
    public String generateSignWithPrivateKey(String params, String privateKey) {
        try {
            PKCS8EncodedKeySpec pspec =  new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            return generateSignWithspec(params,pspec);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private String generateSignWithspec(String params ,PKCS8EncodedKeySpec pkspec){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = keyFactory.generatePrivate(pkspec);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(key);
            signature.update(params.getBytes());
            return Base64.encodeBase64String(signature.sign());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
