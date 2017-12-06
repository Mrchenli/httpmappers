package mrchenli.handler;


import mrchenli.request.MapperRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PostProcess {

    Class<? extends PostProcessor> [] value() default DoNothingPostProcessor.class;

    final class DoNothingPostProcessor implements PostProcessor {

        @Override
        public void handlerBefore(MapperRequest request, Object objectParam) {
            return;
        }

        @Override
        public Object handleAfter(MapperRequest request, Object objectParam, Object object) {
            return object;
        }
    }
}
