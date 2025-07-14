<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Forgot Password</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
        <style>
            body {
                background-color: #f0f2f5;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .forgot-wrapper {
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

            .btn-secondary {
                margin-top: 10px;
            }

            .alert {
                margin-top: 15px;
            }
        </style>
    </head>
    <body>

        <div class="forgot-wrapper">
            <h4 class="title">Reset Your Password</h4>

            <form action="<%= request.getContextPath()%>/forgot_password" method="post">
                <label for="email">Email address:</label>
                <input type="email" name="email" id="email" class="form-control" placeholder="Enter your email" required>
                <button type="submit" class="btn btn-primary w-100 mt-3">Send Reset Link</button>
            </form>

            <div class="d-grid">
                <a href="<%= request.getContextPath()%>/Login" class="btn btn-secondary w-100">Back to Login</a>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger text-center">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success text-center">${success}</div>
            </c:if>
        </div>

    </body>
</html>
