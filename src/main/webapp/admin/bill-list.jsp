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
    <title>Bill List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* === GLOBAL === */
        body {
            background: linear-gradient(to right, #dff4ff, #eef7ff);
            font-family: 'Segoe UI', sans-serif;
            color: #1e3a5f;
            padding: 20px 30px;
        }

        h3 {
            font-weight: bold;
            color: #1e3b8a;
            margin-bottom: 20px;
        }

        /* === CARD / TABLE === */
        .card {
            border-radius: 16px;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
            border: none;
        }

        .table {
            border-radius: 12px;
            overflow: hidden;
            background-color: #fff;
        }

        .table thead {
            background-color: #1e3b8a;
            color: white;
            text-align: center;
        }

        .table-responsive {
            overflow-x: auto;
            overflow-y: visible !important;
            position: relative;
            z-index: 1;
        }

        .table tbody tr:hover {
            background-color: #eef2ff;
            transition: 0.3s ease;
        }

        /* === BUTTONS === */
        .btn {
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.3s ease-in-out;
        }

        .btn-primary:hover {
            background-color: #2563eb;
            transform: scale(1.05);
        }

        .btn-success:hover {
            background-color: #15803d;
            transform: scale(1.05);
        }

        .btn-danger:hover {
            background-color: #dc2626;
            transform: scale(1.05);
        }

        .btn-create-bill {
            background-color: #1e3b8a;
            color: white;
            padding: 12px 20px;
            border-radius: 10px;
            font-weight: bold;
            transition: 0.3s ease-in-out;
        }

        .btn-create-bill:hover {
            background-color: #2563eb;
            transform: scale(1.05);
        }

        /* === DROPDOWN === */
        .dot-button {
            background: transparent;
            border: none;
            font-size: 20px;
            cursor: pointer;
            color: #1e3b8a;
            padding: 5px;
            transition: transform 0.2s ease-in-out;
        }

        .dot-button:hover {
            color: #2563eb;
            transform: scale(1.3);
        }

        .dropdown {
            position: relative;
            display: inline-block;
        }

        .dropdown-content {
            display: none;
            position: absolute;
            background-color: white;
            min-width: 180px;
            border-radius: 12px;
            box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.12);
            z-index: 1000;
            margin-top: 5px;
            padding: 10px 0;
            right: 0;
            transform: translateX(0);
        }

        .dropdown-content.show {
            display: block;
        }

        .dropdown-content a,
        .dropdown-content form .dropdown-item {
            color: #1e3b8a;
            padding: 10px 16px;
            text-decoration: none;
            display: block;
            font-weight: 500;
            transition: background 0.2s ease-in-out, padding-left 0.2s ease;
            white-space: nowrap;
        }

        .dropdown-content a:hover,
        .dropdown-content form .dropdown-item:hover {
            background-color: #e0e7ff;
            padding-left: 20px;
        }

        .dropdown-content .text-danger:hover {
            background-color: #ffe4e6;
            color: #dc2626;
        }

        /* === BADGES === */
        .badge-unpaid {
            background-color: #dc2626;
            color: white;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.85rem;
        }

        .badge-paid {
            background-color: #16a34a;
            color: white;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.85rem;
        }

        /* === ANIMATIONS === */
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* === TABLE CELL FIX === */
        td {
            overflow: visible !important;
            position: relative;
        }

        /* Responsive fix for small screens */
        @media (max-width: 768px) {
            .dropdown-content {
                min-width: 150px;
                right: -10px;
            }
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
                        <th>Room Rent</th>
                        <th>Electricity</th>
                        <th>Water</th>
                        <th>Trash</th>
                        <th>Wi-Fi</th>
                        <th>Deposit</th>
                        <th>Total</th>
                        <th>Collected</th>
                        <th>Due</th>
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
                        <td><%= isElectricityFree ? FormatUtil.formatMoney(electricity) : "0₫" %></td>
                        <td><%= isWaterFree ? FormatUtil.formatMoney(water) : "0₫" %></td>
                        <td><%= isTrashFree ? FormatUtil.formatMoney(trash) : "0₫" %></td>
                        <td><%= isWifiFree ? FormatUtil.formatMoney(wifi) : "0₫" %></td>
                        <td><%= (deposit != null) ? FormatUtil.formatMoney(deposit) : "--" %></td>
                        <td><fmt:formatNumber value="<%= totalAmount %>" type="currency" currencySymbol="₫"/></td>
                        <td><fmt:formatNumber value="<%= totalPaid %>" type="currency" currencySymbol="₫"/></td>
                        <td><fmt:formatNumber value="<%= due %>" type="currency" currencySymbol="₫"/></td>
                        <td>
                            <% if ("PAID".equalsIgnoreCase(status)) { %>
                                <span class="badge badge-paid">✔ Paid</span>
                            <% } else { %>
                                <span class="badge badge-unpaid">Unpaid</span>
                            <% } %>
                        </td>
                        <td>
                            <% if (!"PAID".equalsIgnoreCase(status)) { %>
                                <a href="<%= ctx + "/admin/payment?billId=" + billId %>" class="btn btn-success btn-sm">Collect</a>
                            <% } else { %>
                                <span class="text-muted">✔</span>
                            <% } %>
                        </td>
                        <td>
                            <div class="dropdown drop-left">
                                <button class="dot-button">⋮</button>
                                <div class="dropdown-content">
                                    <a href="bill?action=view&billId=<%= billId %>">📄 View</a>
                                    <% if (!"PAID".equalsIgnoreCase(status)) { %>
                                        <a href="bill?action=edit&billId=<%= billId %>">✏️ Edit</a>
                                    <% } %>
                                    <a href="<%= ctx + "/admin/bill?action=print&billId=" + billId %>" target="_blank">🖨 Print</a>
                                    <% if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) { %>
                                        <form method="post" action="<%= ctx %>/admin/bill" style="margin: 0;">
                                            <input type="hidden" name="action" value="send"/>
                                            <input type="hidden" name="billId" value="<%= billId %>"/>
                                            <button type="submit" class="dropdown-item">📤 Send</button>
                                        </form>
                                    <% } else if (!"PAID".equalsIgnoreCase(status)) { %>
                                        <span class="dropdown-item text-muted" style="cursor: not-allowed;">📤 Cannot Send</span>
                                    <% } %>
                                    <% if (!"PAID".equalsIgnoreCase(status)) { %>
                                        <a class="dropdown-item text-danger" href="<%= ctx + "/admin/bill?action=cancel&billId=" + billId %>" onclick="return confirm('Are you sure you want to cancel this bill?')">🗑 Cancel</a>
                                    <% } %>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <% }} else { %>
                        <tr><td colspan="13" class="text-center text-muted">No bills found for this month.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <a href="bill?action=step&step=1" class="btn btn-create-bill mt-3">➕ Create New Bill</a>

        <!-- Pending Payment Proofs -->
        <h3 class="mt-5">Pending Payment Proofs</h3>
        <c:if test="${not empty pendingProofs}">
            <div class="table-responsive">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Room</th>
                            <th>Uploaded At</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="proof" items="${pendingProofs}">
                            <tr>
                                <td>${proof.roomNumber}</td>
                                <td><fmt:formatDate value="${proof.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/bill?action=viewProof&confId=${proof.confId}" target="_blank">View Image</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
        <c:if test="${empty pendingProofs}">
            <p class="text-muted">No pending payment proofs.</p>
        </c:if>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function closeAllDropdowns() {
            document.querySelectorAll('.dropdown-content').forEach(drop => {
                drop.classList.remove('show');
            });
        }

        document.addEventListener('click', function (event) {
            if (!event.target.closest('.dropdown')) {
                closeAllDropdowns();
            }
        });

        document.querySelectorAll('.dot-button').forEach(button => {
            button.addEventListener('click', function (event) {
                event.stopPropagation();
                closeAllDropdowns();

                const dropdown = this.nextElementSibling;
                dropdown.classList.add('show');

                const rect = dropdown.getBoundingClientRect();
                const windowWidth = window.innerWidth;
                if (rect.right > windowWidth - 20) {
                    dropdown.style.right = '0';
                    dropdown.style.left = 'auto';
                } else {
                    dropdown.style.left = '0';
                    dropdown.style.right = 'auto';
                }
            });
        });
    </script>
</body>
</html>