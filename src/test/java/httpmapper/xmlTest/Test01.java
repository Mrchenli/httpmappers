package httpmapper.xmlTest;

import mrchenli.utils.StringUtil;
import org.dom4j.*;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;

public class Test01 {

    private final String xmlDefineReg = "^<\\?xml.*\\?>";
    private final String endReg = "<\\/.*>";

    private final String MH = ":";
    private final String DKH = "{";
    private final String FDKH = "}";
    @Test
    public void testXmlDefineReg(){
        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        String re = str.replaceAll(xmlDefineReg,"hello");
        System.out.println(re);
        System.out.println(str.matches(xmlDefineReg));
    }
    @Test
    public void testEndReg() throws DocumentException {
        String str = "<Head>\n" +
                " \t <ReqJnlNo>1234567891</ReqJnlNo>\n" +
                "\t <ResJnlNo></ResJnlNo>\n" +
                " \t <ResTime>2012-12-21 11:03:45</ResTime> \n" +
                " \t <ResCode>000000</ResCode> \n" +
                " \t <ResMsg> </ResMsg> \n" +
                " </Head>";
        Document document = DocumentHelper.parseText(str);
        String element = "";
        Node node = document.selectSingleNode("//Head");
        System.out.println(node.getName());
        //System.out.println(node.getStringValue());
        System.out.println();
        String value = node.valueOf("ReqJnlNo");
        System.out.println("value is ==>"+value);
    }

    public static Message xmlResponseHandler(String xml){
        if(StringUtil.isEmpty(xml)){
            return null;
        }
        //1. to json
        try {
            Document document = DocumentHelper.parseText(xml);
            Element node = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //2. to object
        return null;
    }

    /**
     * 什么东西 允许出现多少
     * 什么东西有：
     *      ` .代表任意字符
     *      ` \d代表一个数字
     * 出现多少次有：
     *      ` ? ==> 0 or 1
     *      ` + ==> >=1
     *      ` * ==> 0 or any
     *      ` {n} ==> only n
     *      ` {m,n} ==> [m.n]
     *      ` {m,}==> [m,)
     * 分组和或
     *      ` 分组==> ()
     *      ` 或 ==> |
     */
    @Test
    public void testReg(){
        String str="today is 2016-01-22,it is";
        String regex = ".*\\d{4}-\\d{2}-\\d{2}.*";
        String result = str.replaceAll(regex,"");
        System.out.println("result is ==>"+result);
    }

    public static void main(String[] args) throws NoSuchFieldException {
        ParameterizedType pt = (ParameterizedType) Head.class.getDeclaredField("list").getGenericType();
        Class clz = (Class) pt.getActualTypeArguments()[0];
        String str = pt.getActualTypeArguments()[0].getTypeName();
        System.out.println(clz.getName());
        System.out.println(str);
    }

    @Test
    public void testMessage(){

        Head head = new Head();
        head.setReqJnlNo("111");
        head.setResCode("400");
        head.setResJnlNo("12142");
        head.setResMsg("1111");
        head.setResTime("2018-3-2 10:00:00");
    }



}
