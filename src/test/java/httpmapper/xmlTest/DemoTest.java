package httpmapper.xmlTest;

import com.alibaba.fastjson.JSONObject;
import mrchenli.utils.Dom4jUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.junit.Test;

public class DemoTest {

    @Test
    public void test12() throws DocumentException {
        String str ="<MSG>\n" +
                "\t\t\t\t\t<MSGID>uuid（32位）</MSGID>\n" +
                "\t\t\t\t\t<TOPIC>osp.office</TOPIC>\n" +
                "\t\t\t\t\t<OPERATION>CREATE</OPERATION>\n" +
                "\t\t\t\t\t<TIMESTAMP>2017-05-25 11:44:53</TIMESTAMP>\n" +
                "\t\t\t\t\t<DATA>\n" +
                "\t\t\t\t\t\t<ID>机构ID</ID>\n" +
                "\t\t\t\t\t\t<PARENTIDS>上级ID</PARENTIDS>\n" +
                "\t\t\t\t\t</DATA>\n" +
                "\t\t\t\t</MSG>";
        MSG msg = Dom4jUtil.parseResult(str,MSG.class);
        System.out.println(JSONObject.toJSONString(msg));
    }
    @Test
    public void test02() throws DocumentException {
        String str = "<table>\n" +
                "    <tr>\n" +
                "        <td>topic</td>\n" +
                "        <td colspan=\"3\">osp.office</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "\t\t\t数据格式\n" +
                "\t\t</td>\n" +
                "        <td  colspan=\"3\">\n" +
                "\t\t\t<pre>\n" +
                "\t\t\t\t<MSG>\n" +
                "\t\t\t\t\t<MSGID>uuid（32位）</MSGID>\n" +
                "\t\t\t\t\t<TOPIC>osp.office</TOPIC>\n" +
                "\t\t\t\t\t<OPERATION>CREATE</OPERATION>\n" +
                "\t\t\t\t\t<TIMESTAMP>2017-05-25 11:44:53</TIMESTAMP>\n" +
                "\t\t\t\t\t<DATA>\n" +
                "\t\t\t\t\t\t<ID>机构ID</ID>\n" +
                "\t\t\t\t\t\t<PARENTIDS>上级ID</PARENTIDS>\n" +
                "\t\t\t\t\t</DATA>\n" +
                "\t\t\t\t</MSG>\n" +
                "\t\t\t</pre>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n";
        Document document = DocumentHelper.parseText(str);
        Node node = document.selectSingleNode("//table/tr/td/pre/MSG");
        MSG ret = Dom4jUtil.parseObject(node,MSG.class);
        System.out.println(JSONObject.toJSONString(ret));
    }

}
