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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is used to create database for the cache.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper helper;

    private static final String DATABASE_NAME = "cache_db";
    private static final int DATABASE_VERSION = 1;

    public static final String CACHE_TABLE_NAME = "cache";
    public static final String CACHE_TABLE_ID = "id";
    public static final String CACHE_TABLE_TOPIC = "topic";
    public static final String CACHE_TABLE_MESSAGE = "message";
    public static final String CACHE_TABLE_RECEIVED_TIME = "received_time";

    private static final String CREATE_CACHE_TABLE = "CREATE TABLE " + CACHE_TABLE_NAME +
            "(" + CACHE_TABLE_ID + " integer primary key autoincrement, " +
            CACHE_TABLE_TOPIC + " text not null, " +
            CACHE_TABLE_MESSAGE + " text not null, " +
            CACHE_TABLE_RECEIVED_TIME + " text not null)";

    private static final String DROP_CACHE_TABLE = "DROP TABLE IF EXISTS " + CACHE_TABLE_NAME;

    public static synchronized DBHelper getInstance(Context context){
        if(helper == null){
            helper = new DBHelper(context);
        }
        return helper;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CACHE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CACHE_TABLE);
        onCreate(db);
    }

}
