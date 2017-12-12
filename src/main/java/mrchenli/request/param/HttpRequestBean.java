package mrchenli.request.param;

import lombok.Data;

import java.util.Map;
@Data
public class HttpRequestBean {
    private Object param;
    private Map<String,String> headers;
}
