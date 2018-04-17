package mrchenli.crypt.rsa;

public interface RsaService {

	String encrypt(String plain_data);

	String decrypt(String encry_data);

	boolean verifySign(String sign, String param);

	String generateSign(String params);

	String generateSignWithPrivateKey(String params, String privateKey);

}
