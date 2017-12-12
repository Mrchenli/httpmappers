package mrchenli.http.executor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import mrchenli.http.httpclient.HttpClientFactory;
import mrchenli.propertiesconfig.Config;
import mrchenli.propertiesconfig.ConfigManager;
import mrchenli.request.MapperRequest;
import mrchenli.request.param.EntityType;
import mrchenli.utils.ReflectUtil;
import mrchenli.utils.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultHttpExecutor implements HttpExecutor,AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpExecutor.class);

    private HttpClient httpClient;

    public DefaultHttpExecutor(HttpClientFactory factory) {
        this.httpClient =  factory.create();
    }

    /**
     * @param request mapperRequest params
     * @param paramsObject
     * @return
     */
    @Override
    public HttpResponse execute(MapperRequest request, Object paramsObject,Map<String,String> headers) {
        try {
            checkNotNull(request);

            /**
             * 如果是配置文件里面的话
             */
            String url = request.getRequestInfo().getUrl();
            if(!StringUtil.isEmpty(url)&&!url.startsWith("http")){
                Config config = ConfigManager.getConfig(request.getConfigKey());
                url = config.getHttpMapperPropertiesUtil().getValue(url);
                if(url.startsWith("\"")){
                    url = url.trim().substring(1,url.length()-1);
                }
                request.getRequestInfo().setUrl(url);
            }

            HttpUriRequest httpUriRequest = buildHttpRequest(request,paramsObject);
            if(headers!=null){
                for (Map.Entry<String,String> entry :headers.entrySet()) {
                    httpUriRequest.setHeader(entry.getKey(),entry.getValue());
                }
            }
            LOGGER.info("paramsObject is ==>{}",JSONObject.toJSONString(paramsObject));
            LOGGER.info("execute http request:mapperRequest={}, httpUriRequest={}", request, JSONObject.toJSONString(httpUriRequest));
            HttpResponse response = httpClient.execute(httpUriRequest);
            return response;
        }catch (Exception e){
            LOGGER.error("http request error e==>{}",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if(this.httpClient instanceof Closeable){
            ((Closeable) this.httpClient).close();
        }
    }

    /**
     * 根据注解配置的请求
     * @param request
     * @param params
     * @return
     */
    private HttpUriRequest buildHttpRequest(MapperRequest request,Object params){
        try {
            switch (request.getHttpMethod()){

                case GET:{
                    final HttpGet httpGet = new HttpGet();
                    httpGet.setURI(new URI(request.getRequestInfo().getUrl()));
                    return httpGet;
                }

                case POST:{
                    final HttpPost httpPost = new HttpPost();
                    httpPost.setEntity(createHttpEntity(params,request.getEntityType()));
                    httpPost.setURI(new URI(request.getRequestInfo().getUrl()));
                    return httpPost;
                }
                default:
                    throw new UnsupportedOperationException("不支持的http method :"+request.getHttpMethod());
            }
        }catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpEntity createHttpEntity(Object params ,EntityType entityType) throws IllegalAccessException {
        switch (entityType){
            case JSON_STRING:
                try {
                    return new StringEntity(JSON.toJSONString(params));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            case FORM:
            default:
                Map<String,Object> par;
                if(params instanceof Map){
                    par = (Map<String, Object>) params;
                }else{
                    par = Maps.newHashMap();
                    ReflectUtil.objectToMap(par,params);
                }
                return new UrlEncodedFormEntity(
                        Iterables.transform(par.entrySet(),
                        (Function<Map.Entry<String, Object>, NameValuePair>) en -> new BasicNameValuePair(en.getKey(), String.valueOf(en.getValue())))
                );
        }

    }

}
