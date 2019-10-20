package team.zhou.zim.client.service;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import team.zhou.zim.client.model.HttpResponseAdapt;
import team.zhou.zim.client.model.ImResult;
import team.zhou.zim.common.constant.Constants;
import team.zhou.zim.common.enums.ErrorCode;

/**
 * @author zhouxinghang
 * @date 2019-10-20
 */
@Slf4j
@Service
public class HttpRequstService {
    @Value("${zim.secret}")
    private static String secret;
    private static SSLConnectionSocketFactory sslsf;

    private CloseableHttpClient httpClient;

    public <T> ImResult<T> post(String url, String json, Class<T> resultType) {
        HttpPost post = null;
        try {
            initHttpClient();
            int nonce = (int)(Math.random() * 100000 + 3);
            long timestamp = System.currentTimeMillis();
            String str = nonce + "|" + secret + "|" + timestamp;
            String sign = DigestUtils.sha1Hex(str);

            post = new HttpPost(url);
            post.setHeader("Content-type", "application/json; charset=utf-8");
            post.setHeader("Connection", "Keep-Alive");
            post.setHeader("nonce", nonce + "");
            post.setHeader("timestamp", "" + timestamp);
            post.setHeader("sign", sign);
            post.setEntity(new StringEntity(json, Constants.DEFAULT_CHARSET));

            HttpResponse response = httpClient.execute(post);
            HttpResponseAdapt httpResponseAdapt = transHttpResponseAdapt(response);
            int statusCode = httpResponseAdapt.getHttpCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.info("Request error: {}", statusCode);
                throw new Exception("Http request error with code:" + statusCode);
            } else {
                T result =  JSONObject.parseObject(httpResponseAdapt.getContext(), resultType);
                return new ImResult<>(ErrorCode.SUCCESS, result);
            }
        } catch (Exception e) {
            log.info("Request error,req:{}", json, e);
        }
        return new ImResult<>(ErrorCode.SYS_ERROR);
    }

    public <T> ImResult<T> get(String url, Map<String, Object> reqParam) {
        initHttpClient();
        // todo
        return null;
    }



    private HttpResponseAdapt transHttpResponseAdapt(HttpResponse httpResponse) throws IOException {

        HttpResponseAdapt httpResponseAdapt = new HttpResponseAdapt();
        ContentType contentType = ContentType.get(httpResponse.getEntity());

        if (contentType != null) {
            if (StringUtils.contains(contentType.getMimeType(), "image")) {
                httpResponseAdapt.setBody(EntityUtils.toByteArray(httpResponse.getEntity()));
            } else {
                httpResponseAdapt.setContext(EntityUtils.toString(httpResponse.getEntity()));
            }
        }

        if (httpResponse.getStatusLine().getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES
            && httpResponse.getStatusLine().getStatusCode() < HttpStatus.SC_BAD_REQUEST) {
            Header locationHeader = httpResponse.getFirstHeader("location");
            if (locationHeader != null) {
                httpResponseAdapt.setRedirectUrl(locationHeader.getValue());
            }
        }
        httpResponseAdapt.setHttpCode(httpResponse.getStatusLine().getStatusCode());

        return httpResponseAdapt;
    }

    private void initHttpClient() {
        if (httpClient == null) {
            synchronized (HttpRequstService.class) {
                if (httpClient == null) {
                    httpClient = buildHttpClient(true);
                }
            }
        }
    }

    private CloseableHttpClient buildHttpClient(boolean isMultiThread) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        if (isMultiThread) {
            httpClientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager());
        }
        httpClientBuilder.setSSLSocketFactory(sslsf);
        httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        return httpClientBuilder.build();
    }
}
