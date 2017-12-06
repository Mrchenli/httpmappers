package mrchenli.http.httpclient;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DefaultHttpClientFactory implements HttpClientFactory {

    @Override
    public CloseableHttpClient create() {
        return HttpClients.createDefault();
    }

}
