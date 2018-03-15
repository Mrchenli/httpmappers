package mrchenli.http.executor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import mrchenli.handler.PostProcessor;
import mrchenli.http.httpclient.HttpClientFactory;
import mrchenli.propertiesconfig.Config;
import mrchenli.propertiesconfig.ConfigManager;
import mrchenli.request.MapperRequest;
import mrchenli.request.param.EntityType;
import mrchenli.request.param.HttpRequestBean;
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
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultHttpExecutor implements HttpExecutor,AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpExecutor.class);

    private HttpClient httpClient;

    public DefaultHttpExecutor(HttpClientFactory factory) {
        this.httpClient =  factory.create();
    }


    public void invokePostProcessBefore(Map<String,String> headers,Map<String,Object> params,Map<String,String> urlParams, List<PostProcessor> processors){
        for (PostProcessor postProcessor:processors){
            postProcessor.handlerBefore(headers,params,urlParams);
        }
    }

    /**
     * @param request mapperRequest params
     * @param httpRequestBean
     * @return
     */
    @Override
    public HttpResponse execute(MapperRequest request,HttpRequestBean httpRequestBean) {
        try {
            checkNotNull(request);

            Map<String,String> headers = httpRequestBean.getHeaders();
            Object paramsObject = httpRequestBean.getParam();
            Map<String,Object> tempParam = Maps.newHashMap();
            if(paramsObject instanceof Map){
                tempParam = (Map<String, Object>) paramsObject;
            }else{
                tempParam = Maps.newHashMap();
                ReflectUtil.objectToMap(tempParam,paramsObject);
            }
            invokePostProcessBefore(headers,tempParam,httpRequestBean.getUrlParams(),request.getPostProcessors());
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
                Map<String,String> urlParams = httpRequestBean.getUrlParams();
                if(urlParams!=null&&!urlParams.isEmpty()){
                    for (Map.Entry<String,String> entry : urlParams.entrySet()){
                        if(entry.getKey().equals("urlEncode")){
                            continue;
                        }
                        String name = "{"+entry.getKey()+"}";
                        String value = entry.getValue();
                        if(url.contains(name)){
                            url = url.replace(name,value);
                        }else {
                            url=url + "&"+entry.getKey()+"="+entry.getValue();
                        }
                    }
                }
                if(urlParams.containsKey("urlEncode")){
                    String[] us = url.split("\\?");
                    String l = URLEncoder.encode(us[0],"utf-8");
                    char c = 63;
                    url = us[0]+c+l;
                }
                //request.getRequestInfo().setUrl(url);
            }
            HttpUriRequest httpUriRequest = buildHttpRequest(url,request,tempParam);
            if(headers!=null&&!headers.isEmpty()){
                for (Map.Entry<String,String> entry :headers.entrySet()) {
                    httpUriRequest.setHeader(entry.getKey(),entry.getValue());
                }
            }
            LOGGER.info("httpRequestBean is ==>{}",JSONObject.toJSONString(httpRequestBean));
            LOGGER.info("*******"+request.getRequestInfo().getUrl()+"******* execute http request:mapperRequest={}, httpUriRequest={}", request, JSONObject.toJSONString(httpUriRequest));
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
    private HttpUriRequest buildHttpRequest(String url ,MapperRequest request,Map<String,Object> params){
        try {
            switch (request.getHttpMethod()){

                case GET:{
                    final HttpGet httpGet = new HttpGet();
                    httpGet.setURI(new URI(url));
                    httpGet.setHeader("Content-Encoding",request.getRequestInfo().getUrlCharset());
                    return httpGet;
                }

                case POST:{
                    final HttpPost httpPost = new HttpPost();
                    httpPost.setEntity(createHttpEntity(params,request.getEntityType()));
                    httpPost.setURI(new URI(url));
                    httpPost.setHeader("Content-Encoding",request.getRequestInfo().getUrlCharset());
                    return httpPost;
                }
                default:
                    throw new UnsupportedOperationException("不支持的http method :"+request.getHttpMethod());
            }
        }catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpEntity createHttpEntity(Map<String,Object> params ,EntityType entityType) throws IllegalAccessException, UnsupportedEncodingException {
        switch (entityType){
            case JSON_STRING:
                return new StringEntity(JSON.toJSONString(params),Charset.forName("utf-8"));
            case SERIALIZER:
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String,Object> entry:params.entrySet()){
                    sb.append(entry.getKey()+"="+JSONObject.toJSONString(entry.getValue())+"&");
                }
                String result = sb.toString();
                result = result.substring(0,result.length()-1);
                return new StringEntity(result,"utf-8");
            case FORM:
            default:
                return new UrlEncodedFormEntity(
                         Iterables.transform(params.entrySet(), (Function<Map.Entry<String, Object>, NameValuePair>) en -> new BasicNameValuePair(en.getKey(), String.valueOf(en.getValue())))
                , Charset.forName("utf-8"));
               /* return new UrlEncodedFormEntity(
                        Iterables.transform(params.entrySet(), (Function<Map.Entry<String, Object>, NameValuePair>) en -> new BasicNameValuePair(en.getKey(), String.valueOf(en.getValue())))
                        );*/
        }

    }
}
