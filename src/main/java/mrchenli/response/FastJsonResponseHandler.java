package mrchenli.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import mrchenli.crypt.des.DesService;
import mrchenli.crypt.rsa.JsonPath;
import mrchenli.crypt.rsa.RsaService;
import mrchenli.crypt.rsa.bean.RsaBean;
import mrchenli.crypt.rsa.bean.SignBean;
import mrchenli.crypt.rsa.exception.SignException;
import mrchenli.propertiesconfig.Config;
import mrchenli.propertiesconfig.ConfigManager;
import mrchenli.request.MapperRequest;
import mrchenli.utils.JsonPathUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author gaohang on 8/11/17.
 */
public class FastJsonResponseHandler implements ResponseHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonResponseHandler.class);

  //这里rsa 就结束掉了
  @Override
  public Object handle(MapperRequest request, HttpResponse response) {
    String text;
    final HttpEntity entity = response.getEntity();
    try {
      text =  EntityUtils.toString(entity);
      LOGGER.info("request result: request={}, result={}", request, text);
      if(text.indexOf("sign")!=-1){
        Object obj = handleRsa(request,text);
        if(obj == null){
          handleSign(request,text);
        }else{
          text = JSONObject.toJSONString(obj);
        }
      }
      //如果"data":"{"code":"1","desc":"可申请，新用户"}"==>"data":{"code":"1","desc":"可申请，新用户"}
      text = removeDataString(text,request.getResultJsonPath());
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


  private String removeDataString(String text ,String jsonPath){
    JSONObject data = JSONObject.parseObject(text);
    Object tar = JSONPath.eval(data,jsonPath);
    if(tar instanceof String){
      String str = (String) tar;
      str = str.substring(str.indexOf("{"),str.lastIndexOf("}")+1);
      JSONObject temp = JSONObject.parseObject(str);
      JsonPathUtil.putObject(data,jsonPath,temp);
    }
    return data.toJSONString();
  }


  public Object handleRsa(MapperRequest request,String text){
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


  public void handleSign(MapperRequest request,String text){
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
