package mrchenli.response;

import mrchenli.request.MapperRequest;
import mrchenli.utils.Dom4jUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class XmlResponseHandler implements ResponseHandler{
    private Logger logger = LoggerFactory.getLogger(XmlResponseHandler.class);
    @Override
    public Object handle(MapperRequest request, HttpResponse response) throws DocumentException, IOException {
        final HttpEntity entity = response.getEntity();
        String text = EntityUtils.toString(entity,"utf-8");
        logger.info("******"+request.getRequestInfo().getUrl()+"*****"+"result:result={}",text);
        Class returnType = request.getMethod().getReturnType();
        Type type = request.getMethod().getGenericReturnType();
        Class genericFiled = type instanceof ParameterizedType? (Class) ((ParameterizedType) type).getActualTypeArguments()[0] :null;
        return Dom4jUtil.parseGennericResult(text,returnType,genericFiled);
    }
}
