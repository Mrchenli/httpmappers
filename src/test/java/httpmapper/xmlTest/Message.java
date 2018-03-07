package httpmapper.xmlTest;

import lombok.Data;

@Data
public class Message<T> {
    private Head head;
    private T body;
}
