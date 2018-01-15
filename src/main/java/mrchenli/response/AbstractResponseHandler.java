package mrchenli.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import mrchenli.crypt.des.DesService;
import mrchenli.crypt.rsa.RsaService;
import mrchenli.crypt.rsa.bean.RsaBean;
import mrchenli.crypt.rsa.bean.SignBean;
import mrchenli.crypt.rsa.exception.SignException;
import mrchenli.propertiesconfig.Config;
import mrchenli.propertiesconfig.ConfigManager;
import mrchenli.request.MapperRequest;
import mrchenli.utils.JsonPathUtil;
import mrchenli.utils.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractResponseHandler implements ResponseHandler{

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public Object handle(MapperRequest request, HttpResponse response) {
        String text;
        final HttpEntity entity = response.getEntity();
        try {
            text =  EntityUtils.toString(entity,"utf-8");
            LOGGER.info("result:result={}",text);
            if(text.indexOf("sign")!=-1){
                Object obj = handleRsa(request,text);
                if(obj == null){
                    handleSign(request,text);
                }else{
                    text = JSONObject.toJSONString(obj);
                }
            }

            text = removeDataString(text,request.getResultJsonPath());
            //这个用来针对 fed 400的时候是业务错误
            text = convertTextToWeWant(text, response.getStatusLine().getStatusCode());

            /*
             *从响应头里面读取一些数据出来
             */
            text = readFromResponseHeaders(text,response);

            final Type returnType = request.getReturnType();
            if (returnType instanceof Class) {
                return JSON.parseObject(text, (Class<? extends Object>) returnType);
            }
            if(returnType instanceof ParameterizedType){
                return JSONObject.parseObject(text,returnType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 从headers 里面获取一些数据的时候 默认是不用加的
     * @param text
     * @param response
     * @return
     */
    public String readFromResponseHeaders(String text,HttpResponse response){
        return text;
    }


    public abstract String convertTextToWeWant(String text,Integer code);

    protected String removeDataString(String text ,String jsonPath){
        JSONObject data;
        try {
            data = JSONObject.parseObject(text);
        }catch (Exception e){
            LOGGER.warn("removing data str error text is ==>{}",text);
            return text;
        }
        if(StringUtil.isEmpty(jsonPath)){
            return text;
        }
        Object tar = JSONPath.eval(data,jsonPath);
        if(tar instanceof String){
            String str = (String) tar;
            str = str.substring(str.indexOf("{"),str.lastIndexOf("}")+1);
            JSONObject temp = JSONObject.parseObject(str);
            JsonPathUtil.putObject(data,jsonPath,temp);
        }
        return data.toJSONString();
    }


    protected Object handleRsa(MapperRequest request, String text){
        try {
            RsaBean rsaBean = JSON.parseObject(text, RsaBean.class);
            if(rsaBean==null||rsaBean.getDes_key()==null||rsaBean.getDes_key().trim().length()==0){
                return null;
            }
            Config config = ConfigManager.getConfig(request.getConfigKey());
            RsaService rsaService = config.getRsaService();
            DesService desService = config.getDesService();
            String des_key = rsaBean.getDes_key();
            String sign = rsaBean.getSign();
            JSONObject data = JSON.parseObject(text);

            Object o = JSONPath.eval(data,request.getResultJsonPath());
            boolean isString = o instanceof String;
            String biz_data = isString? (String) o :JSONObject.toJSONString(o);

            boolean flag = rsaService.verifySign(sign,biz_data);
            if(!flag) throw new SignException("签名认证失败");
            String desKey = rsaService.decrypt(des_key);
            String bizData = desService.decrypt(biz_data,desKey);

            boolean isStr =  (bizData.indexOf('{')!=-1)&&(bizData.indexOf('}')!=-1);
            if(isStr){
                JsonPathUtil.putObject(data,request.getResultJsonPath(),bizData);
            }else {
                JsonPathUtil.putObject(data,request.getResultJsonPath(),JSON.parse(bizData));
            }
            return data;
        }catch (Exception e){
            if(e instanceof SignException){
                LOGGER.error("httpmapper rsa decrypt sign failed m==>{}, e==>{}",e.getMessage(),e);
            }
            LOGGER.error("httpmapper rsa decrypt failed e==>{}",e);
        }
        return null;
    }


    protected void handleSign(MapperRequest request,String text){
        try {
            SignBean signBean = JSON.parseObject(text, SignBean.class);
            if(signBean==null||signBean.getSign()==null||signBean.getSign().trim().length()==0){
                LOGGER.debug("no rsa and no sign both ");
                return;
            }
            Config config = ConfigManager.getConfig(request.getConfigKey());
            RsaService rsaService = config.getRsaService();
            String sign  = signBean.getSign();
            JSONObject data = JSON.parseObject(text);
            Object o = JSONPath.eval(data,request.getResultJsonPath());
            boolean isString = o instanceof String;
            String biz_data = isString? (String) o :JSONObject.toJSONString(o);
            boolean flag = rsaService.verifySign(sign,biz_data);
            if(!flag) throw new SignException("签名认证失败");
            LOGGER.debug("only sign  decrypt success");
        }catch (Exception e){
            if(e instanceof SignException){
                LOGGER.error("httpmapper rsa decrypt sign failed m==>{}, e==>{}",e.getMessage(),e);
            }
            LOGGER.error("httpmapper only sign decrypt failed e==>{}",e);
        }
    }

}
