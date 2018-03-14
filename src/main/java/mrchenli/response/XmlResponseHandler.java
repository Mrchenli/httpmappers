package mrchenli.response;

import mrchenli.request.MapperRequest;
import mrchenli.utils.Dom4jUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class XmlResponseHandler implements ResponseHandler{

    @Override
    public Object handle(MapperRequest request, HttpResponse response) throws DocumentException, IOException {
        final HttpEntity entity = response.getEntity();
        String text = EntityUtils.toString(entity);
        Class returnType = request.getMethod().getReturnType();
        Type type = request.getMethod().getGenericReturnType();
        Class genericFiled = type instanceof ParameterizedType? (Class) ((ParameterizedType) type).getActualTypeArguments()[0] :null;
        return Dom4jUtil.parseGennericResult(text,returnType,genericFiled);
    }
}
