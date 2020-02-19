package cn.zzs.hikari;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

/**
 * <p>测试使用JDBCUtils获取连接并操作数据库</p>
 * @author: zzs
 * @date: 2019年8月31日 下午9:39:54
 */
public class HikariDataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(HikariDataSourceTest.class);

	/**
	 * 测试添加用户
	 */
	@Test
	public void save() {
		// 创建sql
		String sql = "insert into demo_user values(null,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			// 获得连接
			connection = JDBCUtils.getConnection();
			// 开启事务设置非自动提交
			JDBCUtils.startTrasaction();
			// 获得Statement对象
			statement = connection.prepareStatement(sql);
			// 设置参数
			statement.setString(1, "zzf003");
			statement.setInt(2, 18);
			statement.setDate(3, new Date(System.currentTimeMillis()));
			statement.setDate(4, new Date(System.currentTimeMillis()));
			statement.setBoolean(5, false);
			// 执行
			statement.executeUpdate();
			// 提交事务
			JDBCUtils.commit();
		} catch(Exception e) {
			JDBCUtils.rollback();
			logger.error("保存用户失败", e);
		} finally {
			// 释放资源
			JDBCUtils.release(connection, statement, null);
		}
	}

	/**
	 * 测试更新用户
	 */
	@Test
	public void update() {
		// 创建sql
		String sql = "update demo_user set age = ?,gmt_modified = ? where name = ?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			// 获得连接
			connection = JDBCUtils.getConnection();
			// 开启事务
			JDBCUtils.startTrasaction();
			// 获得Statement对象
			statement = connection.prepareStatement(sql);
			// 设置参数
			statement.setInt(1, 19);
			statement.setDate(2, new Date(System.currentTimeMillis()));
			statement.setString(3, "zzf003");
			// 执行
			statement.executeUpdate();
			// 提交事务
			JDBCUtils.commit();
		} catch(Exception e) {
			logger.error("异常导致操作回滚", e);
			JDBCUtils.rollback();
		} finally {
			// 释放资源
			JDBCUtils.release(connection, statement, null);
		}
	}

	/**
	 * 测试查找用户
	 */
	@Test
	public void findAll() {
		// 创建sql
		String sql = "select * from demo_user where deleted = false";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			// 获得连接
			connection = JDBCUtils.getConnection();
			// 获得Statement对象
			statement = connection.prepareStatement(sql);
			// 执行
			resultSet = statement.executeQuery();
			// 遍历结果集
			while(resultSet.next()) {
				String name = resultSet.getString(2);
				int age = resultSet.getInt(3);
				System.out.println("用户名：" + name + ",年龄：" + age);
			}
		} catch(SQLException e) {
			logger.error("查询用户异常", e);
		} finally {
			// 释放资源
			JDBCUtils.release(connection, statement, resultSet);
		}
	}

	/**
	 * 测试删除用户
	 */
	@Test
	public void delete() throws Exception {
		// 创建sql
		String sql = "delete from demo_user where name = ?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			// 获得连接
			connection = JDBCUtils.getConnection();
			// 设置非自动提交
			JDBCUtils.startTrasaction();
			// 获得Statement对象
			statement = connection.prepareStatement(sql);
			// 设置参数
			statement.setString(1, "zzf003");
			// 执行
			statement.executeUpdate();
			// 提交事务
			JDBCUtils.commit();
		} catch(Exception e) {
			logger.error("异常导致操作回滚", e);
			JDBCUtils.rollback();
		} finally {
			// 释放资源
			JDBCUtils.release(connection, statement, null);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new HikariDataSourceTest().findAll();
		Thread.sleep(60 * 60 * 1000);
	}

}
