<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.math.BigDecimal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
%>

<%
    List<Map<String, Object>> billSummary = (List<Map<String, Object>>) request.getAttribute("billSummary");
    String selectedMonth = (String) request.getAttribute("selectedMonth");
    String success = (String) session.getAttribute("success");
    if (success != null) {
%>
<div class="alert alert-success alert-dismissible fade show" role="alert">
    <strong><%= success%></strong>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<%
        session.removeAttribute("success");
    }
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
    .dropdown-content a, .dropdown-content button {
        width: 100%;
        text-align: left;
        background: none;
        border: none;
        padding: 10px 14px;
        font-size: 14px;
        color: #333;
        display: block;
    }

    .dropdown-content a:hover,
    .dropdown-content button:hover {
        background-color: #f0f0f0;
    }


    .dropdown-content.show {
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
        <div class="sidebar">
            <h5 class="text-primary">Admin Menu</h5>
            <ul class="nav flex-column">

                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/viewListAccount">Accounts</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/rooms?action=list">Rooms</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/tenant?action=list">Tenants</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/bill?action=list">Bills</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/utility?action=list">Utilities</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/record-reading">Record Usage</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/statistical">Statistical</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/usage">View Usage List</a>
                </li>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/adminReport">Report</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/notification?action=viewNotifications">Notification</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/Contracts">Contract</a>
                </li>


            </ul>
        </div>


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
                    <th>Đã thu</th>

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
                            BigDecimal totalAmount = (BigDecimal) row.get("TotalAmount"); // cần thêm ở DAO nếu chưa có
                            BigDecimal totalPaid = (BigDecimal) row.get("TotalPaid");
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
                    <td><fmt:formatNumber value="<%= totalAmount%>" type="currency" currencySymbol="đ" /></td> <!-- Tổng -->
                    <td><fmt:formatNumber value="<%= totalPaid%>" type="currency" currencySymbol="đ" /></td> <!-- Đã thu -->
                    <td><fmt:formatNumber value="<%= due%>" type="currency" currencySymbol="đ" /></td> <!-- Cần thu -->


                    <td>
                        <%
                            if ("PAID".equalsIgnoreCase(status)) {
                        %>
                        <span class="badge bg-success">Đã thanh toán</span>
                        <%
                        } else if ("PARTIAL".equalsIgnoreCase(status)) {
                        %>
                        <span class="badge bg-warning text-dark">Chưa thanh toán đủ</span>
                        <a href="${pageContext.request.contextPath}/admin/payment?billId=${bill.billID}" class="btn btn-sm btn-outline-primary ms-2">Tiếp tục thu</a>
                        <%
                        } else {
                        %>
                        <span class="badge bg-danger">Chưa thanh toán</span>
                        <a href="${pageContext.request.contextPath}/admin/payment?billId=${bill.billID}" class="btn btn-sm btn-outline-primary ms-2">Thu tiền</a>
                        <%
                            }
                        %>
                    </td>

                    <td>
                        <div class="d-flex justify-content-center gap-2">
                            <% if (!"PAID".equalsIgnoreCase(status)) {%>
                            <a href="<%= request.getContextPath() + "/admin/payment?billId=" + billId%>" class="btn btn-success btn-sm">
                                Thu tiền
                            </a>
                            <% }%>

                            <div class="dropdown">
                                <button class="dot-button">⋮</button>
                                <div class="dropdown-content">
                                    <a class="dropdown-item" href="bill?action=view&billId=<%= billId%>">📄 Xem</a>
                                    <a class="dropdown-item" href="bill?action=edit&billId=<%= billId%>">✏️ Chỉnh sửa</a>
                                    <a class="dropdown-item" href="<%= request.getContextPath() + "/admin/bill?action=print&billId=" + billId%>" target="_blank">🖨 In hóa đơn</a>

                                    <% if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {%>
                                    <form method="post" action="<%= request.getContextPath()%>/admin/bill" style="margin: 0;">
                                        <input type="hidden" name="action" value="send"/>
                                        <input type="hidden" name="billId" value="<%= billId%>"/>
                                        <button type="submit" class="dropdown-item">📤 Gửi hóa đơn</button>
                                    </form>
                                    <% } else { %>
                                    <span class="dropdown-item text-muted" style="cursor: not-allowed;" title="Hóa đơn chưa đầy đủ dữ liệu">📤 Không thể gửi</span>
                                    <% }%>

                                    <a class="dropdown-item text-danger"
                                       href="<%= request.getContextPath() + "/admin/bill?action=cancel&billId=" + billId%>"
                                       onclick="return confirm('Bạn có chắc chắn muốn hủy hóa đơn này không?')">
                                        🗑 Hủy hóa đơn
                                    </a>
                                </div>
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

        </script>

    </body>
</html>