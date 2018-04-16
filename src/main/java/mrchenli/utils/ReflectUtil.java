package mrchenli.utils;

import mrchenli.request.param.HeaderParam;
import mrchenli.request.param.UrlParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
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
                      /*if(f.isAnnotationPresent(IgnoreField.class)){
                        continue;
                    }*/
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
                    if(f.isAnnotationPresent(IgnoreField.class)){
                        continue;
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


    public static boolean isMapOrCollection(Class clzz){
        return Map.class.isAssignableFrom(clzz)|| Collection.class.isAssignableFrom(clzz);
    }

    public static boolean isStringType(Type type){
        return String.class.getTypeName().equals(type.getTypeName());
    }


    public static boolean isString(Class clzz){
        return String.class.isAssignableFrom(clzz);
    }


    public static boolean isWrapClass(Class clzz){
        try {
            return ((Class) clzz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPrimitive(Class clzz){
        return int.class.isAssignableFrom(clzz)||
                double.class.isAssignableFrom(clzz)||
                boolean.class.isAssignableFrom(clzz)||
                long.class.isAssignableFrom(clzz);
    }

    public static boolean isDate(Class clzz){
        return Date.class.isAssignableFrom(clzz);
    }

    public static boolean isObjValue(Class clzz){
        return !isString(clzz)||!isPrimitive(clzz)||!isWrapClass(clzz)||!isMapOrCollection(clzz)||!isDate(clzz);
    }

}
