package httpmapper.xmlTest;

import com.alibaba.fastjson.JSONObject;
import mrchenli.utils.Dom4jUtil;
import org.dom4j.*;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Dom4jUtilTest<T> {

    private Message<Out> message;
    private String test;
    private T t;
    @Test
    public void testParseObject() throws DocumentException {
        String str = "<Head>\n" +
                " \t <ReqJnlNo>1234567891</ReqJnlNo>\n" +
                "\t <ResJnlNo></ResJnlNo>\n" +
                " \t <ResTime>2012-12-21 11:03:45</ResTime> \n" +
                " \t <ResCode>000000</ResCode> \n" +
                " \t <ResMsg> </ResMsg> \n" +
                " </Head>";
        Head head = Dom4jUtil.parseResult(str,Head.class);
        System.out.println(JSONObject.toJSONString(head));
    }

    @Test
    public void testMessage() throws DocumentException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        String message = "<Message>\n" +
                "<Head>\n" +
                "<ReqJnlNo>1234567891</ReqJnlNo>\n" +
                "<ResJnlNo></ResJnlNo>\n" +
                "<ResTime>2012-12-21 11:03:45</ResTime> \n" +
                "<ResCode>000000</ResC" +
                "ode> \n" +
                "<ResMsg> </ResMsg> \n" +
                "</Head>\n" +
                "<Body>\n" +
                "<CoPatrnerJnlNo>2015090116240003627893</CoPatrnerJnlNo>" +
                "<TrsState>0</TrsState>" +
                "<Reserve1>111</Reserve1>" +
                "<Reserve2></Reserve2>" +
                "<Reserve3></Reserve3>" +
                "<Reserve4></Reserve4>" +
                "<Reserve5></Reserve5>" +
                "</Body>" +
                "</Message>";
      Message message1 = Dom4jUtil.parseGennericResult(message,Message.class,Out.class);
      System.out.println(JSONObject.toJSONString(message1));
    }

    @Test
    public void test() throws NoSuchFieldException, NoSuchMethodException {
        Method method = Dom4jUtilTest.class.getDeclaredMethod("getMessage");
        Field f = Dom4jUtilTest.class.getDeclaredField("t");
        Type type = f.getGenericType();
        System.out.println(type.getTypeName());
        Type cl = method.getGenericReturnType();
        Class cs = method.getReturnType();
        String s = cs.getTypeParameters()[0].getName();
        System.out.println(s);
        if(cl instanceof ParameterizedType){
            System.out.println("ParameterizedType");
            Class c = (Class) ((ParameterizedType) cl).getActualTypeArguments()[0];
            System.out.println(c.getName());
        }
        System.out.println(cl.getTypeName());
    }

    public Message<Out> getMessage(){
        return null;
    }



}
