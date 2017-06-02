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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class contains the database CRUD operations that are related to cache table.
 */
public class CacheDAO {

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public CacheDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public synchronized void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            db.beginTransaction();
        }
    }

    public void close() {
        if(db != null && db.isOpen()){
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public void addEntry(CacheEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.CACHE_TABLE_TOPIC, entry.getTopic());
        values.put(DBHelper.CACHE_TABLE_MESSAGE, entry.getMessage());
        values.put(DBHelper.CACHE_TABLE_RECEIVED_TIME, getCurrentTime());
        db.insert(DBHelper.CACHE_TABLE_NAME, null, values);
    }

    public CacheEntry getEntry(int id) {
        Cursor result =  db.rawQuery("SELECT * FROM " + DBHelper.CACHE_TABLE_NAME + " WHERE id = " + id, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            return cursorToCacheEntry(result);
        }
        return null;
    }

    public int removeEntry(int id) {
       return db.delete(DBHelper.CACHE_TABLE_NAME, DBHelper.CACHE_TABLE_ID + "=" + id, null);
    }

    public int removeAllEntries() {
        return db.delete(DBHelper.CACHE_TABLE_NAME, null, null);
    }

    public List<CacheEntry> getAllEntries() {
        List<CacheEntry> entries = new ArrayList<CacheEntry>();
        Cursor result = db.rawQuery("SELECT * FROM " + DBHelper.CACHE_TABLE_NAME, null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            CacheEntry entry = cursorToCacheEntry(result);
            entries.add(entry);
            result.moveToNext();
        }
        result.close();
        return entries;
    }

    private CacheEntry cursorToCacheEntry(Cursor cursor) {
        CacheEntry entry = new CacheEntry();
        entry.setId(cursor.getInt(0));
        entry.setTopic(cursor.getString(1));
        entry.setMessage(cursor.getString(2));
        entry.setReceivedTime(cursor.getString(3));
        return entry;
    }

    private String getCurrentTime() {
        return Calendar.getInstance().getTime().toString();
    }
}
