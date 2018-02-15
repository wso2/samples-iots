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

/*
 * This class creates the H2 database connection through the Hikari datasource. All the database
 * queries are included in this class.
 */

import android.content.ContextWrapper;
import android.provider.ContactsContract;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class H2Connection{

    private ContextWrapper contextWrapper;
    private Connection connection = null;
    private PreparedStatement ps = null;
    private static boolean tableExists=false;

    @SuppressWarnings("unchecked")
    private final List<String> dataRetrieved = new ArrayList();

    public H2Connection(ContextWrapper contextWrapper){
        this.contextWrapper =contextWrapper;
    }

    public H2Connection() {}

    public void initializeConnection(){
        File directory = contextWrapper.getFilesDir();
        System.out.println("h2 db directory :"+directory);
    }

    public void createQuery (String topic) throws SQLException {

        final String create_query = String.format("CREATE TABLE %s table(Id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, %s value CHAR ) ",topic,topic);

        try{
            connection = DataSource.getConnection();
            ps = connection.prepareStatement(create_query);
            ps.executeUpdate();
            synchronized (this){
                tableExists=true;
            }


        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(connection != null) connection.close();
            if(ps!=null) ps.close();
        }
    }

    public void insertQuery (String Data_to_insert, String data_topic) throws SQLException {

        final String persist_query = String.format("INSERT INTO %s table VALUES (NULL,'%s')" , data_topic, Data_to_insert);

        try{
            connection=DataSource.getConnection();
            ps = connection.prepareStatement(persist_query);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection != null) connection.close();
            if(ps!=null) ps.close();
        }
    }

    public List<String> retrieveData(String topic) throws SQLException {

        final String select_query= "SELECT * FROM "+topic+"table";
        connection = DataSource.getConnection();
        ps = connection.prepareStatement(select_query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            dataRetrieved.add(rs.getString(""+topic+"value"));
            System.out.println("tableIterate :"+ rs.getString(""+topic+"value"));
        }
        System.out.println("tableExists :"+tableExists);

        connection.close();
        if(ps!=null) ps.close();

        return dataRetrieved;
    }

    public void deleteQuery(String topic) throws SQLException {

        final String delete_query = "DELETE FROM "+topic+"table";

        connection = DataSource.getConnection();
        ps = connection.prepareStatement(delete_query);
        ps.executeUpdate();

        connection.close();
        if(ps!=null) ps.close();
    }
}
