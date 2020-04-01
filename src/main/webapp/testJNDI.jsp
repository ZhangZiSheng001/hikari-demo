<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="cn.zzs.mybatis.JDBCUtils"%>
<%@page import="javax.sql.PooledConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.InitialContext"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <%
    	String jndiName = "java:comp/env/jdbc/hikariCP-test";
        
        InitialContext ic = new InitialContext();
        // 获取JNDI上的ComboPooledDataSource
        DataSource ds = (DataSource) ic.lookup(jndiName);
        
        JDBCUtils.setDataSource(ds);

        // 创建sql
        String sql = "select * from demo_user where deleted = false";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        // 查询用户
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
                System.err.println("用户名：" + name + ",年龄：" + age);
            }
        } catch(SQLException e) {
            System.err.println("查询用户异常");
        } finally {
            // 释放资源
            JDBCUtils.release(connection, statement, resultSet);
        }
    %>
</body>
</html>