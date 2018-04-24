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

package org.wso2.iot.dashboard.portal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.wso2.iot.dashboard.portal.LoginController.*;

public class InvokerController extends HttpServlet {
    private static final Log log = LogFactory.getLog(LoginController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(ATTR_ACCESS_TOKEN) == null) {
            resp.sendError(401, "Unauthorized, Access token not found in the session");
            return;
        }
        Object accessTokenObj = session.getAttribute(ATTR_ACCESS_TOKEN);
        String uri = req.getParameter("uri");
        String method = req.getParameter("method");
        String payload = req.getParameter("payload");
        String contentType = req.getParameter("content-type");
        if (uri == null || method == null) {
            resp.sendError(400, "Bad Request, uri or method not found");
            return;
        }
        if (contentType == null || contentType.isEmpty()) contentType = ContentType.APPLICATION_JSON.toString();

        uri = getServletContext().getInitParameter("deviceMgtEndpoint") + uri;
        HttpRequestBase executor = null;
        if ("GET".equalsIgnoreCase(method)) {
            executor = new HttpGet(uri);
        } else if ("POST".equalsIgnoreCase(method)) {
            executor = new HttpPost(uri);
            StringEntity payloadEntity = new StringEntity(payload, ContentType.create(contentType));
            ((HttpPost) executor).setEntity(payloadEntity);
        } else if ("PUT".equalsIgnoreCase(method)) {
            executor = new HttpPut(uri);
            StringEntity payloadEntity = new StringEntity(payload, ContentType.create(contentType));
            ((HttpPut) executor).setEntity(payloadEntity);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            executor = new HttpDelete(uri);
        } else {
            resp.sendError(400, "Bad Request, method not supported");
            return;
        }

        String accessToken = accessTokenObj.toString();
        executor.setHeader("Authorization", "Bearer " + accessToken);

        String result = execute(executor, req, resp);
        if (result != null && !result.isEmpty()) resp.getWriter().write(result);
    }

    private String execute(HttpRequestBase executor, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        return execute(executor, req, resp, 5);
    }

    private String execute(HttpRequestBase executor, HttpServletRequest req, HttpServletResponse resp, int retryCount)
            throws IOException {
        if (retryCount == 0) {
            resp.sendError(500, "Internal Server Error, unable to retrieve access token with refresh token");
        }
        CloseableHttpClient client = null;
        try {
            client = getHTTPClient();
        } catch (LoginException e) {
            resp.sendError(500, "Internal Server Error");
            return null;
        }

        HttpResponse response = client.execute(executor);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF8"));
        StringBuilder resultBuffer = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            resultBuffer.append(line);
        }
        String result = resultBuffer.toString();
        if (response.getStatusLine().getStatusCode() == 401) {
            if (result.equals("Access token expired") || result.equals(
                    "Invalid input. Access token validation failed")) {
                refreshToken(req, resp);
                execute(executor, req, resp, --retryCount);
            }
        }
        rd.close();
        return result;
    }

    private void refreshToken(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("refreshing the token");
        HttpPost tokenEndpoint = new HttpPost(getServletContext().getInitParameter("tokenEndpoint"));
        HttpSession session = req.getSession(false);
        StringEntity tokenEndpointPayload = new StringEntity(
                "grant_type=refresh_token&refresh_token=" + session.getAttribute("refreshToken")
                        + "&scope=PRODUCTION",
                ContentType.APPLICATION_FORM_URLENCODED);

        tokenEndpoint.setEntity(tokenEndpointPayload);

        String encodedClientApp = req.getSession().getAttribute(ATTR_ENCODED_CLIENT_APP).toString();
        tokenEndpoint.setHeader("Authorization",
                "Basic " + encodedClientApp);
        tokenEndpoint.setHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());

        CloseableHttpClient client = null;
        try {
            client = getHTTPClient();
        } catch (LoginException e) {
            resp.sendError(500, "Internal Server Error");
            return;
        }

        HttpResponse response = client.execute(tokenEndpoint);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF8"));
        StringBuilder resultBuffer = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            resultBuffer.append(line);
        }
        String tokenResult = resultBuffer.toString();
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jTokenResult = (JSONObject) jsonParser.parse(tokenResult);
                String refreshToken = jTokenResult.get("refresh_token").toString();
                String accessToken = jTokenResult.get("access_token").toString();
                //String scope = jTokenResult.get("scope").toString();
                session.setAttribute(ATTR_ACCESS_TOKEN, accessToken);
                session.setAttribute(ATTR_REFRESH_TOKEN, refreshToken);
            } catch (ParseException e) {
                log.error("Error while parsing refresh token response", e);
                resp.sendError(500, "Internal Server Error");
            }
        } else {
            log.error("Error while parsing refresh token response, Token EP response : " +
                    response.getStatusLine().getStatusCode());
            resp.sendError(500, "Internal Server Error");
        }
        rd.close();
    }

    private CloseableHttpClient getHTTPClient() throws LoginException {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            return HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            log.error(e.getMessage(), e);
            throw new LoginException("Error occurred while retrieving http client", e);
        }
    }
}
