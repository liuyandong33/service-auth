<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2019/5/18
  Time: 8:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>OAuth Approval</h1>
<p>Do you authorize 'client' to access your protected resources?</p>
<form id="confirmationForm" name="confirmationForm" action="/oauth/authorize" method="post">
    <input name="user_oauth_approval" value="true" type="hidden">
    <ul>
        <li>
            <div class='form-group'>scope.select:
                <input type="radio" name="scope.${scope}" value="true">Approve
                <input type="radio" name="scope.${scope}" value="false" checked="checked">Deny
            </div>
        </li>
    </ul>
    <label>
        <input name="authorize" value="Authorize" type="submit">
    </label>
</form>
</body>
</html>
