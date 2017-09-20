/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var getData;

(function () {
    var CONTENT_TYPE_JSON = "application/json";
    var AUTHORIZATION_HEADER = "Authorization";
    var TENANT_DOMAIN = "domain";
    var CONST_AT = "@";
    var USERNAME = "username";
    var HTTP_USER_NOT_AUTHENTICATED = 403;
    var JS_MAX_VALUE = "9007199254740992";
    var JS_MIN_VALUE = "-9007199254740992";

    var typeMap = {
        "bool": "string",
        "boolean": "string",
        "string": "string",
        "int": "number",
        "integer": "number",
        "long": "number",
        "double": "number",
        "float": "number",
        "time": "time"
    };

    var log = new Log();
    var carbon = require('carbon');
    var JSUtils = Packages.org.wso2.carbon.analytics.jsservice.Utils;
    var AnalyticsCachedJSServiceConnector = Packages.org.wso2.carbon.analytics.jsservice.AnalyticsCachedJSServiceConnector;
    var AnalyticsCache = Packages.org.wso2.carbon.analytics.jsservice.AnalyticsCachedJSServiceConnector.AnalyticsCache;
    var cacheTimeoutSeconds = 5;
    var loggedInUser = null;
    var constants = require("/lib/constants.js").constants;


    var cacheSizeBytes = 1024 * 1024 * 1024; // 1GB
    response.contentType = CONTENT_TYPE_JSON;

    var authParam = request.getHeader(AUTHORIZATION_HEADER);
    if (authParam != null) {
        credentials = JSUtils.authenticate(authParam);
        loggedInUser = credentials[0];
    } else {
        var token = session.get(constants.CACHE_KEY_USER);
        if (token != null) {
            loggedInUser = token[USERNAME] + CONST_AT + token[TENANT_DOMAIN];
        } else {
            log.error("user is not authenticated!");
            response.status = HTTP_USER_NOT_AUTHENTICATED;
            print('{ "status": "Failed", "message": "User is not authenticated." }');
            return;
        }
    }

    var cache = application.get("AnalyticsWebServiceCache");
    if (cache == null) {
        cache = new AnalyticsCache(cacheTimeoutSeconds, cacheSizeBytes);
        application.put("AnalyticsWebServiceCache", cache);
    }
    var connector = new AnalyticsCachedJSServiceConnector(cache);

    /**
     * To get the data from the event store
     * @param buildingId Building Id
     * @param floorId Floor id
     * @param tableName table Name
     * @param fromTime Start time
     * @param toTime end time
     * @param start start index
     * @param limit Limit
     * @param sortBy sort order [ASC, DSC]
     * @returns {Array} the retrived data
     */
    getData = function (tableName, buildingId, floorId, fromTime, toTime, start, limit, sortBy) {
        var luceneQuery = null;
        var sort = sortBy || "ASC";
        if (fromTime && toTime) {
            luceneQuery = "timeStamp:[" + fromTime + " TO " + toTime + "]";
        } else {
            luceneQuery = "timeStamp:[" + JS_MIN_VALUE + " TO " + JS_MAX_VALUE + "]";
        }
        var limitCount = limit || 100;
        var startCount = start || 0;
        var result;
        //if there's a filter present, we should perform a Lucene search instead of reading the table

        if (buildingId && floorId) {
            luceneQuery = 'buildingId:"' + buildingId + '" AND floorId:"' + floorId + '" AND ' + luceneQuery;
        } else {
            luceneQuery = 'buildingId:"' + buildingId + '" AND ' + luceneQuery;
        }
        var filter = {
            "query": luceneQuery,
            "start": startCount,
            "count": limitCount,
            "sortBy" : [{
                "field" : "timeStamp",
                "sortType" : sort
            }]
        };
        result = connector.search(loggedInUser, tableName, stringify(filter)).getMessage();

        result = JSON.parse(result);
        var data = [];
        for (var i = 0; i < result.length; i++) {
            var values = result[i].values;
            data.push(values);
        }
        return data;
    };

}());
