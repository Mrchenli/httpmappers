package mrchenli.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectFieldSortUtil {

    private static Logger logger = LoggerFactory.getLogger(ObjectFieldSortUtil.class);
    private static final String markReg = "^[_a-zA-Z]+[0-9]+$";
    private static final String markNo = "^[_a-zA-Z]+";

    public static String getSignString(Object target){
        try {
            List<Field> list = new ArrayList<>();
            for (Class<?> clzz = target.getClass();clzz!=Object.class;clzz=clzz.getSuperclass()){
                for (Field f:clzz.getDeclaredFields()){
                    if(f.isAnnotationPresent(IgnoreField.class)){
                        continue;
                    }
                    list.add(f);
                }
            }
            Collections.sort(list, (o1, o2) -> {
                String o1Name = o1.getName();
                String o2Name = o2.getName();
                o1Name  = StringUtil.upperFirst(o1Name);
                o2Name = StringUtil.upperFirst(o2Name);
                if(o1Name.matches(markReg)&&o2Name.matches(markReg)){
                    Pattern p = Pattern.compile(markNo);
                    Matcher m1 = p.matcher(o1Name);
                    Matcher m2 = p.matcher(o2Name);
                    String no1 = m1.replaceAll("").trim();
                    String no2 = m2.replaceAll("").trim();
                    int o1Int = Integer.parseInt(no1);
                    int o2Int = Integer.parseInt(no2);
                    return o1Int-o2Int;
                }
                return o1Name.compareTo(o2Name);
            });
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<list.size();i++){
                Field f = list.get(i);
                f.setAccessible(true);
                if(i==list.size()-1){
                    sb.append(f.getName()+"="+f.get(target));
                }else{
                    sb.append(f.getName()+"="+f.get(target)+"&");
                }
                f.setAccessible(false);
            }
            String ret  =  sb.toString();
            logger.info("sign str is ==>{}",ret);
            return ret;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


}
