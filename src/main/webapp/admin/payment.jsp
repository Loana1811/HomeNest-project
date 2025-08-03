<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    java.time.LocalDate currentDate = java.time.LocalDate.now();
      String ctx = request.getContextPath();  
    %>  
    <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>  
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Collect bills</title>
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
            <h3 class="mb-4 text-primary">💵 Record cash receipts</h3>

            <!-- Thông tin tổng tiền -->
            <c:if test="${not empty bill}">
                <p><strong>Total bill:</strong>
                    <fmt:formatNumber value="${bill.totalAmount}" type="number" groupingUsed="true"/> đ
                </p>
                <p><strong>Customer paid:</strong>
                    <fmt:formatNumber value="${totalPaid}" type="number" groupingUsed="true"/> đ
                </p>
                <p><strong>Remaining:</strong>
                    <fmt:formatNumber value="${amountRemaining}" type="number" groupingUsed="true"/> đ
                </p>
            </c:if>

            <!-- Thông báo -->
            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <!-- Form thu tiền -->
            <c:if test="${not empty bill}">
                <form action="${pageContext.request.contextPath}/admin/payment" method="post">
                    <input type="hidden" name="action" value="collect"/>
                    <input type="hidden" name="billId" value="${bill.billID}" />

                    <div class="mb-3">
                        <label class="form-label">Amount paid by customer (VND)</label>
                        <input type="text" class="form-control" name="amountPaid" required
                               placeholder="Nhập đúng số: ${amountRemaining}" />
                        <div class="form-text">⚠️ You must only enter the exact amount owed.</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Payment method</label>
                        <select class="form-select" name="paymentMethod">
                            <option value="CASH">Cash</option>
                            <option value="BANK">Bank</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Reason</label>
                        <input type="text" class="form-control" name="paymentNote" placeholder="VD: Thu tiền tháng 7" />
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Date of receipt</label>
                        <input type="date" class="form-control" name="paymentDate"
                               value="<%= currentDate.toString()%>" required />
                    </div>

                    <button type="submit" class="btn btn-success w-100">Confirmation of payment</button>
                </form>
            </c:if>

            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/admin/bill?action=view&billId=${bill.billID}"
                   class="btn btn-outline-secondary">
                    ← Back to the bill
                </a>
            </div>
        </div>
    </body>
</html>
