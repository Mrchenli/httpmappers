package mrchenli.request.param;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RsaParam {
    String value();
    String signName() default "sign";
    String desKeyName() default "des_key";
    EncryptType encType() default EncryptType.RSA_DES_PRI_PUB;

}
