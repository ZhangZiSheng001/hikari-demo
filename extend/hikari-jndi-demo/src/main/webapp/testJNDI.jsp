<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="javax.sql.PooledConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="cn.zzs.hikari.entity.User"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
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
        DataSource dataSource = (DataSource) ic.lookup(jndiName);
        
        // 创建sql
        String sql = "select * from demo_user where deleted = 0";
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;
        
        // 查询用户
        try {
            connection = dataSource.getConnection();
            // 创建PreparedStatement对象
            prepareStatement = connection.prepareStatement(sql);
            // 执行sql
            resultSet = prepareStatement.executeQuery();
            // 出参的映射
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
            // 释放资源
            list.stream().forEach(System.err::println);
        } catch(Exception e) {
            // do nothing
        } finally {
            // 释放资源
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch(Exception e){
                }
            }
            if(prepareStatement != null){
                try {
                    prepareStatement.close();
                } catch(Exception e){
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch(Exception e){
                }
            }
        }
    %>
</body>
</html>