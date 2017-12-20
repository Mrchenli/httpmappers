package mrchenli.response;

/**
 * @author gaohang on 8/11/17.
 */
public class FastJsonResponseHandler extends AbstractResponseHandler {

  @Override
  public String convertTextToWeWant(String text, Integer code) {
    return text;
  }

}
