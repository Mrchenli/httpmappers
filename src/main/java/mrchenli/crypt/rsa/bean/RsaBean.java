package mrchenli.crypt.rsa.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RsaBean {
    private String sign;
    private String des_key;
    private String rsa_string;

}
