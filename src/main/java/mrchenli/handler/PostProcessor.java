package mrchenli.handler;

import mrchenli.request.MapperRequest;

public interface PostProcessor {

    void handlerBefore(MapperRequest request, Object objectParam);

    Object handleAfter(MapperRequest mapperRequest,Object objectParam,Object object);

}
