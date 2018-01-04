package mrchenli.response;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author gaohang on 8/11/17.
 */
public class FastJsonResponseHandler extends AbstractResponseHandler {

  private  Logger logger = LoggerFactory.getLogger(FastJsonResponseHandler.class);

  /**
   * @param text
   * @param code
   * @return
   */
  @Override
  public String convertTextToWeWant(String text, Integer code) {
    if(code!=200){
      logger.info("code is ==>{},text is==>{}",code,text);
      throw new RuntimeException(JSONObject.toJSONString(code));
    }else {
      return text;
    }
  }

}
