
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
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
   
  <style>
        :root {
            --primary-color: #1e3a8a;
            --background-color: #f0f4f8;
            --hover-color: #3b5cb8;
            --action-color: #2563eb;
        }

        body {
            background-color: var(--background-color);
            font-family: 'Segoe UI', sans-serif;
            margin: 0;
            padding: 0;
        }

        .main-content {
            margin-left: 250px;
            padding: 40px 30px;
            animation: fadeIn 0.6s ease-in-out;
        }

        h1 {
            color: var(--primary-color);
            font-weight: bold;
            margin-bottom: 20px;
        }

     table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  background: white;
  animation: fadeIn 0.6s ease;
}

th {
  background-color: #1e3a8a;
  color: #fff;
  padding: 16px;
  text-align: center;
}

td {
  padding: 14px;
  text-align: center;
  vertical-align: middle;
  transition: background 0.3s ease;
}

td:hover {
  background-color: #f0f4ff;
}

.action-btn {
  transition: transform 0.2s ease, box-shadow 0.3s ease;
  border-radius: 10px;
}

.action-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 0 8px rgba(0,0,0,0.15);
}


        .search-box {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .search-box input[type="text"] {
            width: 300px;
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 8px;
            outline: none;
        }

        .btn-create {
            background-color: #10b981;
            color: white;
            padding: 10px 16px;
            border-radius: 10px;
            text-decoration: none;
            transition: 0.3s ease;
        }

        .btn-create:hover {
            background-color: #059669;
            transform: translateY(-2px);
        }

        .action-form {
            display: flex;
            justify-content: center;
            gap: 6px;
        }

        .btn-action {
            padding: 6px 10px;
            border-radius: 20px;
            border: none;
            font-size: 13px;
            font-weight: 500;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            transition: all 0.3s ease-in-out;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
        }

        .btn-edit {
            background-color: #3b82f6;
            color: white;
        }

        .btn-delete {
            background-color: #ef4444;
            color: white;
        }

        .btn-edit:hover {
            background-color: #2563eb;
            transform: scale(1.05);
        }

        .btn-delete:hover {
            background-color: #dc2626;
            transform: scale(1.05);
        }

        .empty-state {
            color: #6b7280;
            font-style: italic;
            text-align: center;
            padding: 20px;
        }

        .message {
            padding: 10px 20px;
            border-radius: 6px;
            margin-bottom: 15px;
            animation: fadeIn 0.5s ease-in-out;
        }

        .message.success {
            background-color: #d1fae5;
            color: #065f46;
        }

        .message.error {
            background-color: #fee2e2;
            color: #991b1b;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 9999;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgba(0,0,0,0.5);
        }

        .modal-content {
            background-color: #fff;
            margin: 10% auto;
            padding: 20px;
            border-radius: 12px;
            width: 400px;
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
            animation: zoomIn 0.4s ease;
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #ccc;
            margin-bottom: 15px;
        }

        .modal-header h5 {
            margin: 0;
            color: var(--primary-color);
        }

        .modal-body label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
        }

        .modal-body input,
        .modal-body textarea {
            width: 100%;
            padding: 8px;
            margin-bottom: 12px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .modal-footer {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        .modal .btn {
            padding: 8px 14px;
            border-radius: 8px;
        }

        .modal .btn-secondary {
            background-color: #e5e7eb;
            color: #374151;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @keyframes zoomIn {
            from { transform: scale(0.9); opacity: 0; }
            to { transform: scale(1); opacity: 1; }
        }
    </style>
    </head>
    <body>
        <div class="main-content">
           <h1>Notification Management</h1>

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


    <div class="search-box">
        <form action="notification" method="get">
            <input type="hidden" name="action" value="viewNotifications">
            <input type="text" name="search" placeholder="Search by title or message" value="${param.search}"/>
            <button type="submit" class="btn btn-primary">Search</button>
        </form>
        <a href="notification?action=createNotification" class="btn-create">+ Create Notification</a>
    </div>

<h3>Customer Notifications</h3>
<table>
    <thead>
        <tr>
            <th>Customer</th>
            <th>Title</th>
            <th>Message</th>
            <th>Created At</th>
            <th>Sender</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${not empty notificationList}">
                <c:forEach var="notification" items="${notificationList}">
                    <c:set var="customer" value="${customerMap[notification.customerID]}"/>
                    <tr>
                        <td>${customer != null ? customer.customerFullName : 'Unknown Customer'}</td>
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
                                    onclick="showEditNotificationModal(${notification.notificationID}, '${fn:escapeXml(notification.title)}', '${fn:escapeXml(notification.message)}')">
                                <i class="fas fa-edit"></i> 
                            </button>
                            <button type="button" class="btn btn-delete"
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
                        No customer notifications found
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<h3>Manager Notifications</h3>
<table>
    <thead>
        <tr>
            <th>Manager</th>
            <th>Title</th>
            <th>Message</th>
            <th>Created At</th>
            <th>Sender</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${not empty userNotificationList}">
                <c:forEach var="userNotification" items="${userNotificationList}">
                    <c:set var="manager" value="${userMap[userNotification.userID]}"/>
                    <c:if test="${manager != null && manager.userStatus == 'Active'}">
                        <tr>
                            <td>${manager != null ? manager.userFullName : 'Unknown Manager'}</td>
                            <td>${fn:escapeXml(userNotification.userTitle)}</td>
                            <td>${fn:escapeXml(userNotification.userMessage)}</td>
                            <td><fmt:formatDate value="${userNotification.userNotificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                            <td>
                                <c:choose>
                                    <c:when test="${userNotification.sentBy == 1}">Admin</c:when>
                                    <c:when test="${userNotification.sentBy == 2}">Manager</c:when>
                                    <c:otherwise>Unknown</c:otherwise>
                                </c:choose>
                            </td>
                            <td class="action-form">
                                <button type="button" class="btn btn-edit"
                                        onclick="showEditUserNotificationModal(${userNotification.userNotificationID}, '${fn:escapeXml(userNotification.userTitle)}', '${fn:escapeXml(userNotification.userMessage)}')">
                                    <i class="fas fa-edit"></i> 
                                </button>
                                <button type="button" class="btn btn-delete"
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
                        No manager notifications found
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>
</div>

<!-- Edit Customer Notification Modal -->
<div id="editNotificationModal" class="modal">
    <form id="editNotificationForm" method="post" action="${pageContext.request.contextPath}/admin/notification">
        <input type="hidden" name="action" value="editNotification">
        <div class="modal-content">
            <div class="modal-header">
                <h5 id="editNotificationLabel">Edit Notification</h5>
                <span class="close" onclick="closeModal('editNotificationModal')">×</span>
            </div>
            <div class="modal-body">
                <input type="hidden" name="notificationID" id="editNotificationID">
                <div class="mb-3">
                    <label for="editNotificationTitle" class="form-label">Title</label>
                    <input type="text" id="editNotificationTitle" name="title" required>
                </div>
                <div class="mb-3">
                    <label for="editNotificationMessage" class="form-label">Message</label>
                    <textarea id="editNotificationMessage" name="message" rows="3" required></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal('editNotificationModal')">Cancel</button>
                <button type="submit" class="btn">Save Changes</button>
            </div>
        </div>
    </form>
</div>

<!-- Edit Manager Notification Modal -->
<div id="editUserNotificationModal" class="modal">
    <form id="editUserNotificationForm" method="post" action="${pageContext.request.contextPath}/admin/notification">
        <input type="hidden" name="action" value="editUserNotification">
        <div class="modal-content">
            <div class="modal-header">
                <h5 id="editUserNotificationLabel">Edit Manager Notification</h5>
                <span class="close" onclick="closeModal('editUserNotificationModal')">×</span>
            </div>
            <div class="modal-body">
                <input type="hidden" name="userNotificationID" id="editUserNotificationID">
                <div class="mb-3">
                    <label for="editUserNotificationTitle" class="form-label">Title</label>
                    <input type="text" id="editUserNotificationTitle" name="userTitle" required>
                </div>
                <div class="mb-3">
                    <label for="editUserNotificationMessage" class="form-label">Message</label>
                    <textarea id="editUserNotificationMessage" name="userMessage" rows="3" required></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal('editUserNotificationModal')">Cancel</button>
                <button type="submit" class="btn">Save Changes</button>
            </div>
        </div>
    </form>
</div>


        <script>
            function showEditNotificationModal(id, title, message) {
                document.getElementById('editNotificationID').value = id;
                document.getElementById('editNotificationTitle').value = title;
                document.getElementById('editNotificationMessage').value = message;
                document.getElementById('editNotificationModal').style.display = 'block';
            }

            function showEditUserNotificationModal(id, title, message) {
                document.getElementById('editUserNotificationID').value = id;
                document.getElementById('editUserNotificationTitle').value = title;
                document.getElementById('editUserNotificationMessage').value = message;
                document.getElementById('editUserNotificationModal').style.display = 'block';
            }

            function closeModal(modalId) {
                document.getElementById(modalId).style.display = 'none';
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
