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

package org.wso2.androidtv.agent.h2cache;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
 * This is used to create a single instance of of a data source to get the database connection
 */

public class DataSource {

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource Hikarids;

    static {

        try {
            Class.forName("org.h2.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        config.setJdbcUrl( "jdbc:h2:/data/data/org.wso2.androidtv.agent/data/edgeTVGateway;" +
                "FILE_LOCK=FS;PAGE_SIZE=1024;CACHE_SIZE=8192" );
        config.setMaximumPoolSize(40);
        config.setUsername( "admin" );
        config.setPassword( "admin" );
        config.setMaxLifetime(60000);
        config.setMinimumIdle(13);
        config.setIdleTimeout(40000);
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        Hikarids = new HikariDataSource( config );

    }

    private DataSource() {
    }

    static Connection getConnection() throws SQLException {
        return Hikarids.getConnection();
    }
}
