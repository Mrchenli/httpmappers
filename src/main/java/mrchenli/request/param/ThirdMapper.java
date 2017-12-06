package mrchenli.request.param;

import mrchenli.propertiesconfig.Config;
import mrchenli.propertiesconfig.LocalConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThirdMapper {
    Class<? extends Config> value() default LocalConfig.class;
}
