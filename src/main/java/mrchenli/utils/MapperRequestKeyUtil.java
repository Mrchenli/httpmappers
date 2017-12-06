package mrchenli.utils;

import java.lang.reflect.Method;

public class MapperRequestKeyUtil {

    public static String getKey(Method method){
        return method.getDeclaringClass().getCanonicalName()+"."+method.getName();
    }

}
