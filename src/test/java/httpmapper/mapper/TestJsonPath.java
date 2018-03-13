package httpmapper.mapper;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import httpmapper.httpclient.Bean;
import lombok.Data;
import org.junit.Test;

public class TestJsonPath {

    @Test
    public void test(){
        Outer out = new Outer();
        Result in = new Result();
        in.setCode("1");
        in.setData(new Bean("mrchenli"));
        in.setReason("why");
        out.setReason("what");
        out.setCode("1");
        out.setData(in);
        String str = JSONObject.toJSONString(out);
        JSONObject jsonObject = JSONObject.parseObject(str);
        Object o = JSONPath.eval(jsonObject,"$.or[1]");
        System.out.println(JSONObject.toJSONString(o));
    }

    @Data
    private static class Outer{
        private String code;

        private String reason;

        private Result data;
    }
    @Data
    private static class Result{
        private String code;

        private String reason;

        private Bean data;
    }

}
