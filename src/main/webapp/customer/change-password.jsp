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
            background-color: #f3f6fc;
            font-family: 'Segoe UI', sans-serif;
        }

        .change-password-container {
            max-width: 720px;
            margin: 80px auto;
            background-color: white;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            animation: fadeIn 0.5s ease-in-out;
        }

        h2 {
            text-align: center;
            color: #1e3a8a;
            margin-bottom: 30px;
            font-weight: bold;
        }

        .form-label {
            font-weight: 600;
            color: #1e3a8a;
        }

        .btn-primary {
            background-color: #1e3a8a;
            border-color: #1e3a8a;
            font-weight: 600;
        }

        .btn-primary:hover {
            background-color: #162f67;
            border-color: #162f67;
        }

        .btn-secondary {
            background-color: #fff;
            color: #1e3a8a;
            border: 1.5px solid #1e3a8a;
            font-weight: 600;
        }

        .btn-secondary:hover {
            background-color: #e0e7ff;
            color: #1e3a8a;
        }

        .alert {
            font-size: 14px;
            border-radius: 10px;
        }

        .alert-danger {
            background-color: #fee2e2;
            color: #b91c1c;
            border-left: 4px solid #f87171;
        }

        .alert-success {
            background-color: #e0f2fe;
            color: #2563eb;
            border-left: 4px solid #3b82f6;
        }

        .btn-close {
            filter: brightness(0.5);
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body>
    <div class="mb-4">
        <a href="<%= request.getContextPath() %>/customer/view-profile" class="btn btn-success btn-lg text-white">
            <i class="bi bi-arrow-left-circle me-2"></i> Back to Profile
        </a>
    </div>

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

        <!-- Client-side validation alert -->
        <div id="passwordError" class="alert alert-danger d-none" role="alert"></div>

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
                <button type="submit" class="btn btn-primary">Update Password</button>
                <a href="${pageContext.request.contextPath}/customer/view-profile" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
  <%@include file = "/WEB-INF/inclu/footer.jsp" %>
    <script>
        function validatePassword() {
            const newPassword = document.getElementById("newPassword").value;
            const confirmPassword = document.getElementById("confirmPassword").value;
            const errorDiv = document.getElementById("passwordError");
            const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_])[A-Za-z\d\W_]{8,20}$/;

            errorDiv.classList.add("d-none");
            errorDiv.innerHTML = "";

            if (/\s/.test(newPassword)) {
                errorDiv.innerHTML = "Password must not contain whitespace.";
                errorDiv.classList.remove("d-none");
                return false;
            }

            if (!pattern.test(newPassword)) {
                errorDiv.innerHTML = "Password must be 8–20 characters, contain uppercase, lowercase, digit, and special character.";
                errorDiv.classList.remove("d-none");
                return false;
            }

            if (newPassword !== confirmPassword) {
                errorDiv.innerHTML = "New password and confirmation do not match.";
                errorDiv.classList.remove("d-none");
                return false;
            }

            return true;
        }
    </script>
</body>
</html>
