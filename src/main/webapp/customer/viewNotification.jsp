<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Notification List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #1e3a8a;
            --unread-bg: #e6f3ff;
            --text-dark: #1f2937;
            --text-light: #f9fafb;
        }

        body {
            background-color: #f3f6fc;
            font-family: 'Segoe UI', Tahoma, sans-serif;
            color: var(--text-dark);
            padding: 30px;
        }

        h2 {
            color: var(--primary-color);
            font-weight: 700;
            text-align: center;
            margin-bottom: 30px;
        }

        .notification-unread {
            background-color: var(--unread-bg);
            font-weight: bold;
        }

        .table th {
            background-color: var(--primary-color) !important;
            color: var(--text-light) !important;
            text-align: center;
        }

        .table td {
            vertical-align: middle;
        }

        .text-center {
            text-align: center;
            margin-top: 50px;
            font-weight: 500;
            color: #6b7280;
        }

        @media (max-width: 768px) {
            body {
                padding: 15px;
            }

            .table {
                font-size: 0.95rem;
            }
        }
    </style>
</head>
<body>

    <h2>📬 Notification List</h2>

    <c:choose>
        <c:when test="${empty notifications}">
            <p class="text-center">You have no notifications.</p>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Message</th>
                            <th>Sent At</th>
                            <th>Status</th>
                            <th>Sent By</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="notification" items="${notifications}">
                            <tr class="${notification.isRead ? '' : 'notification-unread'}">
                                <td>${notification.notificationID}</td>
                                <td><c:out value="${notification.title}"/></td>
                                <td><c:out value="${notification.message}"/></td>
                                <td><fmt:formatDate value="${notification.notificationCreatedAt}" pattern="dd-MM-yyyy HH:mm"/></td>
                                <td>
                                    <span class="badge ${notification.isRead ? 'bg-success' : 'bg-warning'}">
                                        ${notification.isRead ? 'Read' : 'Unread'}
                                    </span>
                                </td>
                                <td>
                                    ${notification.sentBy != null ? notification.sentBy : 'System'}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
