<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.math.BigDecimal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    List<Map<String, Object>> billSummary = (List<Map<String, Object>>) request.getAttribute("billSummary");
    String selectedMonth = (String) request.getAttribute("selectedMonth");
    String success = (String) request.getAttribute("success");
%>
<style>
    body {
        font-family: "Segoe UI", sans-serif;
        background-color: #f9f9f9;
        margin: 20px;
    }

    h3 {
        font-weight: 600;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        background-color: #fff;
        box-shadow: 0 2px 6px rgba(0,0,0,0.05);
        border-radius: 6px;
        overflow: hidden;
    }

    th, td {
        padding: 12px 15px;
        text-align: center;
        font-size: 14px;
    }

    thead {
        background-color: #1976d2;
        color: white;
    }

    tbody tr:nth-child(even) {
        background-color: #f5f5f5;
    }

    tbody tr.green-bg {
        background-color: #e9f7ef !important;
    }

    .badge-success {
        background-color: #28a745;
        color: white;
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 13px;
        font-weight: 500;
    }

    .badge-warning {
        background-color: #ffc107;
        color: #212529;
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 13px;
        font-weight: 500;
    }

    .dot-button {
        background: none;
        border: none;
        font-size: 18px;
        cursor: pointer;
        color: #555;
    }

    .dropdown {
        position: relative;
        display: inline-block;
    }

    .dropdown-content {
        display: none;
        position: absolute;
        right: 0;
        background-color: #ffffff;
        min-width: 160px;
        border: 1px solid #ddd;
        border-radius: 4px;
        box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        z-index: 999;
    }

    .dropdown:hover .dropdown-content {
        display: block;
    }

    .dropdown-content a {
        color: #333;
        padding: 10px 14px;
        text-decoration: none;
        display: block;
        font-size: 14px;
    }

    .dropdown-content a:hover {
        background-color: #f0f0f0;
    }

    .btn-primary {
        background-color: #1976d2;
        border: none;
        padding: 8px 16px;
        color: white;
        border-radius: 5px;
        font-weight: 500;
        text-decoration: none;
    }

    .btn-primary:hover {
        background-color: #155fa0;
    }

    .text-center {
        text-align: center;
    }

    .text-green {
        color: #28a745;
        font-size: 0.9em;
    }

    .mb-3 {
        margin-bottom: 1rem;
    }

    .d-flex {
        display: flex;
    }

    .justify-content-between {
        justify-content: space-between;
    }

    .align-items-center {
        align-items: center;
    }

    button {
        cursor: pointer;
    }
</style>

<html>
    <head>
        <title>Danh sách hóa đơn</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="container mt-4">

        <% if (success != null) {%>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <strong><%= success%></strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>

        <h3>📑 Danh sách hóa đơn chi tiết</h3>

        <table class="table table-bordered mt-3">
            <thead class="table-primary text-center">
                <tr>
                    <th>Phòng</th>
                    <th>Tiền phòng</th>
                    <th>Điện</th>
                    <th>Nước</th>
                    <th>Wifi</th>
                    <th>Rác</th>
                    <th>Phụ thu</th>
                    <th>Tiền cọc</th>
                    <th>Tổng</th>
                    <th>Cần thu</th>
                    <th>Trạng thái</th>
                    <th>Chi tiết</th>
                </tr>
            </thead>
            <tbody>
                <% if (billSummary != null && !billSummary.isEmpty()) {
                        for (Map<String, Object> row : billSummary) {
                            String room = (String) row.get("Room");
                            String status = (String) row.get("BillStatus");
                            BigDecimal rent = (BigDecimal) row.get("RoomRent");
                            BigDecimal electricity = (BigDecimal) row.get("Electricity");
                            BigDecimal water = (BigDecimal) row.get("Water");
                            BigDecimal wifi = (BigDecimal) row.get("Wifi");
                            BigDecimal trash = (BigDecimal) row.get("Trash");
                            BigDecimal extra = (BigDecimal) row.get("ExtraFee");
                            BigDecimal deposit = (BigDecimal) row.get("Deposit");
                            BigDecimal total = (BigDecimal) row.get("Total");
                            BigDecimal due = (BigDecimal) row.get("DueAmount");
                            int billId = (int) row.get("BillID");
                %>
                <tr>
                    <td><strong><%= room%></strong></td>
                    <td><fmt:formatNumber value="<%= rent%>" type="currency" currencySymbol="đ" /></td>
                    <td><fmt:formatNumber value="<%= electricity%>" type="currency" currencySymbol="đ" /></td>
                    <td><fmt:formatNumber value="<%= water%>" type="currency" currencySymbol="đ" /></td>
                    <td><fmt:formatNumber value="<%= wifi%>" type="currency" currencySymbol="đ" /></td>
                    <td><fmt:formatNumber value="<%= trash%>" type="currency" currencySymbol="đ" /></td>
                    <td><%= (extra != null) ? new java.text.DecimalFormat("#,###").format(extra) + " đ" : "--"%></td>
                    <td><%= (deposit != null) ? new java.text.DecimalFormat("#,###").format(deposit) + " đ" : "--"%></td>
                    <td><fmt:formatNumber value="<%= total%>" type="currency" currencySymbol="đ" /></td>
                    <td><fmt:formatNumber value="<%= due%>" type="currency" currencySymbol="đ" /></td>
                    <td>
                        <% if ("PAID".equalsIgnoreCase(status)) { %>
                        <span class="badge badge-success">Đã thanh toán</span>
                        <% } else { %>
                        <span class="badge badge-warning">Chưa thanh toán</span>
                        <% }%>
                    </td>

                    <td>
                        <div class="dropdown">
                            <button class="dot-button">⋮</button>
                            <div class="dropdown-content">
                                <a href="bill?action=view&billId=<%= billId%>">Xem</a>
                                <a href="bill?action=edit&billId=<%= billId%>">Chỉnh sửa</a>
                                <% if (!"PAID".equalsIgnoreCase(status)) {%>
                                <a href="${pageContext.request.contextPath}/admin/payment.jsp?billId=<%= billId%>">Thu tiền</a>
                                <% } %>

                            </div>
                        </div>
                    </td>

                </tr>
                <% }
                } else { %>
                <tr>
                    <td colspan="12" class="text-center text-muted">Không có dữ liệu hóa đơn cho tháng này.</td>
                </tr>
                <% }%>
            </tbody>
        </table>

        <a href="bill?action=step&step=1" class="btn btn-primary mt-3">➕ Tạo hóa đơn</a>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
