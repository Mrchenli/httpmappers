package mrchenli.crypt.des;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PhpDesServiceImpl extends AbstractDesService implements DesService {

    private static final String TAG = "DES3Util";
    private static final String DES3 = "DESede";
    private static final Logger Log = LoggerFactory.getLogger(PhpDesServiceImpl.class);

    @Override
    public int getKeyLength() {
        return 24;
    }

    @Override
    public String encrypt(String plain_data,String des_key) {
        try {
            SecretKey DESKey = new SecretKeySpec(des_key.getBytes(), DES3);    //生成密钥
            Cipher cipher = Cipher.getInstance(DES3 + "/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, DESKey);
            return Base64.encodeBase64String(cipher.doFinal(plain_data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public String decrypt(String encry_data,String des_key) {
        try {
            SecretKey DESKey = new SecretKeySpec(des_key.getBytes(), DES3);    //生成密钥
            Cipher cipher = Cipher.getInstance(DES3 + "/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, DESKey);
            return new String(cipher.doFinal(Base64.decodeBase64(encry_data)));
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(TAG, e.getMessage());
            return null;
        }
    }

}
