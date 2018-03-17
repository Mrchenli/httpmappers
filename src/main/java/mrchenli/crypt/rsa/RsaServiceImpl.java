package mrchenli.crypt.rsa;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;

public class RsaServiceImpl extends AbstractRsaService implements RsaService {



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
        super(privateKey,publicKey);
    }

    @Override
    public Cipher getCipper() {
        return cipherThreadLocal.get();
    }

}
