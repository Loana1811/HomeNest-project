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
             .status-read { background-color: rgba(40, 167, 69, 0.2); color: var(--success); }
             .status-unread { background-color: rgba(255, 193, 7, 0.2); color: var(--warning); }
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
             .editable { cursor: pointer; }
             .non-editable { opacity: 0.6; pointer-events: none; }
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

             <!-- Action Buttons -->
             <div class="action-buttons">
                 <a href="${pageContext.request.contextPath}/managerDashboard.jsp?idManager=${idUser}" class="btn btn-secondary">
                     <i class="fas fa-arrow-left"></i> Back to Dashboard
                 </a>
             </div>

             <!-- Manager Notifications Table (from Admin) -->
             <h3>Admin Notifications to Manager</h3>
             <div class="notification-table">
                 <table class="table table-striped">
                     <thead>
                         <tr>
                             <th>ID</th>
                             <th>Manager</th>
                             <th>Title</th>
                             <th>Message</th>
                             <th>Status</th>
                             <th>Created At</th>
                             <th>Actions</th>
                         </tr>
                     </thead>
                     <tbody>
                         <c:choose>
                             <c:when test="${not empty managerNotifications}">
                                 <c:forEach var="notification" items="${managerNotifications}">
                                     <c:set var="manager" value="${manager}"/>
                                     <c:set var="sender" value="${notification.sentBy != null ? userMap[notification.sentBy] : null}"/>
                                     <c:set var="isEditable" value="${notification.sentBy != null && notification.sentBy.equals(idUser)}"/>
                                     <tr>
                                         <td>${notification.userNotificationID}</td>
                                         <td>${manager != null ? fn:escapeXml(manager.userFullName) : 'Unknown Manager'}</td>
                                         <td>${fn:escapeXml(notification.userTitle)}</td>
                                         <td>${fn:escapeXml(notification.userMessage)}</td>
                                         <td>
                                             <span class="status-badge ${notification.isRead ? 'status-read' : 'status-unread'}">
                                                 ${notification.isRead ? 'Read' : 'Unread'}
                                             </span>
                                         </td>
                                         <td><fmt:formatDate value="${notification.userNotificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                         <td>
                                             <button type="button" class="${isEditable ? 'btn btn-sm btn-outline-success me-2 editable' : 'btn btn-sm btn-outline-success me-2 non-editable'}"
                                                     onclick="${isEditable ? 'showEditNotificationModal(' + notification.userNotificationID + ', \'' + fn:escapeXml(notification.userTitle) + '\', \'' + fn:escapeXml(notification.userMessage) + '\')' : ''}">
                                                 <i class="fas fa-edit"></i>
                                             </button>
                                             <button type="button" class="${isEditable ? 'btn btn-sm btn-outline-danger editable' : 'btn btn-sm btn-outline-danger non-editable'}"
                                                     onclick="${isEditable ? 'confirmDeleteNotification(' + notification.userNotificationID + ')' : ''}">
                                                 <i class="fas fa-trash"></i>
                                             </button>
                                         </td>
                                     </tr>
                                 </c:forEach>
                             </c:when>
                             <c:otherwise>
                                 <tr>
                                     <td colspan="7" class="empty-state">
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
                             <th>ID</th>
                             <th>Customer</th>
                             <th>Title</th>
                             <th>Message</th>
                             <th>Status</th>
                             <th>Created At</th>
                             <th>Sent By</th>
                         </tr>
                     </thead>
                     <tbody>
                         <c:choose>
                             <c:when test="${not empty customerNotifications}">
                                 <c:forEach var="notification" items="${customerNotifications}">
                                     <c:set var="customer" value="${customerMap[notification.customerID]}"/>
                                     <c:set var="sender" value="${userMap[notification.sentBy]}"/>
                                     <tr>
                                         <td>${notification.notificationID}</td>
                                         <td>${customer != null ? fn:escapeXml(customer.customerFullName) : 'Unknown Customer'}</td>
                                         <td>${fn:escapeXml(notification.title)}</td>
                                         <td>${fn:escapeXml(notification.message)}</td>
                                         <td>
                                             <span class="status-badge ${notification.isRead ? 'status-read' : 'status-unread'}">
                                                 ${notification.isRead ? 'Read' : 'Unread'}
                                             </span>
                                         </td>
                                         <td><fmt:formatDate value="${notification.notificationCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                         <td>${sender != null ? fn:escapeXml(sender.userFullName) : 'System'}</td>
                                     </tr>
                                 </c:forEach>
                             </c:when>
                             <c:otherwise>
                                 <tr>
                                     <td colspan="7" class="empty-state">
                                         <i class="fas fa-bell-slash"></i><br>No customer notifications sent
                                     </td>
                                 </tr>
                             </c:otherwise>
                         </c:choose>
                     </tbody>
                 </table>
             </div>
         </div>

         <!-- Edit Notification Modal -->
         <div class="modal fade" id="editNotificationModal" tabindex="-1" aria-labelledby="editNotificationLabel" aria-hidden="true">
             <div class="modal-dialog">
                 <form id="editNotificationForm" method="post" action="${pageContext.request.contextPath}/manager/notification">
                     <input type="hidden" name="action" value="editUserNotification">
                     <div class="modal-content">
                         <div class="modal-header">
                             <h5 class="card-title" id="editNotificationLabel">Edit Notification</h5>
                             <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                         </div>
                         <div class="modal-body">
                             <input type="hidden" name="userNotificationID" id="editNotificationID">
                             <div class="mb-3">
                                 <label for="editNotificationTitle" class="form-label">Title</label>
                                 <input type="text" class="form-control" id="editNotificationTitle" name="userTitle" required>
                             </div>
                             <div class="mb-3">
                                 <label for="editNotificationMessage" class="form-label">Message</label>
                                 <textarea class="form-control" id="editNotificationMessage" name="userMessage" rows="3" required></textarea>
                             </div>
                         </div>
                         <div class="modal-footer">
                             <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                             <button type="submit" class="btn btn-success">Save Changes</button>
                         </div>
                     </div>
                 </form>
             </div>
         </div>

         <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
         <script>
             // Show Edit Notification Modal
             function showEditNotificationModal(id, title, message) {
                 document.getElementById('editNotificationID').value = id;
                 document.getElementById('editNotificationTitle').value = title;
                 document.getElementById('editNotificationMessage').value = message;
                 new bootstrap.Modal(document.getElementById('editNotificationModal')).show();
             }

             // Confirm Delete Notification
             function confirmDeleteNotification(id) {
                 if (confirm('Are you sure you want to delete this notification? This action cannot be undone.')) {
                     window.location.href = '${pageContext.request.contextPath}/manager/notification?action=deleteUserNotification&userNotificationID=' + id + '&idUser=${idUser}';
                 }
             }

             // Auto-dismiss alerts after 5 seconds
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