package mrchenli.utils;

import com.alibaba.fastjson.JSONObject;

public class JsonPathUtil {


    public static void putObject(JSONObject jsonObject,String jsonpath,Object data){
        if (!StringUtil.isEmpty(jsonpath)){
            jsonObject.put(jsonpath.replace("$.","").trim(),data);
        }
    }

}
