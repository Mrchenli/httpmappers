package mrchenli.config;


import mrchenli.http.executor.DefaultHttpExecutor;
import mrchenli.http.executor.HttpExecutor;
import mrchenli.http.httpclient.DefaultHttpClientFactory;
import mrchenli.proxy.MapperProxyFactory;
import mrchenli.request.MapperRequest;
import mrchenli.request.MapperRequestFactory;

public class Configuration {

    private HttpExecutor httpExecutor = new DefaultHttpExecutor(new DefaultHttpClientFactory());

    private MapperRequestFactory mapperRequestFactory;

    public MapperRequestFactory getMapperRequestFactory() {
        return mapperRequestFactory;
    }

    public HttpExecutor getHttpExecutor() {
        return httpExecutor;
    }

    public MapperRequest getMapperRequest(String key){
        return mapperRequestFactory.getMapperRequest(key);
    }

    private Configuration(HttpExecutor httpExecutor,MapperRequestFactory mapperRequestFactory){
        this.httpExecutor = httpExecutor;
        this.mapperRequestFactory = mapperRequestFactory;
    }

    public <T> T newMapper(Class<T> mapperClass) {
        return MapperProxyFactory.newProxy(this, mapperClass);
    }

    public static ConfigurationBuilder newBuilder() {
        return new ConfigurationBuilder();
    }

    public static class ConfigurationBuilder{

        private HttpExecutor httpExecutor;
        private String[] scanPaths;


        private ConfigurationBuilder() {
        }

        public  ConfigurationBuilder setHttpExecutor(HttpExecutor httpExecutor){
            this.httpExecutor = httpExecutor;
            return this;
        }
        public ConfigurationBuilder setScanPath(String... scanPaths){
            this.scanPaths = scanPaths;
            return this;
        }

        public Configuration build(){
            if(httpExecutor==null){
                httpExecutor = new DefaultHttpExecutor(new DefaultHttpClientFactory());
            }
            if(scanPaths == null || scanPaths.length==0){
                throw new RuntimeException("请指定third mapper 的扫描路径");
            }

            MapperRequestFactory mapperRequestFactory = new MapperRequestFactory(scanPaths);
            return new Configuration(httpExecutor,mapperRequestFactory);
        }
    }


}
