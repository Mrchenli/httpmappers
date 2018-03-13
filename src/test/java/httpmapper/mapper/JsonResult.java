package httpmapper.mapper;

import lombok.Data;

@Data
public class JsonResult<T> {

    private String code;

    private String reason;

    private T payload;

}
