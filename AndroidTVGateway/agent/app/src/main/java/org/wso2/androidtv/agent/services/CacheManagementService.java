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

package org.wso2.androidtv.agent.services;

import android.content.Context;

import org.wso2.androidtv.agent.cache.CacheDAO;
import org.wso2.androidtv.agent.cache.CacheEntry;
import org.wso2.androidtv.agent.cache.CacheManager;

import java.util.List;

/**
 * This class represents the all the management services with respect to cache.
 */
public class CacheManagementService implements CacheManager {

    private CacheDAO cacheDAO;

    public CacheManagementService(Context context) {
        cacheDAO = new CacheDAO(context.getApplicationContext());
    }

    @Override
    public void addCacheEntry(String topic, String message) {
        CacheEntry entry = new CacheEntry();
        entry.setTopic(topic);
        entry.setMessage(message);
        try {
            cacheDAO.open();
            cacheDAO.addEntry(entry);
        } finally {
            cacheDAO.close();
        }
    }

    @Override
    public CacheEntry getCacheEntry(int id) {
        try {
            cacheDAO.open();
            return cacheDAO.getEntry(id);
        } finally {
            cacheDAO.close();
        }
    }

    @Override
    public List<CacheEntry> getCacheEntries() {
        try {
            cacheDAO.open();
            return cacheDAO.getAllEntries();
        } finally {
            cacheDAO.close();
        }
    }

    @Override
    public int removeCacheEntry(int id) {
        try {
            cacheDAO.open();
            return cacheDAO.removeEntry(id);
        } finally {
            cacheDAO.close();
        }
    }

    @Override
    public int removeAllCacheEntries() {
        try {
            cacheDAO.open();
            return cacheDAO.removeAllEntries();
        } finally {
            cacheDAO.close();
        }
    }

    public int getNumberOfEntries() {
        try {
            cacheDAO.open();
            return cacheDAO.getAllEntries().size();
        } finally {
            cacheDAO.close();
        }
    }
}
