package mrchenli.utils;

import mrchenli.request.param.HeaderParam;
import mrchenli.request.param.UrlParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReflectUtil {

    public static void objectToMap(Map<String,Object> tempParams, Map<String,String> temHeaders, Map<String,String> tempUrlParams, Object params){
        checkNotNull(tempParams);
        for(Class<?> clazz = params.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                Field[] fields = clazz.getDeclaredFields();
                for (Field f :fields) {
                    f.setAccessible(true);
                    if(f.get(params)==null){
                        continue;
                    }

                    if(temHeaders!=null){
                        if(f.isAnnotationPresent(HeaderParam.class)){
                            if(f.get(params) instanceof String){
                                HeaderParam headerParam = f.getAnnotation(HeaderParam.class);
                                String key = headerParam.value();
                                key = StringUtil.isEmpty(key)?f.getName():key;
                                temHeaders.put(key, (String) f.get(params));
                            }
                        }
                    }

                    if(tempUrlParams!=null){
                        if(f.isAnnotationPresent(UrlParam.class)){
                            if(f.get(params) instanceof String){
                                UrlParam urlParam = f.getAnnotation(UrlParam.class);
                                String key = urlParam.value();
                                key = StringUtil.isEmpty(key)?f.getName():key;
                                tempUrlParams.put(key, (String) f.get(params));
                            }
                        }
                    }

                    if(!(f.isAnnotationPresent(UrlParam.class)||f.isAnnotationPresent(HeaderParam.class))){
                        tempParams.put(f.getName(),f.get(params));
                    }

                    f.setAccessible(false);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void objectToMap(Map<String,Object> tempParams, Object params){
       objectToMap(tempParams,null,null,params);
    }

}
