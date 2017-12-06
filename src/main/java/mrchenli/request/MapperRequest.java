package mrchenli.request;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.ToString;
import mrchenli.handler.PostProcessor;
import mrchenli.propertiesconfig.Config;
import mrchenli.request.param.EntityType;
import mrchenli.request.param.RequestInfo;
import mrchenli.request.type.HttpMethod;
import mrchenli.response.ResponseHandler;
import java.lang.reflect.Type;
import java.util.List;


@Data
@ToString
public class MapperRequest {

    private String configKey;
    private String resultJsonPath;
    private HttpMethod httpMethod;//要执行的方法 get post//这个是必须的
    private EntityType entityType;//row 还是jsonstring //这个post的时候是必须的
    private RequestInfo requestInfo;//这个是必须的
    //返回值得类型
    private Type returnType;
    private ResponseHandler responseHandler;//解析返回值得 序列化 json String什么的
    private List<PostProcessor> postProcessors =Lists.newArrayList() ;//返回值得拦截器列表 //这个是可选的

}
