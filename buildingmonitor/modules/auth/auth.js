/**
 * @license
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

var module = {};

(function (module) {
    var log = new Log("auth-module");
    var constants = require("/utils/constants.js").constants;

    /**
     * Load current user tenant
     * @param username logged user name
     */
    module.loadTenant = function (username) {
        var carbon = require('carbon');
        var MultitenantUtils = Packages.org.wso2.carbon.utils.multitenancy.MultitenantUtils;
        var MultitenantConstants = Packages.org.wso2.carbon.base.MultitenantConstants;
        var TenantAxisUtils = Packages.org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
        var service;
        var ctx;
        var domain = MultitenantUtils.getTenantDomain(username);
        if (domain != null && !MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(domain)) {
            service = carbon.server.osgiService('org.wso2.carbon.utils.ConfigurationContextService');
            ctx = service.getServerConfigContext();
            TenantAxisUtils.setTenantAccessed(domain, ctx);
        }
    };

    /**
     * Basic login.
     * @param request {Object} HTTP request
     * @param response {Object} HTTP response
     */
    module.login = function (request, response) {
        var username = request.getParameter("username");

        if (!username || (username.length == 0)) {
            log.error("Username is not specified");
            return;
        }
        var password = request.getParameter("password");
        if (!password || (password.length == 0)) {
            log.error("Password is not specified");
            return;
        }

        var carbonServer = require("carbon").server;
        var isAuthenticated;
        try {
            isAuthenticated = (new carbonServer.Server()).authenticate(username, password);
            log.info(isAuthenticated + " isAutheticated reply");
        } catch (e) {
            log.error(e.message);
            var messageForNotExistingDomain = "Could not find a domain for the username";
            if (e.message.indexOf(messageForNotExistingDomain) < 0) {
                response.sendError(500, e.message);
                return;
            } else {
                isAuthenticated = false;
            }
        }
        if (isAuthenticated) {
            var tenantUser = carbonServer.tenantUser(username);
            var user = {
                username: tenantUser.username,
                domain: tenantUser.domain,
                tenantId: tenantUser.tenantId
            };
            session.put(constants.USER_CACHE_KEY, user);
            module.loadTenant(username);
            var utility = require("/app/modules/utility.js").utility;
            var apiWrapperUtil = require("/app/modules/oauth/token-handlers.js")["handlers"];

            apiWrapperUtil.setupTokenPairByPasswordGrantType(username, password);
            return true;
        } else {
            return false;
        }
    };

    /**
     * Basic logout.
     * @param response {Object} HTTP response
     */
    module.logout = function (response) {
        var previousUser = session.get(constants.USER_CACHE_KEY);
        try {
            session.invalidate();
        } catch (e) {
            log.error(e.message, e);
            response.sendError(500, e.message);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("User '" + previousUser.username + "' logged out.");
        }
        return true;
    };
})(module);
