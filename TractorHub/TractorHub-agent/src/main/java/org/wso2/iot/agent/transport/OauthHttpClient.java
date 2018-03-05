package org.wso2.iot.agent.transport;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.wso2.iot.agent.transport.dto.AccessTokenInfo;

import java.io.IOException;

public class OauthHttpClient {

    private final TokenHandler tokenHandler;

    public OauthHttpClient(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    public HttpResponse execute(HttpRequestBase request) throws IOException {
        AccessTokenInfo accessTokenInfo = tokenHandler.getAccessTokenInfo();
        HttpClient client = HttpClientBuilder.create().build();
        request.setHeader("Authorization", "Bearer " + accessTokenInfo.getAccessToken());
        return client.execute(request);
    }

}
