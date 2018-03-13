package mrchenli.http.httpclient;

import org.apache.http.client.HttpClient;

/**
 * 使用了普通工厂
 */
public interface HttpClientFactory {
    HttpClient create();
}
