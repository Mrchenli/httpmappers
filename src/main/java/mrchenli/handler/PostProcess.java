package mrchenli.handler;


import mrchenli.request.MapperRequest;
import mrchenli.request.param.HttpRequestBean;
import org.apache.http.client.methods.HttpUriRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PostProcess {

    Class<? extends PostProcessor> [] value() default DoNothingPostProcessor.class;

    final class DoNothingPostProcessor implements PostProcessor {


        @Override
        public void handlerBefore(Map<String, String> headers, Map<String, Object> params, Map<String, String> urlParams) {
            return;
        }

        @Override
        public Object handleAfter(MapperRequest mapperRequest, HttpRequestBean requestBean, Object object) {
            return object;
        }
    }
}
