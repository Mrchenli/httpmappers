package mrchenli.handler;

import mrchenli.request.MapperRequest;
import mrchenli.request.param.HttpRequestBean;

import java.util.Map;

public interface PostProcessor {

    void handlerBefore(Map<String,String> headers ,Map<String,Object> params,Map<String,String> urlParams);

    Object handleAfter(MapperRequest mapperRequest,HttpRequestBean requestBean,Object object);

}
