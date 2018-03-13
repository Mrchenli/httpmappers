package mrchenli;

import mrchenli.config.Configuration;
import mrchenli.http.executor.DefaultHttpExecutor;
import mrchenli.http.httpclient.SSLHttpClientFactory;
import mrchenli.propertiesconfig.ConfigManager;

public class HttpMapperFactory {

    private Configuration configuration;

    public static HttpMapperFactoryBuilder builder(){
        return new HttpMapperFactoryBuilder();
    }

    private HttpMapperFactory() {
    }

    public <T> T getMapper(Class<T> clzz){
        T t = configuration.newMapper(clzz);
        return t;
    }

    public String getValueByAppid(String appid,String key){
        return ConfigManager.getValueByAppid(appid,key);
    }

    public static class HttpMapperFactoryBuilder{

        private HttpMapperFactory httpMapperFactory;

        public HttpMapperFactoryBuilder configManager(String... configPaths){
            if(httpMapperFactory==null){
                httpMapperFactory = new HttpMapperFactory();
            }
            ConfigManager.init(configPaths);
            return this;
        }

        public HttpMapperFactoryBuilder httpmapperConfig(String... basePackages){
            if(httpMapperFactory ==null){
                httpMapperFactory = new HttpMapperFactory();
            }
            httpMapperFactory.configuration = Configuration.newBuilder().setScanPath(basePackages)
                    .setHttpExecutor(new DefaultHttpExecutor(new SSLHttpClientFactory()))
                    .build();
            return this;
        }

        public HttpMapperFactory build(){
            return httpMapperFactory;
        }
    }

}
