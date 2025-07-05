<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    java.time.LocalDate currentDate = java.time.LocalDate.now();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thu tiền hóa đơn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 40px;
        }
        .container {
            max-width: 650px;
        }
    </style>
</head>
<body>
<div class="container bg-white rounded shadow p-4">
    <h3 class="mb-4 text-primary">💵 Ghi nhận thu tiền</h3>

    <!-- Thông tin tổng tiền -->
    <c:if test="${not empty bill}">
        <p><strong>Tổng tiền hóa đơn:</strong>
            <fmt:formatNumber value="${bill.totalAmount}" type="number" groupingUsed="true"/> đ
        </p>
        <p><strong>Khách đã thanh toán:</strong>
            <fmt:formatNumber value="${totalPaid}" type="number" groupingUsed="true"/> đ
        </p>
        <p><strong>Còn nợ:</strong>
            <fmt:formatNumber value="${bill.totalAmount - totalPaid}" type="number" groupingUsed="true"/> đ
        </p>
    </c:if>

    <!-- Hiển thị thông báo -->
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <!-- Form thu tiền -->
    <form action="${pageContext.request.contextPath}/admin/payment" method="post">
        <input type="hidden" name="action" value="collect"/>
        <input type="hidden" name="billId" value="${bill.billID}" />

        <div class="mb-3">
            <label class="form-label">Số tiền khách thanh toán (đ)</label>
            <input type="text" class="form-control" name="amountPaid" required placeholder="Ví dụ: 5000000" />
        </div>

        <div class="mb-3">
            <label class="form-label">Phương thức thanh toán</label>
            <select class="form-select" name="paymentMethod">
                <option value="CASH">Trả tiền mặt</option>
                <option value="BANK">Chuyển khoản</option>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Lý do thu tiền / Ghi chú</label>
            <input type="text" class="form-control" name="paymentNote" placeholder="Ví dụ: Thu tiền tháng 7" />
        </div>

        <div class="mb-3">
            <label class="form-label">Ngày nhận tiền</label>
            <input type="date" class="form-control" name="paymentDate"
                   value="<%= currentDate.toString() %>" required />
        </div>

        <button type="submit" class="btn btn-success w-100">Xác nhận thu tiền</button>
    </form>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/admin/bill?action=view&billId=${bill.billID}" class="btn btn-outline-secondary">
            ← Quay lại hóa đơn
        </a>
    </div>
</div>
</body>
</html>
