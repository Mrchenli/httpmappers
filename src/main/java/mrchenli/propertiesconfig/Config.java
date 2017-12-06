package mrchenli.propertiesconfig;


import mrchenli.crypt.des.DesService;
import mrchenli.crypt.rsa.RsaService;

public abstract class Config{

    private RsaService rsaService;
    private RsaService thirdRsaService;
    private DesService desService;
    protected HttpMapperPropertiesUtil httpMapperPropertiesUtil;

    public abstract String getAppid();

    public HttpMapperPropertiesUtil getHttpMapperPropertiesUtil() {
        return httpMapperPropertiesUtil;
    }

    public void setHttpMapperPropertiesUtil(HttpMapperPropertiesUtil httpMapperPropertiesUtil) {
        this.httpMapperPropertiesUtil = httpMapperPropertiesUtil;
    }

    public String getPrivateKey(){return "";}

    public abstract String getSuffix();

    public abstract String getPublic_key();

    public abstract String getRsaServiceStr();
    public abstract String getDesServiceStr();

    public  RsaService getRsaService(){
        try {
            if(rsaService!=null){
                return rsaService;
            }
            Class clzz = Class.forName(getRsaServiceStr());
            rsaService = (RsaService) clzz.getConstructor(String.class,String.class).newInstance(ConfigManager.getSelfPrivateKey(),getPublic_key());
            return rsaService;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 针对一些用自己私钥给对方用来签名的合作机构
     * @return
     */
    public  RsaService getThirdRsaService(){
        try {
            if(thirdRsaService!=null){
                return thirdRsaService;
            }
            if(getPrivateKey()==null||getPrivateKey().trim().length()==0){
                throw new RuntimeException("third rsa privateKey 为null");
            }
            Class clzz = Class.forName(getRsaServiceStr());
            thirdRsaService = (RsaService) clzz.getConstructor(String.class,String.class).newInstance(getPrivateKey(),getPublic_key());
            return thirdRsaService;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public DesService getDesService(){
        try {
            if(desService!=null){
                return desService;
            }
            Class clzz = Class.forName(getDesServiceStr());
            desService= (DesService) clzz.newInstance();
            return desService;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Config(HttpMapperPropertiesUtil httpMapperPropertiesUtil) {
        this.httpMapperPropertiesUtil = httpMapperPropertiesUtil;
        ConfigManager.addConfig(this.getClass().getCanonicalName(),this);
    }

    public Config(HttpMapperPropertiesUtil httpMapperPropertiesUtil,String str){
        this.httpMapperPropertiesUtil = httpMapperPropertiesUtil;
        ConfigManager.addConfig(str,this);
    }



}
