/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package org.wso2.iot.mqttsample.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.wso2.iot.mqttsample.util.dto.AccessTokenInfo;
import org.wso2.iot.mqttsample.util.dto.TokenIssuerService;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;

public class TokenRenewAsyncExecutor extends AsyncTask<String, Void, Map<String, String>> {

    private static final String STATUS = "status";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    private Context context;

    public TokenRenewAsyncExecutor(Context context) {
        this.context = context;
    }

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    Client disableHostnameVerification = new Client.Default(getTrustedSSLSocketFactory(), new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    });

    @Override
    protected Map<String, String> doInBackground(String... parameters) {
        String endpoint = parameters[0];
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(STATUS, "200");
        AccessTokenInfo accessTokenInfo;
        try {
            //Refresh Token Grant Type
            TokenIssuerService tokenIssuerService = Feign.builder().client(disableHostnameVerification).requestInterceptor(
                    new BasicAuthRequestInterceptor(LocalRegistry.getConsumerKey(context), LocalRegistry.getConsumerSecret(context)))
                    .contract(new JAXRSContract()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                    .target(TokenIssuerService.class, endpoint);
            accessTokenInfo = tokenIssuerService.renewToken(REFRESH_TOKEN_GRANT_TYPE, LocalRegistry.getRefreshToken(context));

            if (accessTokenInfo != null) {
                LocalRegistry.addAccessToken(context, accessTokenInfo.getAccess_token());
                LocalRegistry.addRefreshToken(context, accessTokenInfo.getRefresh_token());
            }
            return responseMap;
        } catch (FeignException e) {
            responseMap.put(STATUS, "" + e.status());
            return responseMap;
        }
    }

    private SSLSocketFactory getTrustedSSLSocketFactory() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            Log.e(TokenRenewAsyncExecutor.class.getName(), "Invalid Certificate");
            return null;
        }

    }
}
