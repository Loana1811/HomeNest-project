<%-- 
    Document   : change-password
    Created on : Jun 24, 2025, 3:08:42 AM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
        response.sendRedirect("../Login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Change Password</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <style>
            body {
                background-color: #d8f3dc;
                font-family: 'Segoe UI', sans-serif;
            }

            .change-password-container {
                max-width: 720px;
                margin: 80px auto;
                background-color: white;
                padding: 40px;
                border-radius: 20px;
                box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
            }

            h2 {
                text-align: center;
                color: #167c4c;
                margin-bottom: 30px;
                font-weight: bold;
            }

            .form-label {
                font-weight: 600;
            }

            .btn-primary {
                background-color: #167c4c;
                border-color: #167c4c;
                font-weight: 600;
            }

            .btn-primary:hover {
                background-color: #125f3a;
            }

            .btn-secondary {
                font-weight: 600;
            }

            .alert {
                font-size: 14px;
            }
        </style>
    </head>
    <body>

        <div class="change-password-container">
            <h2>Change Password</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/customer/change-password" method="post" onsubmit="return validatePassword();">
                <div class="mb-3">
                    <label for="currentPassword" class="form-label">Current Password</label>
                    <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                </div>

                <div class="mb-3">
                    <label for="newPassword" class="form-label">New Password</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                </div>

                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirm New Password</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                </div>

                <div class="d-flex justify-content-between">
                    <button type="submit" class="btn btn-primary">Change</button>
                    <a href="view-profile.jsp" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>

        <script>
            function validatePassword() {
                const newPassword = document.getElementById("newPassword").value;
                const confirmPassword = document.getElementById("confirmPassword").value;
                const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_])[A-Za-z\d\W_]{8,20}$/;

                if (/\s/.test(newPassword)) {
                    alert("Password must not contain spaces.");
                    return false;
                }

                if (!pattern.test(newPassword)) {
                    alert("Password must be 8–20 characters long, include uppercase, lowercase, number, special character, and no spaces.");
                    return false;
                }

                if (newPassword !== confirmPassword) {
                    alert("Passwords do not match.");
                    return false;
                }

                return true;
            }
        </script>

    </body>
</html>
