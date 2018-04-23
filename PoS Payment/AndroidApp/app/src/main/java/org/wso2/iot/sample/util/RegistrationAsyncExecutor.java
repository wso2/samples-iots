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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.sample.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.wso2.iot.sample.constants.DeviceConstants;
import org.wso2.iot.sample.util.dto.AccessTokenInfo;
import org.wso2.iot.sample.util.dto.ApiApplicationKey;
import org.wso2.iot.sample.util.dto.ApiApplicationRegistrationService;
import org.wso2.iot.sample.util.dto.ApiRegistrationProfile;
import org.wso2.iot.sample.util.dto.Device;
import org.wso2.iot.sample.util.dto.EnrollmentService;
import org.wso2.iot.sample.util.dto.EnrolmentInfo;
import org.wso2.iot.sample.util.dto.OAuthRequestInterceptor;
import org.wso2.iot.sample.util.dto.TokenIssuerService;

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

public class RegistrationAsyncExecutor extends AsyncTask<String, Void, Map<String, String>> {

    private static final String TAG = RegistrationAsyncExecutor.class.getSimpleName();

    private static final String STATUS = "status";
    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String SCOPES = "perm:device:enroll perm:device:disenroll perm:device:modify " +
            "perm:device:operations perm:device:publish-event";

    private Context context;
    private TrustManager[] trustAllCerts = new TrustManager[]{
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
    private Client disableHostnameVerification = new Client.Default(getTrustedSSLSocketFactory(), new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    });

    public RegistrationAsyncExecutor(Context context) {
        this.context = context;
    }

    @Override
    protected Map<String, String> doInBackground(String... parameters) {
        if (android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        String username = parameters[0];
        String password = parameters[1];
        String deviceId = parameters[2];
        String endpoint = parameters[3];
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(STATUS, "200");
        AccessTokenInfo accessTokenInfo;
        try {
            //ApiApplicationRegistration
            ApiApplicationRegistrationService apiApplicationRegistrationService = Feign.builder().client(disableHostnameVerification)
                    .requestInterceptor(new BasicAuthRequestInterceptor(username, password))
                    .contract(new JAXRSContract()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                    .target(ApiApplicationRegistrationService.class, endpoint + DeviceConstants.API_APPLICATION_REGISTRATION_CONTEXT);
            ApiRegistrationProfile apiRegistrationProfile = new ApiRegistrationProfile();
            apiRegistrationProfile.setApplicationName(DeviceConstants.DEVICE_TYPE + "_" + deviceId);
            apiRegistrationProfile.setIsAllowedToAllDomains(false);
            apiRegistrationProfile.setIsMappingAnExistingOAuthApp(false);
            apiRegistrationProfile.setTags(new String[]{"device_agent"});
            ApiApplicationKey apiApplicationKey = apiApplicationRegistrationService.register(apiRegistrationProfile);

            //PasswordGrantType
            TokenIssuerService tokenIssuerService = Feign.builder().client(disableHostnameVerification).requestInterceptor(
                    new BasicAuthRequestInterceptor(apiApplicationKey.getConsumerKey(), apiApplicationKey.getConsumerSecret()))
                    .contract(new JAXRSContract()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                    .target(TokenIssuerService.class, endpoint);
            accessTokenInfo = tokenIssuerService.getToken(PASSWORD_GRANT_TYPE, username, password, SCOPES);

            //DeviceRegister
            EnrollmentService enrollmentService = Feign.builder().client(disableHostnameVerification)
                    .requestInterceptor(new OAuthRequestInterceptor(accessTokenInfo.getAccess_token()))
                    .contract(new JAXRSContract()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                    .target(EnrollmentService.class, endpoint + DeviceConstants.DEVICE_API_CONTEXT);
            Device device = new Device();
            device.setDeviceIdentifier(deviceId);
            device.setType(DeviceConstants.DEVICE_TYPE);
            device.setName("PoS Client " + deviceId);
            device.setDescription("Sample Description");
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setOwnership("BYOD");
            enrolmentInfo.setStatus("ACTIVE");
            device.setEnrolmentInfo(enrolmentInfo);
            enrollmentService.register(device);

            LocalRegistry.addConsumerKey(context, apiApplicationKey.getConsumerKey());
            LocalRegistry.addConsumerSecret(context, apiApplicationKey.getConsumerSecret());
            LocalRegistry.addAccessToken(context, accessTokenInfo.getAccess_token());
            LocalRegistry.addRefreshToken(context, accessTokenInfo.getRefresh_token());
            return responseMap;
        } catch (FeignException e) {
            responseMap.put(STATUS, "" + e.status());
            Log.e(TAG, e.getMessage(), e);
            return responseMap;
        }
    }

    private SSLSocketFactory getTrustedSSLSocketFactory() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            Log.e(RegistrationAsyncExecutor.class.getName(), "Invalid Certificate");
            return null;
        }

    }
}
