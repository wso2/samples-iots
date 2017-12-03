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

package org.wso2.androidtv.agent.cache;

import java.util.List;

/**
 * This interface represents the functions that associate with cache management
 */
public interface CacheManager {

    /**
     * This method is used to add a cache entry to the table.
     *
     * @param topic MQTT topic
     * @param message MQTT Message
     */
    void addCacheEntry(String topic, String message);
    /**
     * This method is used to retrieve a cache entry.
     *
     * @param id Id of the cache entry
     * @return returns the cache entry with respected to given id
     */
    CacheEntry getCacheEntry(int id);

    /**
     * This method will return the all the entries of the cache table.
     *
     * @return returns list of cache entries.
     */
    List<CacheEntry> getCacheEntries();

    /**
     * This method is used to remove a cache entry given an id.
     *
     * @param id cache entry id
     * @return returns the number of affected rows.
     */
    int removeCacheEntry(int id);

    /**
     * This method is used to remove all the cache entries of the table.
     *
     * @return returns the number of affected rows.
     */
    int removeAllCacheEntries();

}
