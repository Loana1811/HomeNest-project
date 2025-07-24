<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.UserNotification"%>
<%@page import="model.Notification"%>
<%@page import="model.User"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manager Notifications</title>
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
                max-width: 1000px;
                margin: 0 auto;
                padding: 15px;
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
            .status-badge {
                padding: 0.2em 0.5em;
                border-radius: 10px;
                font-size: 0.7em;
                font-weight: 500;
            }
            .status-read {
                background-color: rgba(40, 167, 69, 0.2);
                color: var(--success);
            }
            .status-unread {
                background-color: rgba(255, 193, 7, 0.2);
                color: var(--warning);
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
            .editable {
                cursor: pointer;
            }
            .non-editable {
                opacity: 0.6;
                pointer-events: none;
            }
        </style>
    </head>
    <body>


        <div class="container">
            <!-- Header -->
            <div class="header">
                <h1>Manager Notifications</h1>
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



            <div class="action-buttons">
             
                <a href="${pageContext.request.contextPath}/manager/notification?action=createNotification" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Create New Notification
                </a>
            </div>

            <!-- Manager Notifications Table (from Admin) -->
            <h3>Admin Notifications to Manager</h3>
            <div class="notification-table">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Message</th>
                            <th>Sending time</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty managerNotifications}">
                                <c:forEach var="notification" items="${managerNotifications}">
                                    <c:set var="manager" value="${manager}"/>
                                    <c:set var="sender" value="${notification.sentBy != null ? userMap[notification.sentBy] : null}"/>
                                    <tr>
                                        <td>${fn:escapeXml(notification.userTitle)}</td>
                                        <td>${fn:escapeXml(notification.userMessage)}</td>
                                        <td><fmt:formatDate value="${notification.userNotificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="3" class="empty-state">
                                        <i class="fas fa-bell-slash"></i><br>No admin notifications found
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <!-- Customer Notifications Table (sent by Manager) -->
            <h3>Notifications Sent to Customers</h3>
            <div class="notification-table">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Customer</th>
                            <th>Title</th>
                            <th>Message</th>
                            <th>Sending Time</th>
                            <th>Sent By</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty customerNotifications}">
                                <c:forEach var="notification" items="${customerNotifications}">
                                    <c:set var="customer" value="${customerMap[notification.customerID]}"/>
                                    <c:set var="sender" value="${userMap[notification.sentBy]}"/>
                                    <tr>
                                        <td>${customer != null ? fn:escapeXml(customer.customerFullName) : 'Unknown Customer'}</td>
                                        <td>${fn:escapeXml(notification.title)}</td>
                                        <td>${fn:escapeXml(notification.message)}</td>
                                        <td><fmt:formatDate value="${notification.notificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        <td>${sender != null ? fn:escapeXml(sender.userFullName) : 'System'}</td>
                                        <td>
                                            <button type="button" class="btn btn-sm btn-outline-success me-2" onclick="showEditModal(${notification.notificationID}, '${fn:escapeXml(notification.title)}', '${fn:escapeXml(notification.message)}')">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button type="button" class="btn btn-sm btn-outline-danger" onclick="confirmDelete(${notification.notificationID})">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="empty-state">
                                        <i class="fas fa-bell-slash"></i><br>No customer notifications sent
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Edit Modal -->
        <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <form method="post" action="${pageContext.request.contextPath}/manager/notification">
                    <input type="hidden" name="action" value="editNotification">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5>Edit Notification</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="notificationID" id="editID">
                            <div class="mb-3">
                                <label for="editTitle" class="form-label">Title</label>
                                <input type="text" class="form-control" id="editTitle" name="title" required>
                            </div>
                            <div class="mb-3">
                                <label for="editMessage" class="form-label">Message</label>
                                <textarea class="form-control" id="editMessage" name="message" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-success">Save</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Auto-dismiss alerts after 5 seconds
            document.addEventListener('DOMContentLoaded', function () {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    setTimeout(() => {
                        new bootstrap.Alert(alert).close();
                    }, 5000);
                });
            });

            function showEditModal(id, title, message) {
                document.getElementById('editID').value = id;
                document.getElementById('editTitle').value = title;
                document.getElementById('editMessage').value = message;
                new bootstrap.Modal(document.getElementById('editModal')).show();
            }

            function confirmDelete(id) {
                if (confirm('Are you sure you want to delete this notification?')) {
                    window.location.href = '${pageContext.request.contextPath}/manager/notification?action=deleteNotification&notificationID=' + id + '&idUser=${idUser}';
                }
            }
        </script>
    </body>
</html>