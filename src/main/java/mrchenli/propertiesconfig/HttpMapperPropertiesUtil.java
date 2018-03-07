package mrchenli.propertiesconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class HttpMapperPropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpMapperPropertiesUtil.class);

    private Properties propertie;

    public HttpMapperPropertiesUtil(String filePath) {
        propertie = new Properties();
        InputStream is;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(filePath);
            //解决中文乱码问题
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            propertie.load(reader);
            is.close();
        } catch (Exception ex) {
            logger.error("装载文件--->失败！- 原因：文件路径错误或者文件不存在 e==>{}",ex);
        }
    }

    /**
     * 重载函数，得到key的值
     *
     * @param key
     *            取得其值的键
     * @return key的值
     */
    public String getValue(String key) {
        if (propertie.containsKey(key)) {
            String value = propertie.getProperty(key);// 得到某一属性的值
            return value;
        } else
            return "";
    }

}
