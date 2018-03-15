package mrchenli.request.param;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface SignParam {
    String value() default "";//如果为空的话就是把里面的对象的属性是key value 否则的话就是key:JsonObject.toJsonString(obj);
    String signName() default "sign";
}
