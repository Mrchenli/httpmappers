package mrchenli.crypt.rsa;

import javax.crypto.Cipher;

public class PhpRsaServiceImpl extends AbstractRsaService implements RsaService {

    private static final ThreadLocal<Cipher> cipherPkcs1ThreadLocal = ThreadLocal.withInitial(() -> {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    });

    public PhpRsaServiceImpl(String privateKey, String publicKey) {
       super(privateKey,publicKey);
    }


    @Override
    public Cipher getCipper() {
        return cipherPkcs1ThreadLocal.get();
    }



}
