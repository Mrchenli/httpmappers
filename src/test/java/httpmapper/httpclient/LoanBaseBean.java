package httpmapper.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.FieldInfo;
import httpmapper.mapper.JsonResult;
import httpmapper.mapper.TestBean;
import httpmapper.mapper.TestMapper;
import httpmapper.mapper.TestMapper1;
import lombok.Data;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
public class LoanBaseBean<T> extends RsaModel<T> {
    private String method;

    public static void main(String[] args) {
        JsonResult<TestBean<Bean>> jr = new JsonResult<>();
        jr.setCode("1");
        TestBean testBean = new TestBean();
        testBean.setName("aaa");
        testBean.setAge("18");
        testBean.setData(new Bean("bean"));
        jr.setPayload(testBean);
        String jrstr = JSONObject.toJSONString(jr);
        System.out.println(jrstr);
        Method clzz = TestMapper.class.getDeclaredMethods()[0];
        Type type = TestMapper.class.getDeclaredMethods()[0].getGenericReturnType();
        System.out.println(type.getClass().getName());
        System.out.println(type.getTypeName());
        //Type filedtype = jr.getClass().
        System.out.println("type instance of class ======"+(type instanceof Class));
        ParameterizedType parameterizedType = (ParameterizedType) type;
        //FieldInfo.getFieldType(jr.getClass(),type,)
        final Type type1 = parameterizedType.getActualTypeArguments()[0];
        System.out.println(type1.getTypeName());
        JsonResult<TestBean<Bean>> newJr = JSONObject.parseObject(jrstr,type);
        System.out.println(newJr.getPayload().getName());
        System.out.println(newJr.getPayload().getData().getBean());
        System.out.println(type.getTypeName());

      /*  LoanBaseBean<TestBean> test  = new LoanBaseBean<>();
        String str = JSONObject.toJSONString(test);
        LoanBaseBean loanBaseBean =  JSON.parseObject(str,test.getClass().getGenericSuperclass());
        TestBean testBeans = (TestBean) loanBaseBean.getData();
        System.out.println(JSONObject.toJSONString(testBean));*/
    }

    @Test
    public void test02(){
        Method method = TestMapper.class.getDeclaredMethods()[1];
        System.out.println(method.getGenericReturnType().getTypeName());
        System.out.println(method.getGenericReturnType() instanceof ParameterizedType);
        System.out.println(method.getGenericReturnType() instanceof Class);
        System.out.println(method.getReturnType().getTypeName());
    }



    @Test
    public void test03(){
        Method method = TestMapper1.class.getDeclaredMethods()[0];
        System.out.println(method.getGenericReturnType().getTypeName());
        System.out.println(method.getGenericReturnType() instanceof ParameterizedType);
        System.out.println(method.getGenericReturnType() instanceof Class);
        System.out.println(method.getReturnType().getTypeName());
    }
}
