package mrchenli.request.param;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RsaPublicParam {
	String value() default "data";

	String signName() default "sign";

}
