package mrchenli.http.executor;


import mrchenli.request.MapperRequest;
import org.apache.http.HttpResponse;

import java.io.Closeable;
import java.util.Map;

public interface HttpExecutor{

    HttpResponse execute(MapperRequest request, Object paramObject,Map<String,String> headers);

}
