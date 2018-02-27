/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.agent;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.iot.agent.dto.AccessTokenInfo;
import org.wso2.iot.agent.dto.ApiApplicationKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.apache.commons.codec.CharEncoding.UTF_8;

public class TokenHandler {

    private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final Log log = LogFactory.getLog(TokenHandler.class);

    private String tokenEndpoint;
    private AccessTokenInfo accessTokenInfo;
    private ApiApplicationKey apiApplicationKey;
    private TokenRenewListener tokenRenewListener;

    public TokenHandler(String tokenEndpoint, AccessTokenInfo accessTokenInfo, ApiApplicationKey apiApplicationKey,
                        TokenRenewListener tokenRenewListener) {
        this.tokenEndpoint = tokenEndpoint;
        this.accessTokenInfo = accessTokenInfo;
        this.apiApplicationKey = apiApplicationKey;
        this.tokenRenewListener = tokenRenewListener;
    }

    public AccessTokenInfo renewTokens() throws TokenRenewalException {
        String encodedClientApp = new String(
                Base64.encodeBase64((apiApplicationKey.getConsumerKey() + ":" + apiApplicationKey.getConsumerSecret())
                                            .getBytes(Charset.forName(UTF_8))), Charset.forName(UTF_8));
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(this.tokenEndpoint);
            httpPost.setHeader("Authorization", "Basic " + encodedClientApp);
            httpPost.setHeader("Content-Type", APPLICATION_FORM_URLENCODED);

            StringEntity tokenEPPayload = new StringEntity(
                    "grant_type=refresh_token&refresh_token=" + accessTokenInfo.getRefreshToken(),
                    "UTF-8");
            httpPost.setEntity(tokenEPPayload);
            String tokenResult;

            HttpResponse response = client.execute(httpPost);
            inputStreamReader = new InputStreamReader(response.getEntity().getContent(), Charset.forName(UTF_8));
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            inputStreamReader.close();
            tokenResult = result.toString();
            JSONParser jsonParser = new JSONParser();
            JSONObject jTokenResult = (JSONObject) jsonParser.parse(tokenResult);
            if (jTokenResult.containsKey("error")) {
                String errorMsg = "Token renewal failed. " + jTokenResult.get("error_description");
                log.error(errorMsg);
                throw new TokenRenewalException(errorMsg);
            }
            accessTokenInfo.setAccessToken(jTokenResult.get("access_token").toString());
            accessTokenInfo.setRefreshToken(jTokenResult.get("refresh_token").toString());
            tokenRenewListener.onTokenRenewed(accessTokenInfo);
            log.info("Token renewed.");
            return accessTokenInfo;
        } catch (IOException | ParseException e) {
            log.error("Cannot renew tokens due to " + e.getMessage(), e);
            throw new TokenRenewalException(e);
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    log.error("Error occurred when closing input stream reader.", e);
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("Error occurred when closing buffered reader.", e);
                }
            }
        }
    }

    public AccessTokenInfo getAccessTokenInfo() {
        return accessTokenInfo;
    }

}
