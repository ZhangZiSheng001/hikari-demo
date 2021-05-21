package cn.zzs.hikari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cn.zzs.hikari.entity.User;
import cn.zzs.hikari.util.CollectionUtils;
import cn.zzs.hikari.util.IdUtils;

/**
 * 测试HikariCP
 * @author zzs
 * @date 2021/5/2
 * @version 1.0.0
 */
public class HikariCPTest {

    private HikariDataSource dataSource;

    private static final String EXIST_USER_ID = "11111111111111111111111111111111";

    @Before
    public void setup() {
        // 加载配置文件，也可以使用启动参数 hikaricp.configurationFile 指定
        HikariConfig config = new HikariConfig("/hikaricp.properties");
        dataSource = new HikariDataSource(config);
    }

    @After
    public void tearDown() {
        dataSource.close();
    }

    /**
     * 根据id获取用户
     */
    @Test
    public void getById() throws SQLException {
        User user = getById(EXIST_USER_ID);
        System.err.println(user);
    }

    private User getById(String id) throws SQLException {
        Connection connection = dataSource.getConnection();
        // 创建PreparedStatement对象
        String sql = "select * from demo_user where id = ?";
        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        // 入参映射
        prepareStatement.setString(1, id);
        // 执行sql
        ResultSet resultSet = prepareStatement.executeQuery();
        // 出参映射
        List<User> list = mapResult(resultSet);
        // 释放资源
        resultSet.close();
        prepareStatement.close();
        connection.close();
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    /**
     * 出参映射
     */
    private List<User> mapResult(ResultSet resultSet) throws SQLException {
        List<User> list = new ArrayList<User>();
        while (resultSet.next()) {
            User user = new User();
            user = new User();
            user.setId(resultSet.getString(1));
            user.setName(resultSet.getString(2));
            user.setGender(resultSet.getInt(3));
            user.setAge(resultSet.getInt(4));
            user.setGmt_create(resultSet.getDate(5));
            user.setGmt_modified(resultSet.getDate(6));
            user.setDeleted(resultSet.getInt(7));
            user.setPhone(resultSet.getString(8));
            list.add(user);
        }
        return list;
    }

    /**
     * 测试添加用户
     */
    @Test
    public void save() throws SQLException {
        // 创建用户实体
        User user = new User(
                IdUtils.randomUUID(),
                "zzf001",
                0,
                18,
                new Date(),
                new Date(),
                "188******26"
        );
        // 持久化
        save(user);
    }

    private void save(User user) throws SQLException {
        Connection connection = dataSource.getConnection();
        // 开启事务
        connection.setAutoCommit(false);
        // 创建PreparedStatement对象
        String sql = "insert into demo_user (id,name,gender,age,gmt_create,gmt_modified,deleted,phone) values(?,?,?,?,?,?,?,?)";
        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        // 入参的映射
        prepareStatement.setString(1, user.getId());
        prepareStatement.setString(2, user.getName());
        prepareStatement.setInt(3, user.getGender());
        prepareStatement.setInt(4, user.getAge());
        prepareStatement.setTimestamp(5, Timestamp.from(Instant.now()));
        prepareStatement.setTimestamp(6, Timestamp.from(Instant.now()));
        prepareStatement.setInt(7, 0);
        prepareStatement.setString(8, user.getPhone());
        // 执行sql
        int result = prepareStatement.executeUpdate();
        System.err.println(result);
        // 提交事务
        connection.commit();
        // 释放资源
        prepareStatement.close();
        connection.close();
    }

    /**
     * 测试更新用户
     */
    @Test
    public void update() throws Exception {
        // 获取并更改用户实体
        User user = getById(EXIST_USER_ID);
        user.setAge(17);
        user.setGmt_modified(new Date());
        // 持久化
        update(user);
    }

    private void update(User user) throws SQLException {
        Connection connection = dataSource.getConnection();
        // 开启事务
        connection.setAutoCommit(false);
        // 创建PreparedStatement对象
        String sql = "update demo_user set name = ?,gender = ?,age = ?,gmt_modified = ?,phone = ? where id = ?";
        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        // 入参的映射
        prepareStatement.setString(1, user.getName());
        prepareStatement.setInt(2, user.getGender());
        prepareStatement.setInt(3, user.getAge());
        prepareStatement.setTimestamp(4, Timestamp.from(Instant.now()));
        prepareStatement.setString(5, user.getPhone());
        prepareStatement.setString(6, user.getId());
        // 执行sql
        int result = prepareStatement.executeUpdate();
        System.err.println(result);
        // 提交事务
        connection.commit();
        // 释放资源
        prepareStatement.close();
        connection.close();
    }

    /**
     * 测试查找所有用户
     */
    @Test
    public void find() throws SQLException {
        List<User> list = findAll();
        list.stream().forEach(System.err::println);
    }


    private List<User> findAll() throws SQLException {
        Connection connection = dataSource.getConnection();
        // 创建PreparedStatement对象
        String sql = "select * from demo_user where deleted = 0";
        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        // 执行sql
        ResultSet resultSet = prepareStatement.executeQuery();
        // 出参的映射
        List<User> list = mapResult(resultSet);
        // 释放资源
        resultSet.close();
        prepareStatement.close();
        connection.close();
        return list;
    }

    /**
     * 测试删除用户
     */
    @Test
    public void delete() throws SQLException {
        //持久化
        delete(EXIST_USER_ID);
    }


    private void delete(String id) throws SQLException {
        Connection connection = dataSource.getConnection();
        // 开启事务
        connection.setAutoCommit(false);
        // 创建PreparedStatement对象
        String sql = "delete from demo_user where id = ?";
        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        // 入参的映射
        prepareStatement.setString(1, id);
        // 执行sql
        prepareStatement.executeUpdate();
        // 提交事务
        // connection.commit();
        // 回滚事务
        connection.rollback();
        // 释放资源
        prepareStatement.close();
        connection.close();
    }

    public static void main(String[] args) throws InterruptedException {
        HikariConfig config = new HikariConfig("/hikaricp_base.properties");
        HikariDataSource dataSource = new HikariDataSource(config);
        
        Thread.sleep(20 * 60 * 1000);
        
        dataSource.close();
    }
}
