<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Notification"%>
<%@page import="model.UserNotification"%>
<%@page import="model.User"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản Lý Thông Báo</title>
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
                max-width: 1200px;
                margin: 0 auto;
                padding: 15px;
            }
            .search-bar {
                margin-bottom: 20px;
            }
            .notification-table {
                margin-top: 20px;
                background-color: white;
                border-radius: 10px;
                overflow: hidden;
                margin-bottom: 30px;
            }
            .notification-table th, .notification-table td {
                vertical-align: middle;
                padding: 8px;
                text-align: left;
                font-size: 0.9rem;
            }
            .notification-table th {
                background-color: var(--light);
                color: var(--secondary);
                font-weight: 600;
            }
            .notification-table tbody tr:nth-of-type(odd) {
                background-color: rgba(0,0,0,0.05);
            }
            .alert {
                margin-bottom: 15px;
            }
            .empty-state {
                text-align: center;
                padding: 1rem;
                color: #6c757d;
            }
            .empty-state i {
                font-size: 3rem;
                margin-bottom: 0.5rem;
                color: #dee2e6;
            }
            .action-buttons {
                margin-top: 20px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <!-- Tiêu đề -->
            <div class="header">
                <h1>Quản Lý Thông Báo</h1>
            </div>

            <!-- Thông báo thành công -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
                </div>
            </c:if>

            <!-- Thông báo lỗi -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
                </div>
            </c:if>

            <!-- Thanh tìm kiếm -->
            <div class="search-bar">
                <form action="${pageContext.request.contextPath}/admin/notification" method="get" class="d-flex">
                    <input type="hidden" name="action" value="viewNotifications">
                    <input type="text" name="search" class="form-control me-2" placeholder="Tìm kiếm theo tiêu đề hoặc nội dung..."
                           value="${fn:escapeXml(param.search)}">
                    <button type="submit" class="btn btn-primary">Tìm Kiếm</button>
                </form>
            </div>

            <!-- Nút hành động -->
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/admin/notification?action=createNotification&idUser=${idUser}" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Tạo Thông Báo
                </a>
            </div>

            <!-- Bảng Thông Báo Khách Hàng -->
            <h3>Thông Báo Khách Hàng</h3>
            <div class="notification-table">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Khách Hàng</th>
                            <th>Tiêu Đề</th>
                            <th>Nội Dung</th>
                            <th>Ngày Tạo</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty notificationList}">
                                <c:forEach var="notification" items="${notificationList}">
                                    <c:set var="customer" value="${customerMap[notification.customerID]}"/>
                                    <tr>
                                        <td>${customer != null ? customer.customerFullName : 'Khách Hàng Không Xác Định'}</td>
                                        <td>${fn:escapeXml(notification.title)}</td>
                                        <td>${fn:escapeXml(notification.message)}</td>
                                        <td><fmt:formatDate value="${notification.notificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        <td>
                                            <button type="button" class="btn btn-sm btn-outline-success me-2"
                                                    onclick="showEditNotificationModal(${notification.notificationID}, '${fn:escapeXml(notification.title)}', '${fn:escapeXml(notification.message)}')">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button type="button" class="btn btn-sm btn-outline-danger"
                                                    onclick="confirmDeleteNotification(${notification.notificationID})">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="empty-state">
                                        <i class="fas fa-bell-slash"></i><br>Không tìm thấy thông báo khách hàng
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
       
            <!-- Bảng Thông Báo Quản Lý -->
            <h3>Thông Báo Quản Lý</h3>
            <div class="notification-table">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Quản Lý</th>
                            <th>Tiêu Đề</th>
                            <th>Nội Dung</th>
                            <th>Ngày Tạo</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty userNotificationList}">
                                <c:forEach var="userNotification" items="${userNotificationList}">
                                    <c:set var="manager" value="${userMap[userNotification.userID]}"/>
                                    <c:if test="${manager != null && manager.userStatus == 'Active'}">
                                        <tr>
                                            <td>${manager != null ? manager.userFullName : 'Quản Lý Không Xác Định'}</td>
                                            <td>${fn:escapeXml(userNotification.userTitle)}</td>
                                            <td>${fn:escapeXml(userNotification.userMessage)}</td>
                                            <td><fmt:formatDate value="${userNotification.userNotificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                            <td>
                                                <button type="button" class="btn btn-sm btn-outline-success me-2"
                                                        onclick="showEditUserNotificationModal(${userNotification.userNotificationID}, '${fn:escapeXml(userNotification.userTitle)}', '${fn:escapeXml(userNotification.userMessage)}')">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button type="button" class="btn btn-sm btn-outline-danger"
                                                        onclick="confirmDeleteUserNotification(${userNotification.userNotificationID})">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="empty-state">
                                        <i class="fas fa-bell-slash"></i><br>Không tìm thấy thông báo quản lý
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">
                    ← Quay Lại Bảng Điều Khiển
                </a>
            </div>
        </div>

        <!-- Modal Sửa Thông Báo Khách Hàng -->
        <div class="modal fade" id="editNotificationModal" tabindex="-1" aria-labelledby="editNotificationLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form id="editNotificationForm" method="post" action="${pageContext.request.contextPath}/admin/notification">
                    <input type="hidden" name="action" value="editNotification">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="card-title" id="editNotificationLabel">Sửa Thông Báo</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="notificationID" id="editNotificationID">
                            <div class="mb-3">
                                <label for="editNotificationTitle" class="form-label">Tiêu Đề</label>
                                <input type="text" class="form-control" id="editNotificationTitle" name="title" required>
                            </div>
                            <div class="mb-3">
                                <label for="editNotificationMessage" class="form-label">Nội Dung</label>
                                <textarea class="form-control" id="editNotificationMessage" name="message" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-success">Lưu Thay Đổi</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal Sửa Thông Báo Quản Lý -->
        <div class="modal fade" id="editUserNotificationModal" tabindex="-1" aria-labelledby="editUserNotificationLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form id="editUserNotificationForm" method="post" action="${pageContext.request.contextPath}/admin/notification">
                    <input type="hidden" name="action" value="editUserNotification">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="card-title" id="editUserNotificationLabel">Sửa Thông Báo Quản Lý</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="userNotificationID" id="editUserNotificationID">
                            <div class="mb-3">
                                <label for="editUserNotificationTitle" class="form-label">Tiêu Đề</label>
                                <input type="text" class="form-control" id="editUserNotificationTitle" name="userTitle" required>
                            </div>
                            <div class="mb-3">
                                <label for="editUserNotificationMessage" class="form-label">Nội Dung</label>
                                <textarea class="form-control" id="editUserNotificationMessage" name="userMessage" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-success">Lưu Thay Đổi</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function showEditNotificationModal(id, title, message) {
                document.getElementById('editNotificationID').value = id;
                document.getElementById('editNotificationTitle').value = title;
                document.getElementById('editNotificationMessage').value = message;
                new bootstrap.Modal(document.getElementById('editNotificationModal')).show();
            }

            function showEditUserNotificationModal(id, title, message) {
                document.getElementById('editUserNotificationID').value = id;
                document.getElementById('editUserNotificationTitle').value = title;
                document.getElementById('editUserNotificationMessage').value = message;
                new bootstrap.Modal(document.getElementById('editUserNotificationModal')).show();
            }

            function confirmDeleteNotification(id) {
                if (confirm('Bạn có chắc chắn muốn xóa thông báo này? Hành động này không thể hoàn tác.')) {
                    window.location.href = '${pageContext.request.contextPath}/admin/notification?action=deleteNotification&notificationID=' + id + '&idUser=${idUser}';
                }
            }

            function confirmDeleteUserNotification(id) {
                if (confirm('Bạn có chắc chắn muốn xóa thông báo quản lý này? Hành động này không thể hoàn tác.')) {
                    window.location.href = '${pageContext.request.contextPath}/admin/notification?action=deleteUserNotification&userNotificationID=' + id + '&idUser=${idUser}';
                }
            }

            document.addEventListener('DOMContentLoaded', function () {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    setTimeout(() => {
                        new bootstrap.Alert(alert).close();
                    }, 5000);
                });
            });
        </script>
    </body>
</html>