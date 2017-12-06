package mrchenli.crypt.des;

public interface DesService {

    int getKeyLength();

    String encrypt(String plain_data, String des_key);

    String decrypt(String encry_data, String des_key);

    String getRandomDesKey(int keyLength);


}
