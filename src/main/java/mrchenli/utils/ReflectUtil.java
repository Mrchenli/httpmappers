package mrchenli.utils;

import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReflectUtil {

    public static void objectToMap(Map<String,Object> tempParams,Object params){
        checkNotNull(tempParams);
        for(Class<?> clazz = params.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                Field[] fields = clazz.getDeclaredFields();
                for (Field f :fields) {
                    f.setAccessible(true);
                    if(f.get(params)==null){
                        continue;
                    }
                    tempParams.put(f.getName(),f.get(params));
                    f.setAccessible(false);
                }
            } catch (Exception e) {
            }
        }
    }

}
