package httpmapper.httpclient;

import lombok.Data;

@Data
public class RsaModel<T> extends SignModel<T>{
    private String des_key;
    private String rsaDataString;//业务数据

}
