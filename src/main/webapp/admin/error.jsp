<%-- 
    Document   : error
    Created on : Jun 13, 2025, 5:17:04 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <meta charset="UTF-8">
</head>
<body style="font-family: sans-serif; padding: 30px;">
    <h2 style="color: red;">Oops! Something went wrong.</h2>
    <p>${errorMessage}</p>
    <a href="${pageContext.request.contextPath}/category?action=list">Go back to category list</a>
</body>
</html>
