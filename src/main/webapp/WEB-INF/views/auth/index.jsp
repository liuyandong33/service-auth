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
    <script type="text/javascript" src="http://localhost:8888/libraries/jquery/jquery-3.2.1.min.js"></script>
    <script type="text/javascript">
        function login() {
            var username = $("#username").val();
            var password = $("#password").val();

            $.post("/auth/login", {username: username, password: password}, function (result) {
                console.log(result)
            }, "json");
        }
    </script>
</head>
<body>
<div style="text-align: center;">
    <form method="post" action="/auth/login">
        用户名：<input type="text" id="username" name="username"><br><br>
        密码： <input type="password" id="password" name="password"><br><br>
        <input type="submit" value="登录">
    </form>

    <button onclick="login();">登录</button>
</div>
</body>
</html>
