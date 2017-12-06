package httpmapper.mapper;

import httpmapper.httpclient.Bean;
import mrchenli.request.param.*;
import mrchenli.request.type.POST;

/**
 * 只支持String 和 自己定义的object
 */
@ThirdMapper
public interface TestMapper {//可以产生一个代理对象 在执行的时候收集信息还是初始化的时候收集信息

    @Request("http://localhost:8080/home/index.json")
    @POST(entity = EntityType.FORM)
    JsonResult<TestBean> home(TestBean testBean);
    JsonResult<TestBean<Bean>> home();

    @Request("http://localhost:8080/home/index.json")
    @POST(entity = EntityType.FORM)
    JsonResult<TestBean> home(@ReqParam("name") String name,@ReqParam("age") String age);

    JsonResult home(String a);

    TestBean<Bean> homes(@RsaParam("data") TestBean testBean,String name);




}
