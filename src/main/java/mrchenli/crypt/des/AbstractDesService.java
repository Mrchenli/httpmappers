package mrchenli.crypt.des;

import java.util.Random;

public abstract class AbstractDesService implements DesService {

    @Override
    public String getRandomDesKey(int keyLength) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer Keysb = new StringBuffer();
        for(int i = 0; i<keyLength; i++){
            int number = random.nextInt(base.length());
            Keysb.append(base.charAt(number));
        }
        return Keysb.toString();
    }

}
