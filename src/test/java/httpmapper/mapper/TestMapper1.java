package httpmapper.mapper;

import httpmapper.httpclient.Bean;
import mrchenli.request.param.EntityType;
import mrchenli.request.param.ReqParam;
import mrchenli.request.param.Request;
import mrchenli.request.param.ThirdMapper;
import mrchenli.request.type.POST;

import java.util.List;

@ThirdMapper
public interface TestMapper1 {//可以产生一个代理对象 在执行的时候收集信息还是初始化的时候收集信息


    JsonResult home(TestBean<Bean> a);

    JsonResult home(List<Bean> a);
}
