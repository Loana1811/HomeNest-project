
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.UserNotification"%>
<%@page import="model.Notification"%>
<%@page import="model.User"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản Lý Thông Báo</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                margin: 0;
                padding: 20px;
                background-color: #f5f5f5;
            }

            .container {
                max-width: 1200px;
                margin: 0 auto;
                background-color: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }

            h1 {
                color: #333;
                border-bottom: 2px solid #eee;
                padding-bottom: 10px;
                margin-bottom: 20px;
            }

            h3 {
                color: #333;
                margin-top: 30px;
                margin-bottom: 15px;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }

            th, td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }

            th {
                background-color: #4CAF50;
                color: white;
                position: sticky;
                top: 0;
            }

            tr:hover {
                background-color: #f5f5f5;
            }

            .btn {
                padding: 6px 12px;
                background-color: #4CAF50;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                transition: background-color 0.3s;
            }

            .btn:hover {
                background-color: #45a049;
            }

            .btn-secondary {
                background-color: #6c757d;
            }

            .btn-secondary:hover {
                background-color: #5a6268;
            }

            .btn-edit {
                background-color: transparent;
                border: 1px solid #4CAF50;
                color: #4CAF50;
                padding: 6px 12px;
                border-radius: 4px;
                cursor: pointer;
                transition: background-color 0.3s, color 0.3s;
                display: flex;
                align-items: center;
                gap: 5px;
                font-size: 0.9rem;
            }

            .btn-edit:hover {
                background-color: #4CAF50;
                color: white;
            }

            .btn-delete {
                background-color: transparent;
                border: 1px solid #dc3545;
                color: #dc3545;
                padding: 6px 12px;
                border-radius: 4px;
                cursor: pointer;
                transition: background-color 0.3s, color 0.3s;
                display: flex;
                align-items: center;
                gap: 5px;
                font-size: 0.9rem;
            }

            .btn-delete:hover {
                background-color: #dc3545;
                color: white;
            }

            .message {
                padding: 10px;
                margin-bottom: 20px;
                border-radius: 4px;
            }

            .success {
                background-color: #dff0d8;
                color: #3c763d;
            }

            .error {
                background-color: #f2dede;
                color: #a94442;
            }

            .empty-state {
                text-align: center;
                padding: 40px;
                color: #777;
            }

            .action-form {
                display: flex;
                gap: 10px;
                align-items: center;
            }

            .action-buttons {
                margin-bottom: 20px;
            }

            /* Modal styles */
            .modal {
                display: none;
                position: fixed;
                z-index: 1;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                overflow: auto;
                background-color: rgba(0,0,0,0.4);
            }

            .modal-content {
                background-color: #fefefe;
                margin: 15% auto;
                padding: 20px;
                border: 1px solid #888;
                width: 80%;
                max-width: 500px;
                border-radius: 8px;
            }

            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid #eee;
                padding-bottom: 10px;
                margin-bottom: 15px;
            }

            .modal-header h5 {
                margin: 0;
            }

            .close {
                color: #aaa;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
            }

            .close:hover {
                color: black;
            }

            .modal-body .mb-3 {
                margin-bottom: 15px;
            }

            .modal-body label {
                display: block;
                margin-bottom: 5px;
            }

            .modal-body input,
            .modal-body textarea {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box;
            }

            .modal-footer {
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                border-top: 1px solid #eee;
                padding-top: 15px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Quản Lý Thông Báo</h1>

            <%-- Display success/error messages --%>
            <c:if test="${not empty sessionScope.success}">
                <div class="message success">
                    ${sessionScope.success}
                </div>
                <% session.removeAttribute("success"); %>
            </c:if>

            <c:if test="${not empty sessionScope.error}">
                <div class="message error">
                    ${sessionScope.error}
                </div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/manager/notification?action=createNotification" class="btn">
                    <i class="fas fa-plus"></i> Tạo Thông Báo Mới
                </a>
            </div>

            <h3>Thông Báo từ Admin</h3>
            <table>
                <thead>
                    <tr>
                        <th>Tiêu Đề</th>
                        <th>Nội Dung</th>
                        <th>Thời Gian Gửi</th>
                        <th>Người Gửi</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty managerNotifications}">
                            <c:forEach var="notification" items="${managerNotifications}">
                                <tr>
                                    <td>${fn:escapeXml(notification.userTitle)}</td>
                                    <td>${fn:escapeXml(notification.userMessage)}</td>
                                    <td><fmt:formatDate value="${notification.userNotificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${notification.sentBy == 1}">Admin</c:when>
                                            <c:when test="${notification.sentBy == 2}">Manager</c:when>
                                            <c:otherwise>Unknown</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="4" class="empty-state">
                                    Không tìm thấy thông báo từ admin
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <h3>Thông Báo Gửi đến Khách Hàng</h3>
            <table>
                <thead>
                    <tr>
                        <th>Khách Hàng</th>
                        <th>Tiêu Đề</th>
                        <th>Nội Dung</th>
                        <th>Thời Gian Gửi</th>
                        <th>Người Gửi</th>
                        <th>Hành Động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty customerNotifications}">
                            <c:forEach var="notification" items="${customerNotifications}">
                                <c:set var="customer" value="${customerMap[notification.customerID]}"/>
                                <tr>
                                    <td>${customer != null ? fn:escapeXml(customer.customerFullName) : 'Khách Hàng Không Xác Định'}</td>
                                    <td>${fn:escapeXml(notification.title)}</td>
                                    <td>${fn:escapeXml(notification.message)}</td>
                                    <td><fmt:formatDate value="${notification.notificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${notification.sentBy == 1}">Admin</c:when>
                                            <c:when test="${notification.sentBy == 2}">Manager</c:when>
                                            <c:otherwise>Unknown</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="action-form">
                                        <button type="button" class="btn btn-edit"
                                                onclick="showEditModal(${notification.notificationID}, '${fn:escapeXml(notification.title)}', '${fn:escapeXml(notification.message)}')">
                                            <i class="fas fa-edit"></i> Sửa
                                        </button>
                                        <button type="button" class="btn btn-delete"
                                                onclick="confirmDelete(${notification.notificationID})">
                                            <i class="fas fa-trash"></i> Xóa
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" class="empty-state">
                                    Không tìm thấy thông báo gửi đến khách hàng
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

           
        </div>

        <!-- Modal Sửa Thông Báo -->
        <div id="editModal" class="modal">
            <form method="post" action="${pageContext.request.contextPath}/manager/notification">
                <input type="hidden" name="action" value="editNotification">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5>Sửa Thông Báo</h5>
                        <span class="close" onclick="closeModal('editModal')">×</span>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="notificationID" id="editID">
                        <div class="mb-3">
                            <label for="editTitle" class="form-label">Tiêu Đề</label>
                            <input type="text" id="editTitle" name="title" required>
                        </div>
                        <div class="mb-3">
                            <label for="editMessage" class="form-label">Nội Dung</label>
                            <textarea id="editMessage" name="message" rows="3" required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" onclick="closeModal('editModal')">Hủy</button>
                        <button type="submit" class="btn">Lưu Thay Đổi</button>
                    </div>
                </div>
            </form>
        </div>

        <script>
            function showEditModal(id, title, message) {
                document.getElementById('editID').value = id;
                document.getElementById('editTitle').value = title;
                document.getElementById('editMessage').value = message;
                document.getElementById('editModal').style.display = 'block';
            }

            function closeModal(modalId) {
                document.getElementById(modalId).style.display = 'none';
            }

            function confirmDelete(id) {
                if (confirm('Bạn có chắc chắn muốn xóa thông báo này? Hành động này không thể hoàn tác.')) {
                    window.location.href = '${pageContext.request.contextPath}/manager/notification?action=deleteNotification&notificationID=' + id + '&idUser=${idUser}';
                }
            }

            // Close modal when clicking outside
            window.onclick = function(event) {
                if (event.target.classList.contains('modal')) {
                    event.target.style.display = 'none';
                }
            };

            // Auto-dismiss messages after 5 seconds
            document.addEventListener('DOMContentLoaded', function () {
                const messages = document.querySelectorAll('.message');
                messages.forEach(message => {
                    setTimeout(() => {
                        message.style.display = 'none';
                    }, 5000);
                });
            });
        </script>
    </body>
</html>
