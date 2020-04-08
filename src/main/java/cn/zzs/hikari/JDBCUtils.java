package cn.zzs.hikari;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * <p>用于获取数据库连接对象的工具类。</p>
 * @author: zzs
 * @date: 2019年12月8日 上午10:13:16
 */
public class JDBCUtils {

    private static DataSource dataSource;

    private static ThreadLocal<Connection> tl = new ThreadLocal<>();

    private static final Logger log = LoggerFactory.getLogger(JDBCUtils.class);

    static {
        init();
    }

    /**
     * 
     * <p>获取数据库连接对象的方法，线程安全</p>
     * @author: zzs
     * @date: 2019年8月31日 下午9:22:29
     * @return: Connection
     */
    public static Connection getConnection() throws SQLException {
        // 从当前线程中获取连接对象
        Connection connection = tl.get();
        // 判断为空的话，创建连接并绑定到当前线程
        if(connection == null) {
            connection = createConnection();
            tl.set(connection);
        }
        return connection;
    }

    /**
     * 
     * <p>释放资源</p>
     * @author: zzs
     * @date: 2019年8月31日 下午9:39:24
     * @param conn
     * @param statement
     * @return: void
     */
    public static void release(Connection conn, Statement statement, ResultSet resultSet) {
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch(SQLException e) {
                log.error("关闭ResultSet对象异常", e);
            }
        }
        if(statement != null) {
            try {
                statement.close();
            } catch(SQLException e) {
                log.error("关闭Statement对象异常", e);
            }
        }
        if(conn != null) {
            try {
                conn.close();
                tl.remove();
            } catch(SQLException e) {
                log.error("关闭Connection对象异常", e);
            }
        }
    }

    /**
     * 
     * <p>开启事务</p>
     * @author: zzs
     * @date: 2019年11月3日 上午11:03:24
     * @return: void
     * @throws SQLException 
     */
    public static void startTrasaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    /**
     * 
     * <p>提交事务</p>
     * @author: zzs
     * @date: 2019年11月3日 上午11:05:54
     * @return: void
     * @throws SQLException 
     */
    public static void commit() throws SQLException {
        Connection connection = tl.get();
        if(connection == null) {
            throw new RuntimeException("未开启事务");
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * 
     * <p>回滚事务</p>
     * @author: zzs
     * @date: 2019年11月3日 上午11:08:12
     * @return: void
     * @throws SQLException 
     */
    public static void rollback() throws SQLException {
        Connection connection = tl.get();
        if(connection != null) {
            connection.rollback();
            connection.setAutoCommit(true);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void setDataSource(DataSource dataSource) {
        JDBCUtils.dataSource = dataSource;
        tl.remove();
    }

    /**
     * 
     * <p>创建数据库连接</p>
     * @author: zzs
     * @date: 2019年8月31日 下午9:27:03
     * @return: Connection
     * @throws SQLException 
     */
    private static Connection createConnection() throws SQLException {
        Connection conn = null;
        // 获得连接
        conn = dataSource.getConnection();
        return conn;
    }

    /**
     * <p>根据指定配置文件创建数据源对象</p>
     * @author: zzs
     * @date: 2019年9月1日 上午10:53:05
     * @return: void
     */
    private static void init() {
        try {
            HikariConfig config = new HikariConfig("/hikari.properties");
            dataSource = new HikariDataSource(config);
        } catch(Exception e) {
            throw new RuntimeException("创建数据源失败", e);
        }
    }
}
