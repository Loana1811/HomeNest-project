<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String username = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("username".equals(cookie.getName())) {
                username = cookie.getValue();
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f0f2f5;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .login-wrapper {
            max-width: 420px;
            margin: 100px auto;
            padding: 30px 35px;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 6px 15px rgba(0, 128, 128, 0.1);
        }
        .title {
            margin-bottom: 20px;
            color: rgb(0, 128, 128);
            text-align: center;
        }
        label {
            font-weight: 500;
            margin-bottom: 5px;
        }
        .form-control:focus {
            border-color: rgb(0, 128, 128);
            box-shadow: 0 0 5px rgba(0, 128, 128, 0.3);
        }
        .btn-primary {
            background-color: rgb(0, 128, 128);
            border: none;
        }
        .btn-primary:hover {
            background-color: #006666;
        }
        .text-end a {
            font-size: 14px;
            color: rgb(0, 128, 128);
        }
        .text-end a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-wrapper">
        <h4 class="title">Login</h4>
        <c:if test="${not empty error}">
            <div class="alert alert-danger text-center">${error}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/Login" method="post">
            <div class="form-group mb-3">
                <label for="user_name">Email or Phone Number</label>
                <input type="text" class="form-control" name="_username" id="user_name"
                       value="<%= username %>" placeholder="Enter your email or phone number" required>
            </div>
            <div class="form-group mb-3">
                <label for="login_password">Password</label>
                <input type="password" class="form-control" name="_password" id="login_password"
                       placeholder="Enter your password" required>
            </div>
            <div class="form-check mb-3">
                <input type="checkbox" class="form-check-input" id="remember" name="remember">
                <label class="form-check-label" for="remember">Remember me</label>
            </div>
            <div class="d-grid mb-2">
                <button type="submit" class="btn btn-primary">Log In</button>
            </div>
        </form>
        <div class="text-end mb-3">
            <a href="${pageContext.request.contextPath}/forgot_password" class="text-decoration-none">Forgot password?</a>
        </div>
    </div>
</body>
</html>