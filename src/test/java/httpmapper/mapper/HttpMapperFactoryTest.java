package httpmapper.mapper;

import mrchenli.HttpMapperFactory;
import org.junit.Test;

public class HttpMapperFactoryTest {
    @Test
    public void test01(){
        HttpMapperFactory httpMapperFactory = HttpMapperFactory.builder()
                .httpmapperConfig("httpmapper.mapper")
                .build();

        TestMapper testMapper = httpMapperFactory.getMapper(TestMapper.class);
        System.out.println(testMapper.getClass().getName());
    }
}
