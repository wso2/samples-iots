/**
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

var constants = {};
(function (constants) {
    constants.USER_CACHE_KEY = 'USER_CACHE_KEY';
    constants.APP_NAME = 'buildingmonitor';
    constants.USER_SESSION_KEY = "_UUF_USER";
    constants.UNSPECIFIED = "Unspecified";
    constants.httpURL = "httpURL";
    constants.httpsURL = "httpsURL";

    constants.DEVICE_IDENTIFIER = "deviceIdentifier";
    constants.DEVICE_NAME = "name";
    constants.DEVICE_OWNERSHIP = "ownership";
    constants.DEVICE_OWNER = "owner";
    constants.DEVICE_TYPE = "type";
    constants.DEVICE_VENDOR = "vendor";
    constants.DEVICE_MODEL = "model";
    constants.DEVICE_PRODUCT = "PRODUCT";
    constants.DEVICE_OS_VERSION = "osVersion";
    constants.DEVICE_OS_BUILD_DATE = "osBuildDate";
    constants.DEVICE_PROPERTIES = "properties";
    constants.DEVICE_ENROLLMENT_INFO = "enrolmentInfo";
    constants.DEVICE_STATUS = "status";
    constants.DEVICE_INFO = "deviceInfo";

    constants.FEATURE_NAME = "featureName";
    constants.FEATURE_DESCRIPTION = "featureDescription";

    constants.PLATFORM_ANDROID = "android";
    constants.PLATFORM_WINDOWS = "windows";
    constants.PLATFORM_IOS = "ios";

    constants.LANGUAGE_US = "en_US";

    constants.VENDOR_APPLE = "Apple";
    constants.ERRORS = {
        "USER_NOT_FOUND": "USER_NOT_FOUND"
    };

    constants.USER_STORES_NOISY_CHAR = "\"";
    constants.USER_STORES_SPLITTING_CHAR = "\\n";
    constants.USER_STORE_CONFIG_ADMIN_SERVICE_END_POINT =
        "/services/UserStoreConfigAdminService.UserStoreConfigAdminServiceHttpsSoap12Endpoint/";

    constants.SOAP_VERSION = 1.2;
    constants.WEB_SERVICE_ADDRESSING_VERSION = 1.0;
    constants.TOKEN_PAIR = "tokenPair";
    constants.ENCODED_TENANT_BASED_CLIENT_APP_CREDENTIALS = "encodedTenantBasedClientAppCredentials";
    constants.CONTENT_TYPE_IDENTIFIER = "Content-Type";
    constants.ENCODED_TENANT_BASED_WEB_SOCKET_CLIENT_CREDENTIALS = "encodedTenantBasedWebSocketClientCredentials";

    constants.CONTENT_DISPOSITION_IDENTIFIER = "Content-Disposition";
    constants.APPLICATION_JSON = "application/json";
    constants.APPLICATION_ZIP = "application/zip";
    constants.STREAMING_FILES_ACCEPT_HEADERS = ["application/zip", "application/pdf", "application/octet-stream"];
    constants.ACCEPT_IDENTIFIER = "Accept";
    constants.AUTHORIZATION_HEADER= "Authorization";
    constants.BEARER_PREFIX = "Bearer ";
    constants.HTTP_GET = "GET";
    constants.HTTP_POST = "POST";
    constants.HTTP_PUT = "PUT";
    constants.HTTP_DELETE = "DELETE";

    constants.HTTP_CONFLICT = 409;
    constants.HTTP_CREATED = 201;

    constants.CACHED_CREDENTIALS = "tenantBasedCredentials";
    constants.CACHED_CREDENTIALS_FOR_WEBSOCKET_APP = "tenantBasedWebSocketClientCredentials";

    constants.ALLOWED_SCOPES = "scopes";
}(constants));

