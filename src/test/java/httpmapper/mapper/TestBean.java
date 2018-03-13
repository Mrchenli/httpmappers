package httpmapper.mapper;

import lombok.Data;

@Data
public class TestBean <T>{
    private String name;
    private String age;
    private T data;
}
