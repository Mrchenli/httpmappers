package httpmapper.httpclient;

import lombok.Data;

@Data
public class SignModel<T>{

    private String code;//业务是否成功 0失败 1成功
    private String  desc;//业务失败的原因
    private String sign;//签名
    private String appid;//我们给第三方合作机构的appid
    private T data;

}
