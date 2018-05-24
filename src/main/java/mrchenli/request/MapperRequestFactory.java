package mrchenli.request;

import com.google.common.collect.Maps;
import mrchenli.crypt.rsa.JsonPath;
import mrchenli.handler.PostProcess;
import mrchenli.handler.PostProcessor;
import mrchenli.request.param.Request;
import mrchenli.request.param.RequestInfo;
import mrchenli.request.param.ThirdMapper;
import mrchenli.request.type.GET;
import mrchenli.request.type.HttpMethod;
import mrchenli.request.type.POST;
import mrchenli.response.Response;
import mrchenli.response.FastJsonResponseHandler;
import mrchenli.utils.MapperRequestKeyUtil;
import mrchenli.utils.StringUtil;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 初始化一个MapperRequest的容器出来呀
 */
public class MapperRequestFactory {

    private Map<String,MapperRequest> mapperRequests = Maps.newHashMap();
    private Set<Class<?>>  thirdmappers ;

    public Set<Class<?>>  getThirdmappers() {
        return thirdmappers;
    }

    public MapperRequestFactory(String... classPath) {
        for (String path:classPath) {
            init(path);
        }
    }

    public void init(String classPath){
        try {
            Reflections reflections = new Reflections(classPath);
            Set<Class<?>> thirdMappers = reflections.getTypesAnnotatedWith(ThirdMapper.class);
            this.thirdmappers = thirdMappers;
            for (Class<?> thirdMapper:thirdMappers) {
                ThirdMapper annotation = thirdMapper.getAnnotation(ThirdMapper.class);
                Method[] methods = thirdMapper.getDeclaredMethods();
                for (Method method:methods) {
                    if(!method.isAnnotationPresent(Request.class)){
                        continue;
                    }
                    MapperRequest mapperRequest = parseMethodToMapperRequest(method);
                    checkNotNull(mapperRequest);
                    mapperRequest.setConfigKey(annotation.value().getCanonicalName());
                    String key = MapperRequestKeyUtil.getKey(method);
                    checkNotNull(key);
                    mapperRequests.put(key,mapperRequest);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("mapperFactory 初始化异常",e);
        }
    }



    private MapperRequest parseMethodToMapperRequest(Method method) throws IllegalAccessException, InstantiationException {
        MapperRequest mapperRequest = new MapperRequest();
        //1.收集request 的url
        RequestInfo requestInfo = new RequestInfo();
        //解析@Request
        Request request = method.getAnnotation(Request.class);
        requestInfo.setUrl(request.value());
        requestInfo.setUrlCharset(request.urlCharset());
        requestInfo.setTimeOut(request.timeout());
        if(StringUtil.isEmpty(request.desc())){
            String desc = method.getDeclaringClass().getSimpleName()+method.getName();
            requestInfo.setDesc(desc);
        }else{
            requestInfo.setDesc(request.desc());
        }

        mapperRequest.setRequestInfo(requestInfo);

        //解析其他
        if(method.isAnnotationPresent(GET.class)){
            mapperRequest.setHttpMethod(HttpMethod.GET);
        }

        if(method.isAnnotationPresent(POST.class)){
            mapperRequest.setHttpMethod(HttpMethod.POST);
            POST post = method.getAnnotation(POST.class);
            mapperRequest.setEntityType(post.entity());
        }

        if(method.isAnnotationPresent(JsonPath.class)){
            mapperRequest.setResultJsonPath(method.getAnnotation(JsonPath.class).value());
        }else if(method.getDeclaringClass().isAnnotationPresent(JsonPath.class)){
            mapperRequest.setResultJsonPath(method.getDeclaringClass().getAnnotation(JsonPath.class).value());
        }else{
            mapperRequest.setResultJsonPath(null);
        }
        //解析序列化
        if(method.isAnnotationPresent(Response.class)){
            Response response = method.getAnnotation(Response.class);
            mapperRequest.setResponseHandler(response.value().newInstance());
        }else{
            mapperRequest.setResponseHandler(new FastJsonResponseHandler());
        }

        //解析拦截器了
        if(method.isAnnotationPresent(PostProcess.class)){
            Class<? extends PostProcessor>[] clzzs = method.getAnnotation(PostProcess.class).value();
            for (Class<? extends PostProcessor> clzz: clzzs) {
                mapperRequest.getPostProcessors().add(clzz.newInstance());
            }
        }
        //返回值类型
        mapperRequest.setReturnType(method.getGenericReturnType());
        mapperRequest.setMethod(method);
        return mapperRequest;
    }


    public MapperRequest getMapperRequest(String key){
        return mapperRequests.get(key);
    }

}
