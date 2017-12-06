package httpmapper.httpclient;

import com.alibaba.fastjson.JSONObject;
import httpmapper.mapper.JsonResult;
import httpmapper.mapper.TestBean;
import httpmapper.mapper.TestMapper;
import mrchenli.config.Configuration;
import mrchenli.request.MapperRequest;
import mrchenli.response.ResponseHandler;
import mrchenli.response.FastJsonResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;

public class Test01 {

    @Test
    public void testHttpEntity() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/home/index.json");
        System.out.println("executing request "+httpGet.getURI());
        CloseableHttpResponse response = httpclient.execute(httpGet);
       /* HttpEntity entity = response.getEntity();
        System.out.println("---------------------");
        System.out.println(response.getStatusLine());
        if(entity!=null){
            System.out.println("Response content length: " + entity.getContentLength());
            // 打印响应内容
            System.out.println("Response content: " + EntityUtils.toString(entity));
        }
        System.out.println("------------------------------------");
*/
        ResponseHandler responseHandler = new FastJsonResponseHandler();
        MapperRequest request = new MapperRequest();
        JsonResult<TestBean> jsonResult = new JsonResult<>();
        request.setReturnType(jsonResult.getClass().getGenericSuperclass());
        Object o = responseHandler.handle(request,response);
        System.out.println(JSONObject.toJSONString(o));
    }

    @Test
    public void testType(){
        Type type = int.class.getGenericSuperclass();
        System.out.println("int type == "+type);
        Type type1 = Integer.class.getGenericSuperclass();
        System.out.println("type1 is =="+type1);
        System.out.println(type1 instanceof Class);
    }
    @Test
    public void test(){
        Configuration configuration  = Configuration.newBuilder().setScanPath("httpmapper.mapper").build();
        TestMapper testMapper = configuration.newMapper(TestMapper.class);
        TestBean testBean = new TestBean();
        testBean.setAge("18");
        testBean.setName("mrchenli");
        JsonResult<TestBean> obj = testMapper.home(testBean);
        System.out.println(JSONObject.toJSONString(obj));
    }

    @Test
    public void testsssss(){
        Configuration configuration  = Configuration.newBuilder().setScanPath("httpmapper.mapper").build();
        TestMapper testMapper = configuration.newMapper(TestMapper.class);
        JsonResult<TestBean> obj = testMapper.home("mrchenli","18");
        TestBean testBean = obj.getPayload();
        System.out.println(JSONObject.toJSONString(obj));
    }

}
