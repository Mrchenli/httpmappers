package httpmapper.reflect;

import httpmapper.httpclient.RsaModel;
import httpmapper.mapper.TestBean;
import httpmapper.mapper.TestMapper;
import httpmapper.mapper.TestMapper1;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestMethod {
    /**
     * 测试放回值里面的泛型信息
     */
    @Test
    public void returnTypeGeneric(){
        Type retType = TestMapper.class.getMethods()[0].getReturnType();
        System.out.println(retType.getTypeName());
        System.out.println();

    }
    @Test
    public void argGeneric(){
        Type[] retType = TestMapper1.class.getMethods()[0].getGenericParameterTypes();
        for(Type t:retType){
            if((t instanceof ParameterizedType)){
                System.out.println("yesw");
            }
            System.out.println(t.getTypeName());
        }
    }

    @Test
    public void testColec(){
        Type[] retType = TestMapper1.class.getMethods()[1].getGenericParameterTypes();
        for(Type t:retType){
            if((t instanceof ParameterizedType)){
                System.out.println("yesw");
                Type type= ((ParameterizedType) t).getRawType();
                if(t.equals(List.class)||t.equals(Map.class)){
                    System.out.println("true");
                }
            }
            System.out.println(t.getTypeName());
        }
    }

    @Test
    public void testsss(){
        RsaModel<TestBean> rsaModel = new RsaModel<>();
        RsaModel rsa = rsaModel;
        Type type = rsa.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        Type  c =pt.getActualTypeArguments()[0];
        System.out.println(c.getTypeName());

    }


}
