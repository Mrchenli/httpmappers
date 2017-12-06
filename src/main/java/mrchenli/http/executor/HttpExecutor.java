package mrchenli.http.executor;


import mrchenli.request.MapperRequest;
import org.apache.http.HttpResponse;

import java.io.Closeable;

public interface HttpExecutor extends Closeable{

    HttpResponse execute(MapperRequest request, Object paramObject);



}
