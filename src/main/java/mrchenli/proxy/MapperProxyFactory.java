package mrchenli.proxy;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import mrchenli.config.Configuration;
import mrchenli.crypt.des.DesService;
import mrchenli.crypt.rsa.RsaService;
import mrchenli.crypt.rsa.bean.RsaBean;
import mrchenli.crypt.rsa.bean.SignBean;
import mrchenli.handler.PostProcessor;
import mrchenli.http.executor.HttpExecutor;
import mrchenli.propertiesconfig.Config;
import mrchenli.propertiesconfig.ConfigManager;
import mrchenli.request.MapperRequest;
import mrchenli.request.param.ReqParam;
import mrchenli.request.param.RsaParam;
import mrchenli.request.param.SignParam;
import mrchenli.utils.MapperRequestKeyUtil;
import mrchenli.utils.ReflectUtil;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 这个类主要用来生成mapper接口的代理对象的 用jdk代理就好了
 */
public class MapperProxyFactory extends AbstractInvocationHandler{

    private static Logger logger = LoggerFactory.getLogger(MapperProxyFactory.class);

    private final Configuration configuration;
    private final HttpExecutor httpExecutor;

    public static <T> T newProxy(Configuration configuration,Class<T> interfaceType) {
        return Reflection.newProxy(interfaceType, new MapperProxyFactory(configuration));
    }

    public MapperProxyFactory(Configuration configuration) {
        this.configuration = configuration;
        this.httpExecutor = configuration.getHttpExecutor();
    }

    @Override
    protected Object handleInvocation(Object o, Method method, Object[] args) throws Throwable {
        try (HttpExecutor httpExecutor = this.httpExecutor){
            String mrKey = MapperRequestKeyUtil.getKey(method);
            MapperRequest mapperRequest = configuration.getMapperRequest(mrKey);
            checkNotNull(mapperRequest);
            Object params = resolveRequestParameter(mapperRequest,method,args);
            //前置操作的处理
            invokePostProcessBefore(mapperRequest,params,mapperRequest.getPostProcessors());
            HttpResponse response = httpExecutor.execute(mapperRequest,params);
            logger.info("response statusline is ==>{}",response.getStatusLine());
            if(response.getStatusLine().getStatusCode()!=200){
                throw new RuntimeException(JSONObject.toJSONString(response.getStatusLine()));
            }
            Object target =  mapperRequest.getResponseHandler().handle(mapperRequest,response);
            //执行后置的操作 然后返回
            return invokePostProcessAfter(mapperRequest,params,target,mapperRequest.getPostProcessors());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /*ok 发送rsa数据可以隐藏了 接下来就是接收rsaelse if(args.length==1){
    paramObject = args[0];}*/
    private Object resolveRequestParameter(MapperRequest request,Method method, Object[] args) {
        try {
            final Object paramObject;
            if (args == null || args.length == 0) {
                paramObject = Collections.emptyMap();
            } else {
                final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                final Map<String, Object> tmpParams = Maps.newHashMapWithExpectedSize(args.length);
                outer:
                for (int i = 0; i < parameterAnnotations.length; i++) {
                    Annotation[] annotations = parameterAnnotations[i];
                    for (Annotation annotation : annotations) {
                        if(annotation instanceof RsaParam){
                            RsaBean rsaBean = doRsa(ConfigManager.getConfig(request.getConfigKey()),args[i]);
                            String param = ((RsaParam) annotation).value();
                            tmpParams.put(param,rsaBean.getRsa_string());
                            tmpParams.put("sign",rsaBean.getSign());
                            tmpParams.put("des_key",rsaBean.getDes_key());
                            continue outer;
                        }
                        if(annotation instanceof SignParam){
                            SignBean signBean = doSign(ConfigManager.getConfig(request.getConfigKey()),args[i]);
                            String param = ((SignParam) annotation).value();
                            tmpParams.put("sign",signBean.getSign());
                            tmpParams.put(param,args[i]);
                            continue outer;
                        }
                        if (annotation instanceof ReqParam) {
                            tmpParams.put(((ReqParam) annotation).value(), args[i]);
                            continue outer;
                        }
                    }
                    ReflectUtil.objectToMap(tmpParams,args[i]);
                }
                paramObject = tmpParams;
            }
            return paramObject;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void invokePostProcessBefore(MapperRequest mapperRequest,Object objectParam, List<PostProcessor> processors){
        for (PostProcessor postProcessor:processors){
            postProcessor.handlerBefore(mapperRequest,objectParam);
        }
    }

    public Object invokePostProcessAfter(MapperRequest request,Object objectParam ,Object target, List<PostProcessor> processors){
        for (PostProcessor postProcessor:processors){
            target = postProcessor.handleAfter(request,objectParam,target);
        }
        return target;
    }

    public RsaBean doRsa(Config config,Object data){
        checkNotNull(data);
        checkNotNull(config);
        String rsaData;
        if(data instanceof String){
            rsaData = (String) data;
        }else{
            rsaData = JSONObject.toJSONString(data);
        }

        RsaService rsaService = config.getRsaService();
        DesService desService = config.getDesService();

        String desKey = desService.getRandomDesKey(desService.getKeyLength());

        String biz_data = desService.encrypt(rsaData,desKey);
        String des_key = rsaService.encrypt(desKey);
        String sign = rsaService.generateSign(biz_data);
        return new RsaBean(sign,des_key,biz_data);
    }


    public SignBean doSign(Config config, Object data){
        checkNotNull(data);
        checkNotNull(config);
        String rsaData;
        if(data instanceof String){
            rsaData = (String) data;
        }else{
            rsaData = JSONObject.toJSONString(data);
        }
        RsaService rsaService;
        //这里适配下变态的给把他们私钥给我们用来签名的
        if(config.getPrivateKey()==null){
            rsaService = config.getRsaService();
        }else{
            rsaService = config.getThirdRsaService();
        }
        String sign = rsaService.generateSign(rsaData);
        return new SignBean(sign);
    }


}
