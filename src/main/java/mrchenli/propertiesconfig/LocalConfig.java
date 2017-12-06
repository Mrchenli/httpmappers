package mrchenli.propertiesconfig;

/**
 * this config is for test
 */
public class LocalConfig extends Config {

    public LocalConfig(HttpMapperPropertiesUtil httpMapperPropertiesUtil) {
        super(httpMapperPropertiesUtil);
    }

    @Override
    public String getAppid() {
        return null;
    }

    @Override
    public String getPrivateKey() {
        return null;
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public String getPublic_key() {
        return null;
    }

    @Override
    public String getRsaServiceStr() {
        return null;
    }

    @Override
    public String getDesServiceStr() {
        return null;
    }
}
