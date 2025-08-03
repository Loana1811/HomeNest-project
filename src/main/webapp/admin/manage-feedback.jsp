<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Quản lý phản hồi hóa đơn</title>
    <style>
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 10px; border: 1px solid #ccc; }
        .btn { padding: 6px 12px; margin: 2px; }
    </style>
</head>
<body>
    <h2>Danh sách phản hồi hóa đơn (Chờ duyệt)</h2>
    <table>
        <thead>
            <tr>
                <th>Mã phản hồi</th>
                <th>Bill ID</th>
                <th>User ID</th>
                <th>Lý do</th>
                <th>Ngày gửi</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="f" items="${feedbackList}">
            <tr>
                <td>${f.feedbackId}</td>
                <td>${f.billId}</td>
                <td>${f.userId}</td>
                <td>${f.reason}</td>
                <td>${f.createdAt}</td>
                <td>${f.status}</td>
                <td>
                    <form action="AcceptFeedbackServlet" method="post" style="display:inline;">
                        <input type="hidden" name="feedbackId" value="${f.feedbackId}">
                        <button class="btn" type="submit">Duyệt</button>
                    </form>
                    <form action="RejectFeedbackServlet" method="post" style="display:inline;">
                        <input type="hidden" name="feedbackId" value="${f.feedbackId}">
                        <button class="btn" type="submit">Từ chối</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</body>
</html>
