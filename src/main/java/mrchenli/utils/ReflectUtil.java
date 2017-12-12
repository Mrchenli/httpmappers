package mrchenli.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReflectUtil {

    public static void objectToMap(Map<String,Object> tempParams, Map<String,String> temHeaders, Class<? extends Annotation> annotation, Object params){
        checkNotNull(tempParams);
        for(Class<?> clazz = params.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                Field[] fields = clazz.getDeclaredFields();
                for (Field f :fields) {
                    f.setAccessible(true);
                    if(f.get(params)==null){
                        continue;
                    }
                    if(annotation!=null&&temHeaders!=null&&!temHeaders.isEmpty()){
                        if(f.isAnnotationPresent(annotation)){
                            if(f.get(params) instanceof String){
                                temHeaders.put(f.getName(), (String) f.get(params));
                            }
                        }
                    }else {
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
