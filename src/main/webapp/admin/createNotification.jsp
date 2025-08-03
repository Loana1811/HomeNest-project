<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Customer"%>
<%@page import="model.User"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Notification</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --primary: #1e3a8a;
            --primary-dark: #16387d;
            --secondary: #6c757d;
            --success: #28a745;
            --danger: #dc3545;
            --light: #f8f9fa;
            --input-bg: #fff;
            --input-border: #ced4da;
        }

        body {
            background: linear-gradient(to right, #e9ecef, #f8f9fc);
            font-family: 'Segoe UI', sans-serif;
        }

        .header {
            background: linear-gradient(90deg, var(--primary), var(--secondary));
            color: #fff;
            padding: 1.5rem;
            border-radius: 12px;
            text-align: center;
            font-size: 2rem;
            font-weight: 600;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            margin-bottom: 2rem;
        }

        .container {
            max-width: 960px;
            margin: auto;
            background: #ffffff;
            padding: 2rem 2.5rem;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
        }

        .form-label {
            font-weight: 600;
            color: #333;
        }

        input[type="text"],
        textarea,
        select {
            width: 100%;
            padding: 12px 16px;
            font-size: 1rem;
            border: 1px solid var(--input-border);
            border-radius: 12px;
            background-color: var(--input-bg);
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.02);
            transition: 0.3s ease;
        }

        input:focus,
        textarea:focus,
        select:focus {
            border-color: var(--primary);
            outline: none;
            box-shadow: 0 0 0 4px rgba(30, 58, 138, 0.12);
            transform: scale(1.01);
        }

        textarea {
            resize: vertical;
            min-height: 120px;
        }

        input[type="checkbox"] {
            transform: scale(1.2);
            accent-color: var(--primary);
            cursor: pointer;
        }

        .table {
            margin-top: 1.5rem;
            border-radius: 12px;
            overflow: hidden;
            background-color: #fff;
            box-shadow: 0 3px 12px rgba(0, 0, 0, 0.04);
        }

        .table th {
            background: var(--primary);
            color: #fff;
            text-align: center;
            padding: 14px;
        }

        .table td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
        }

        .table tr:nth-child(even) {
            background-color: #f7f9fc;
        }

        .table tr:hover {
            background-color: #eef2ff;
            transition: 0.2s;
        }

        button[type="submit"] {
            display: inline-block;
            padding: 12px 24px;
            font-size: 1rem;
            font-weight: 600;
            color: white;
            background: linear-gradient(to right, #1e3a8a, #3b82f6);
            border: none;
            border-radius: 10px;
            margin-top: 1.5rem;
            transition: all 0.3s ease;
        }

        button[type="submit"]:hover {
            background: linear-gradient(to right, #16387d, #2563eb);
            transform: translateY(-2px);
            box-shadow: 0 8px 18px rgba(30, 58, 138, 0.2);
        }

        .alert {
            border-radius: 10px;
            padding: 1rem 1.2rem;
            font-size: 1rem;
        }

        .error-message {
            color: var(--danger);
            font-size: 0.88rem;
            margin-top: 6px;
            display: none;
        }
    </style>

</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1>Create Notification</h1>
        </div>

        <!-- Success Message -->
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form id="notificationForm" action="${pageContext.request.contextPath}/admin/notification" method="post" onsubmit="return validateForm()">
            <input type="hidden" name="action" value="createNotification">
            <input type="hidden" name="idUser" value="${idUser}">

            <div class="form-group">
                <label for="title" class="form-label">Title</label>
                <input type="text" class="form-control" id="title" name="title" required>
                <div id="titleError" class="error-message">Title must be at least 5 characters.</div>
            </div>

            <div class="form-group">
                <label for="message" class="form-label">Message Content</label>
                <textarea class="form-control" id="message" name="message" rows="3" required></textarea>
                <div id="messageError" class="error-message">Message must be at least 10 characters.</div>
            </div>

            <div class="form-group">
                <label class="form-label">Select Customers</label>
                <div>
                    <input type="checkbox" id="selectAllCustomers"> Select all customers
                </div>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Select</th>
                            <th>Full Name</th>
                            <th>Phone</th>
                            <th>Email</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="customer" items="${customerList}">
                            <tr>
                                <td><input type="checkbox" name="customerIds" value="${customer.customerID}" class="customerCheckbox"></td>
                                <td>${customer.customerFullName}</td>
                                <td>${customer.phoneNumber}</td>
                                <td>${customer.email}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty customerList}">
                            <tr><td colspan="4">No customers found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="form-group">
                <label class="form-label">Select Managers</label>
                <div>
                    <input type="checkbox" id="selectAllManagers"> Select all managers
                </div>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Select</th>
                            <th>Full Name</th>
                            <th>Phone</th>
                            <th>Email</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="manager" items="${managerList}">
                            <tr>
                                <td><input type="checkbox" name="managerIds" value="${manager.userID}" class="managerCheckbox"></td>
                                <td>${manager.userFullName}</td>
                                <td>${manager.phoneNumber}</td>
                                <td>${manager.email}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty managerList}">
                            <tr><td colspan="4">No managers found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <div id="recipientError" class="error-message">Please select at least one customer or manager.</div>

            <button type="submit" class="btn btn-primary">Send Notification</button>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Select all customers
        document.getElementById('selectAllCustomers').addEventListener('click', function () {
            let checkboxes = document.querySelectorAll('.customerCheckbox');
            for (let checkbox of checkboxes) {
                checkbox.checked = this.checked;
            }
        });

        // Select all managers
        document.getElementById('selectAllManagers').addEventListener('click', function () {
            let checkboxes = document.querySelectorAll('.managerCheckbox');
            for (let checkbox of checkboxes) {
                checkbox.checked = this.checked;
            }
        });

        // Form validation
        function validateForm() {
            let isValid = true;

            const title = document.getElementById('title').value.trim();
            const titleError = document.getElementById('titleError');
            if (title.length < 5) {
                titleError.style.display = 'block';
                isValid = false;
            } else {
                titleError.style.display = 'none';
            }

            const message = document.getElementById('message').value.trim();
            const messageError = document.getElementById('messageError');
            if (message.length < 10) {
                messageError.style.display = 'block';
                isValid = false;
            } else {
                messageError.style.display = 'none';
            }

            const customerCheckboxes = document.querySelectorAll('.customerCheckbox');
            const managerCheckboxes = document.querySelectorAll('.managerCheckbox');
            const recipientError = document.getElementById('recipientError');
            let hasSelected = false;

            for (let checkbox of customerCheckboxes) {
                if (checkbox.checked) {
                    hasSelected = true;
                    break;
                }
            }

            if (!hasSelected) {
                for (let checkbox of managerCheckboxes) {
                    if (checkbox.checked) {
                        hasSelected = true;
                        break;
                    }
                }
            }

            if (!hasSelected) {
                recipientError.style.display = 'block';
                isValid = false;
            } else {
                recipientError.style.display = 'none';
            }

            return isValid;
        }
    </script>
</body>
</html>
