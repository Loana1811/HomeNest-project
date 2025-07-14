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

            .google-btn {
                background-color: #4285F4;
                color: white;
                font-weight: bold;
                border: none;
            }

            .google-btn:hover {
                background-color: #3367D6;
            }

            .text-end a {
                font-size: 14px;
                color: rgb(0, 128, 128);
            }

            .text-end a:hover,
            .text-center a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>

        <div class="login-wrapper">
            <h4 class="title">Login</h4>

            <!-- Display error message if any -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger text-center">${error}</div>
            </c:if>

            <!-- Standard login form -->
            <form action="Login" method="post">
                <div class="form-group mb-3">
                    <label for="user_name">Email</label>
                    <input type="text" class="form-control" name="_username" id="user_name"
                           value="<%= username%>" placeholder="Enter your email" required>
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

            <!-- Google Login -->
            <div class="text-center mt-3">
                <form action="https://accounts.google.com/o/oauth2/auth" method="get">
                    <input type="hidden" name="scope" value="email profile">
                    <input type="hidden" name="redirect_uri" value="http://localhost:8080/SWP301/login-google">
                    <input type="hidden" name="response_type" value="code">
                    <input type="hidden" name="client_id" value="105183721878-bqd17ufa649vuo2qqogsjiqbuboskpin.apps.googleusercontent.com">
                    <input type="hidden" name="approval_prompt" value="force">
                    <input type="hidden" name="access_type" value="offline">
                    <button type="submit" class="btn google-btn w-100">Log in with Google</button>
                </form>
            </div>

            <div class="d-grid mt-3">
                <a href="SortTour" class="btn btn-secondary">Back to Homepage</a>
            </div>

            <div class="text-end mb-3">
                <a href="<%= request.getContextPath()%>/forgot_password" class="text-decoration-none">Forgot password?</a>
            </div>

            <div class="text-center mt-3">
                Don't have an account? <a href="<%= request.getContextPath()%>/RegisterCustomer">Register here</a>
            </div>
        </div>

    </body>
</html>
