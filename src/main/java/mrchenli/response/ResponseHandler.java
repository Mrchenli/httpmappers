package mrchenli.response;


import mrchenli.request.MapperRequest;
import org.apache.http.HttpResponse;
import org.dom4j.DocumentException;

import java.io.IOException;


public interface ResponseHandler {

    /**
     * @param request 注解配置的信息里面有返回类型
     * @param response 返回的数据呀
     * @return
     */
    Object handle(MapperRequest request, HttpResponse response) throws DocumentException, IOException;

}
