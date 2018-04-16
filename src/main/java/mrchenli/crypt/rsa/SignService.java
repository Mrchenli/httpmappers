package mrchenli.crypt.rsa;

public interface SignService {

    String objParamSort(Object o);

    boolean verifySign(String sign, String param);

    String generateSign(String params);

}
