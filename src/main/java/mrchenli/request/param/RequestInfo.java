package mrchenli.request.param;

import lombok.Data;

@Data
public class RequestInfo {

    private String url;
    private String urlCharset;
    private long timeOut;

}
