<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Access Denied</title>
    </head>
    <body>
        <h2 style="color: red;">⚠ Access Denied</h2>
        <p>You are not allowed to access this page directly.</p>
        <a href="${pageContext.request.contextPath}/customer/room-list">Back to Home</a>
    </body>
</html>