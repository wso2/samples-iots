package org.wso2.iot.senseme.api.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.IllegalTransactionStateException;
import org.wso2.iot.senseme.api.constants.DeviceTypeConstants;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//import org.wso2.carbon.device.mgt.extensions.device.type.deployer.exception.DeviceTypeDeployerFileException;
//import org.wso2.carbon.device.mgt.extensions.device.type.deployer.exception.DeviceTypeMgtPluginException;

/**
 * This component handles the connections
 */
public class BuildingDAOHandler {

    private static final Log log = LogFactory.getLog(BuildingDAOHandler.class);

    private static DataSource dataSource;
    private static ThreadLocal<Connection> currentConnection = new ThreadLocal<Connection>();

    public BuildingDAOHandler() {

        String datasourceName =DeviceTypeConstants.DATA_SOURCE_NAME;
        initDAO(datasourceName);
    }

    public void initDAO(String datasourceName) {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(datasourceName);
        } catch (NamingException e) {
            log.debug(e.getMessage());
        }
    }

    public void beginTransaction()  {
        try {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            currentConnection.set(conn);
        } catch (SQLException e) {
            log.debug(e.getMessage());
        }
    }

    /**
     * Open connection to the datasource for read data
     *
     * @throws SQLException
     */
    public void openConnection() throws SQLException {
        Connection conn = currentConnection.get();
        if (conn != null) {
            throw new IllegalTransactionStateException("A transaction is already active within the context of " +
                                                               "this particular thread. Therefore, calling 'beginTransaction/openConnection' while another " +
                                                               "transaction is already active is a sign of improper transaction handling");
        }
        conn = dataSource.getConnection();
        currentConnection.set(conn);
    }

    public static Connection getConnection()  {
        if (currentConnection.get() == null) {
            try {
                currentConnection.set(dataSource.getConnection());
            } catch (SQLException e) {
                log.debug(e.getMessage());
            }
        }
        return currentConnection.get();
    }

    public void commitTransaction() {
        try {
            Connection conn = currentConnection.get();
            if (conn != null) {
                conn.commit();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Datasource connection associated with the current thread is null, hence commit "
                                      + "has not been attempted");
                }
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void closeConnection()  {

        Connection con = currentConnection.get();
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("Error occurred while close the connection");
            }
        }
        currentConnection.remove();
    }

    public void rollbackTransaction()  {
        try {
            Connection conn = currentConnection.get();
            if (conn != null) {
                conn.rollback();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Datasource connection associated with the current thread is null, hence rollback "
                                      + "has not been attempted");
                }
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
        } finally {
            closeConnection();
        }
    }
}