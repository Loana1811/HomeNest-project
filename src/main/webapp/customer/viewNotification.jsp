<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Notification List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .notification-unread {
            background-color: #e6f3ff;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <c:choose>
        <c:when test="${empty notifications}">
            <p class="text-center">Không có thông báo nào.</p>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered table-hover">
                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Tiêu đề</th>
                        <th>Nội dung</th>
                        <th>Ngày gửi</th>
                        <th>Trạng thái</th>
                        <th>Gửi bởi</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="notification" items="${notifications}">
                        <tr class="${notification.isRead ? '' : 'notification-unread'}">
                            <td>${notification.notificationID}</td>
                            <td><c:out value="${notification.title}"/></td>
                            <td><c:out value="${notification.message}"/></td>
                            <td><fmt:formatDate value="${notification.notificationCreatedAt}" pattern="dd-MM-yyyy HH:mm"/></td>
                            <td>${notification.isRead ? 'Đã đọc' : 'Chưa đọc'}</td>
                            <td>${notification.sentBy != null ? notification.sentBy : 'Hệ thống'}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>