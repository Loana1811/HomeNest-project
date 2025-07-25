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
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Thông Báo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --primary: #007bff;
            --secondary: #6c757d;
            --success: #28a745;
            --danger: #dc3545;
            --light: #f8f9fa;
        }
        body {
            background-color: #e9ecef;
            font-family: 'Roboto', sans-serif;
        }
        .header {
            background: linear-gradient(90deg, var(--primary), var(--secondary));
            color: white;
            padding: 1.5rem 0;
            margin-bottom: 1.5rem;
            border-radius: 0 0 15px 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 15px;
        }
        .form-group {
            margin-bottom: 1.5rem;
        }
        .alert {
            margin-bottom: 15px;
        }
        .error-message {
            color: var(--danger);
            font-size: 0.9rem;
            display: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1>Tạo Thông Báo</h1>
        </div>

        <!-- Success Message -->
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:if>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:if>

        <form id="notificationForm" action="${pageContext.request.contextPath}/admin/notification" method="post" onsubmit="return validateForm()">
            <input type="hidden" name="action" value="createNotification">
            <input type="hidden" name="idUser" value="${idUser}">

            <div class="form-group">
                <label for="title" class="form-label">Tiêu đề</label>
                <input type="text" class="form-control" id="title" name="title" required>
                <div id="titleError" class="error-message">Tiêu đề phải có ít nhất 5 ký tự.</div>
            </div>

            <div class="form-group">
                <label for="message" class="form-label">Nội dung thông báo</label>
                <textarea class="form-control" id="message" name="message" rows="3" required></textarea>
                <div id="messageError" class="error-message">Nội dung phải có ít nhất 10 ký tự.</div>
            </div>

            <div class="form-group">
                <label class="form-label">Chọn khách hàng</label>
                <div>
                    <input type="checkbox" id="selectAllCustomers"> Chọn tất cả khách hàng
                </div>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Chọn</th>
                            <th>Họ tên</th>
                            <th>Số điện thoại</th>
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
                            <tr><td colspan="4">Không tìm thấy khách hàng nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="form-group">
                <label class="form-label">Chọn quản lý</label>
                <div>
                    <input type="checkbox" id="selectAllManagers"> Chọn tất cả quản lý
                </div>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Chọn</th>
                            <th>Họ tên</th>
                            <th>Số điện thoại</th>
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
                            <tr><td colspan="4">Không tìm thấy quản lý nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <div id="recipientError" class="error-message">Vui lòng chọn ít nhất một khách hàng hoặc quản lý.</div>

            <button type="submit" class="btn btn-primary">Tạo Thông Báo</button>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Chọn tất cả khách hàng
        document.getElementById('selectAllCustomers').addEventListener('click', function () {
            let checkboxes = document.querySelectorAll('.customerCheckbox');
            for (let checkbox of checkboxes) {
                checkbox.checked = this.checked;
            }
        });

        // Chọn tất cả quản lý
        document.getElementById('selectAllManagers').addEventListener('click', function () {
            let checkboxes = document.querySelectorAll('.managerCheckbox');
            for (let checkbox of checkboxes) {
                checkbox.checked = this.checked;
            }
        });

        // Hàm kiểm tra hợp lệ form
        function validateForm() {
            let isValid = true;

            // Kiểm tra tiêu đề (tối thiểu 5 ký tự)
            const title = document.getElementById('title').value.trim();
            const titleError = document.getElementById('titleError');
            if (title.length < 5) {
                titleError.style.display = 'block';
                isValid = false;
            } else {
                titleError.style.display = 'none';
            }

            // Kiểm tra nội dung (tối thiểu 10 ký tự)
            const message = document.getElementById('message').value.trim();
            const messageError = document.getElementById('messageError');
            if (message.length < 10) {
                messageError.style.display = 'block';
                isValid = false;
            } else {
                messageError.style.display = 'none';
            }

            // Kiểm tra chọn ít nhất một người nhận
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
