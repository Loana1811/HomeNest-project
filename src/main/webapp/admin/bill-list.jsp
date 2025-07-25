<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.math.BigDecimal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="utils.FormatUtil" %>

<%
    List<Map<String, Object>> billSummary = (List<Map<String, Object>>) request.getAttribute("billSummary");
    String selectedMonth = (String) request.getAttribute("selectedMonth");
    String success = (String) session.getAttribute("success");
    String ctx = request.getContextPath();
%>

<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Danh sách hóa đơn</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>
            .main-content {
                margin-left: 240px;
                padding: 80px 30px 30px;
            }

            .table-responsive {
                overflow-x: auto;
                width: 100%;
            }

            .table {
                min-width: 1400px;
                width: 100%;
                table-layout: fixed;
            }

            .table th,
            .table td {
                white-space: nowrap;
                vertical-align: middle;
                text-align: center;
            }

            .table th:first-child,
            .table td:first-child {
                text-align: left;
                min-width: 150px;
                font-weight: bold;
            }

            .table th {
                background-color: #def8f6;
                color: #00404a;
                font-weight: 600;
                padding: 12px;
            }

            .table td {
                padding: 10px 14px;
            }

            .table tbody tr:nth-child(even) {
                background-color: #f8f9fa;
            }

            .badge.bg-primary {
                background-color: #1890ff;
                font-size: 13px;
                padding: 5px 10px;
                border-radius: 12px;
            }

            .badge.bg-danger {
                background-color: #dc3545;
                font-size: 13px;
                padding: 5px 10px;
                border-radius: 12px;
            }

            .dot-button {
                background: transparent;
                border: none;
                font-size: 20px;
                cursor: pointer;
                color: #333;
                padding: 0 8px;
                height: 32px;
                width: 32px;
                border-radius: 6px;
                transition: background-color 0.2s;
            }

            .dot-button:hover {
                background-color: #f0f0f0;
            }

            .dropdown-content {
                display: none;
                position: absolute;
                right: 0;
                top: 100%;
                background-color: white;
                min-width: 160px;
                border: 1px solid #ddd;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
                border-radius: 8px;
                z-index: 1000;
            }

            .dropdown-content.show {
                display: block;
            }

            .dropdown-content a,
            .dropdown-content button {
                width: 100%;
                padding: 10px 14px;
                text-align: left;
                background: none;
                border: none;
                font-size: 14px;
                color: #333;
                cursor: pointer;
            }

            .dropdown-content a:hover,
            .dropdown-content button:hover {
                background-color: #f5f5f5;
            }
        </style>
    </head>


    <body>
        <main class="main-content">
            <% if (success != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <strong><%= success %></strong>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% } %>

            <h3 class="mb-4">📁 Bill Management</h3>

            <div class="table-responsive">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Room</th>
                            <th>RoomRent</th>
                            <th>Electricity</th>
                            <th>Water</th>
                            <th>Trash</th>
                            <th>Wifi</th>
                            <th>Deposit</th>
                            <th>Total</th>
                            <th>Collected</th>
                            <th>In debt</th>
                            <th>Status</th>
                            <th>Action</th>
                            <th>Details</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                        if (billSummary != null && !billSummary.isEmpty()) {
                            for (Map<String, Object> row : billSummary) {
                                String room = (String) row.get("Room");
                                String status = (String) row.get("BillStatus");
                                BigDecimal rent = (BigDecimal) row.get("RoomRent");
                                BigDecimal electricity = (BigDecimal) row.get("Electricity");
                                BigDecimal water = (BigDecimal) row.get("Water");
                                BigDecimal trash = (BigDecimal) row.get("Trash");
                                BigDecimal wifi = (BigDecimal) row.get("Wifi");

                                boolean isElectricityFree = row.get("IsElectricityFree") != null && (Boolean) row.get("IsElectricityFree");
                                boolean isWaterFree = row.get("IsWaterFree") != null && (Boolean) row.get("IsWaterFree");
                                boolean isTrashFree = row.get("IsTrashFree") != null && (Boolean) row.get("IsTrashFree");
                                boolean isWifiFree = row.get("IsWifiFree") != null && (Boolean) row.get("IsWifiFree");

                              
                                BigDecimal deposit = (BigDecimal) row.get("Deposit");
                                BigDecimal totalAmount = (BigDecimal) row.get("TotalAmount");
                                BigDecimal totalPaid = (BigDecimal) row.get("TotalPaid");
                                BigDecimal due = (BigDecimal) row.get("DueAmount");
                                int billId = (int) row.get("BillID");
                        %>
                        <tr>
                            <td><%= room %></td>
                            <td><%= FormatUtil.formatMoney(rent) %></td>
                            <td><%= isElectricityFree ? FormatUtil.formatMoney(electricity) : "0đ" %></td>
                            <td><%= isWaterFree ? FormatUtil.formatMoney(water) : "0đ" %></td>
                            <td><%= isTrashFree ? FormatUtil.formatMoney(trash) : "0đ" %></td>
                            <td><%= isWifiFree ? FormatUtil.formatMoney(wifi) : "0đ" %></td>
                            <td><%= (deposit != null) ? FormatUtil.formatMoney(deposit) : "--" %></td>
                            <td><fmt:formatNumber value="<%= totalAmount %>" type="currency" currencySymbol="đ"/></td>
                            <td><fmt:formatNumber value="<%= totalPaid %>" type="currency" currencySymbol="đ"/></td>
                            <td><fmt:formatNumber value="<%= due %>" type="currency" currencySymbol="đ"/></td>
                            <td>
                                <% if ("PAID".equalsIgnoreCase(status)) { %>
                                <span class="text-success">✔</span>
                                <% } else { %>
                                <span class="badge bg-danger">Chưa thanh toán</span>
                                <% } %>
                            </td>
                            <td>
                                <% if (!"PAID".equalsIgnoreCase(status)) { %>
                                <a href="<%= ctx + "/admin/payment?billId=" + billId %>" class="btn btn-success btn-sm">Thu tiền</a>
                                <% } else { %>
                                <span class="text-muted">✔</span>
                                <% } %>
                            </td>
                            <td>
                                <div class="dropdown">
                                    <button class="dot-button">⋮</button>
                                    <div class="dropdown-content">
                                        <a href="bill?action=view&billId=<%= billId %>">📄 Xem</a>
                                        <% if (!"PAID".equalsIgnoreCase(status)) { %>
                                        <a href="bill?action=edit&billId=<%= billId %>">✏️ Chỉnh sửa</a>
                                        <% } %>
                                        <a href="<%= ctx + "/admin/bill?action=print&billId=" + billId %>" target="_blank">🖨 In hóa đơn</a>
                                        <% if (!"PAID".equalsIgnoreCase(status) && totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) { %>
                                        <form method="post" action="<%= ctx %>/admin/bill" style="margin: 0;">
                                            <input type="hidden" name="action" value="send"/>
                                            <input type="hidden" name="billId" value="<%= billId %>"/>
                                            <button type="submit" class="dropdown-item">📤 Gửi hóa đơn</button>
                                        </form>
                                        <% } else if (!"PAID".equalsIgnoreCase(status)) { %>
                                        <span class="dropdown-item text-muted" style="cursor: not-allowed;">📤 Không thể gửi</span>
                                        <% } %>
                                        <% if (!"PAID".equalsIgnoreCase(status)) { %>
                                        <a class="dropdown-item text-danger" href="<%= ctx + "/admin/bill?action=cancel&billId=" + billId %>" onclick="return confirm('Bạn có chắc chắn muốn hủy hóa đơn này không?')">🗑 Hủy hóa đơn</a>
                                        <% } %>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <% }} else { %>
                        <tr><td colspan="14" class="text-center text-muted">Không có dữ liệu hóa đơn cho tháng này.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <a href="bill?action=step&step=1" class="btn btn-primary mt-3">➕ Tạo hóa đơn</a>
        </main>
    </body>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
                                            document.querySelectorAll('.dot-button').forEach(button => {
                                                button.addEventListener('click', function (event) {
                                                    event.stopPropagation();
                                                    closeAllDropdowns();
                                                    const dropdown = this.nextElementSibling;
                                                    dropdown.classList.toggle('show');
                                                });
                                            });

                                            window.addEventListener('click', closeAllDropdowns);

                                            function closeAllDropdowns() {
                                                document.querySelectorAll('.dropdown-content').forEach(menu => {
                                                    menu.classList.remove('show');
                                                });
                                            }

                                            window.addEventListener('DOMContentLoaded', () => {
                                                const params = new URLSearchParams(window.location.search);
                                                const sendStatus = params.get("sendStatus");

                                                if (sendStatus === "success")
                                                    alert("📤 Gửi hóa đơn thành công!");
                                                else if (sendStatus === "fail")
                                                    alert("❌ Gửi hóa đơn thất bại.");
                                                else if (sendStatus === "exists")
                                                    alert("⚠️ Hóa đơn đã được gửi trước đó!");
                                                else if (sendStatus === "incomplete")
                                                    alert("❌ Hóa đơn chưa đầy đủ tiện ích.");
                                            });
    </script>
</body>
</html>
