<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2019/5/18
  Time: 6:36 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div style="text-align: center;">
    <form method="post" action="/auth/login">
        用户名：<input type="text" id="username" name="username"><br><br>
        密码： <input type="password" id="password" name="password"><br><br>
        <input type="submit" value="登录">
    </form>
</div>
</body>
</html>
