package mrchenli.request.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {
    String value();

    String urlCharset() default "UTF-8";

    long timeout() default 30000;

    String desc() default "";//请求的描述
}
