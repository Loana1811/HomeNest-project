<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .reset-container {
            max-width: 450px;
            margin: 60px auto;
            padding: 30px;
            border: 1px solid #dee2e6;
            border-radius: 10px;
            background-color: white;
            box-shadow: 0 4px 10px rgba(0, 128, 128, 0.2);
        }

        .btn-teal {
            background-color: rgb(0, 128, 128);
            color: white;
            border: none;
        }

        .btn-teal:hover {
            background-color: rgb(0, 100, 100);
            color: white;
        }

        .form-label {
            font-weight: bold;
            color: rgb(0, 100, 100);
        }

        .alert {
            max-width: 450px;
            margin: 20px auto;
        }
    </style>
</head>
<body>

<div class="reset-container">
    <h4 class="text-center mb-4" style="color: rgb(0, 100, 100);">Reset Your Password</h4>
    <form action="reset-password" method="post">
        <input type="hidden" name="token" value="${token}">
        
        <label for="password" class="form-label">New Password:</label>
        <input type="password" name="password" id="password" class="form-control" required>

        <label for="confirm" class="form-label mt-3">Confirm Password:</label>
        <input type="password" name="confirm" id="confirm" class="form-control" required>

        <button type="submit" class="btn btn-teal mt-4 w-100">Reset Password</button>
    </form>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger text-center">${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success text-center">${success}</div>
</c:if>

</body>
</html>
