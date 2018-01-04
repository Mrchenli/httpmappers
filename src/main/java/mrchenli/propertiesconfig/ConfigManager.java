package mrchenli.propertiesconfig;


import com.google.common.collect.Maps;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class ConfigManager {

    private static Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static Map<String,Config> configs = Maps.newHashMap();

    public static final String SELF_CONFIG = "DAFDFNASLZGFNZELWQSZSFAS";


    public static void  init(String configPath) {
        try {
            if(configs.isEmpty()){
                Reflections reflections = new Reflections(configPath);
                Set<Class<?>> set = reflections.getTypesAnnotatedWith(AutoManage.class);
                for (Class<?> clzz :set) {
                    if(Config.class.isAssignableFrom(clzz)){
                        AutoManage autoManage = clzz.getAnnotation(AutoManage.class);
                        clzz.getConstructor(HttpMapperPropertiesUtil.class).newInstance(new HttpMapperPropertiesUtil(autoManage.value()));
                    }
                }
            }
        }catch (Exception e){
            logger.error("ConfigManager init failed! e==>{}",e);
        }

    }


    /**
     * 根据appid 获取配置信息
     * @param
     * @return
     */
    public static Config getConfig(String canonicalName){
        if(canonicalName ==null) return null;
        return configs.get(canonicalName);
    }

    public static String getValueByCanoName(String canonicalName ,String key){
        return getConfig(canonicalName).getHttpMapperPropertiesUtil().getValue(key);
    }

    public static String getValueByType(Class clzz ,String key){
        return getConfigByType(clzz).getHttpMapperPropertiesUtil().getValue(key);
    }

    public static String getValueByAppid(String appid ,String key){
        return getConfigByAppid(appid).getHttpMapperPropertiesUtil().getValue(key);
    }


    public static Config getConfigByType(Class clzz){
        return configs.get(clzz.getCanonicalName());
    }

    public static Config getConfigByAppid(String appid){
        for (Map.Entry<String,Config> entry:configs.entrySet()) {
            if(appid.equals( entry.getValue().getAppid())){
                return entry.getValue();
            }
        }
        return null;
    }
    /**
     * 把这个配置类 配置到配置类管理中心
     * @param config
     */
    public static void addConfig(String key ,Config config){
        configs.put(key,config);
    }


    public static String getSelfPrivateKey(){
        return configs.get(SELF_CONFIG).getPrivateKey();
    }

}
