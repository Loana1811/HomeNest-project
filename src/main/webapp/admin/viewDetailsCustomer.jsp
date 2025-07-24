<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.*" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Customer Details</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
            }
            .container {
                max-width: 1000px;
                margin: 40px auto;
                background: white;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 0 15px rgba(0,0,0,0.1);
            }
            h1 {
                color: #007bff;
                text-align: center;
                margin-bottom: 40px;
                font-weight: bold;
            }
            h4, h5 {
                color: #343a40;
                border-bottom: 2px solid #dee2e6;
                padding-bottom: 10px;
                margin-bottom: 20px;
                font-weight: bold;
            }
            .card {
                border: none;
                box-shadow: 0 3px 8px rgba(0,0,0,0.05);
                margin-bottom: 30px;
                background-color: #fff;
            }
            .card-body {
                padding: 20px;
            }
            .table {
                width: 100%;
                margin-bottom: 0;
                border-collapse: collapse;
            }
            .table th {
                background-color: #007bff;
                color: white;
                text-align: center;
            }
            .table td, .table th {
                padding: 10px;
                vertical-align: middle;
                border: 1px solid #dee2e6;
                text-align: center;
            }
            .alert-info {
                background-color: #e7f1ff;
                color: #31708f;
                border: 1px solid #bce8f1;
                padding: 10px;
                text-align: center;
            }
            ul {
                list-style-type: none;
                padding-left: 0;
                margin: 0;
            }
            ul li {
                margin-bottom: 10px;
                font-size: 16px;
            }
            ul li b {
                color: #495057;
            }
            .btn-secondary {
                background-color: #6c757d;
                border-color: #6c757d;
                font-size: 14px;
            }
            .btn-secondary:hover {
                background-color: #5a6268;
                border-color: #545b62;
            }
            @media (max-width: 768px) {
                .container {
                    padding: 20px;
                }
                .table {
                    font-size: 14px;
                }
            }
        </style>

    </head>
    <body >
        <div class="container mt-4">
            <h1>Customer Full Details</h1>
            <c:choose>
                <c:when test="${not empty details}">
                    <%
                        List<Map<String, Object>> details = (List<Map<String, Object>>) request.getAttribute("details");
                        Map<String, Object> customerRow = details != null && !details.isEmpty() ? details.get(0) : null;

                        // Chuẩn bị các set chứa ID duy nhất cho từng bảng (Contract, Bill, UtilityReading, ...)
                        Set<Integer> contractIds = new LinkedHashSet<>();
                        Set<Integer> billIds = new LinkedHashSet<>();
                        Set<Integer> readingIds = new LinkedHashSet<>();
                        Set<Integer> requestIds = new LinkedHashSet<>();
                        Set<Integer> incurredFeeIds = new LinkedHashSet<>();

                        for (Map<String, Object> row : details) {
                            if (row.get("ContractID") != null) contractIds.add((Integer) row.get("ContractID"));
                            if (row.get("BillID") != null) billIds.add((Integer) row.get("BillID"));
                            if (row.get("ReadingID") != null) readingIds.add((Integer) row.get("ReadingID"));
                            if (row.get("RequestID") != null) requestIds.add((Integer) row.get("RequestID"));
                            if (row.get("IncurredFeeID") != null) incurredFeeIds.add((Integer) row.get("IncurredFeeID"));
                        }
                    %>
                    <!-- Customer Info -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h4>Customer Info</h4>
                            <ul>
                                <li><b>ID:</b> <%= customerRow != null ? customerRow.get("CustomerID") : "" %></li>
                                <li><b>Name:</b> <%= customerRow != null ? customerRow.get("CustomerName") : "" %></li>
                                <li><b>Phone:</b> <%= customerRow != null ? customerRow.get("PhoneNumber") : "" %></li>
                                <li><b>Email:</b> <%= customerRow != null ? customerRow.get("Email") : "" %></li>
                                <li><b>Status:</b> <%= customerRow != null ? customerRow.get("CustomerStatus") : "" %></li>
                            </ul>
                        </div>
                    </div>
                    <!-- Contracts -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5>Contracts</h5>
                            <% if (contractIds.isEmpty()) { %>
                            <div class="alert alert-info">No contracts yet</div>
                            <% } else { %>
                            <table class="table table-striped table-hover table-bordered table-sm">
                                <thead>
                                    <tr>
                                        <th>Contract ID</th><th>Tenant</th><th>Room</th>
                                        <th>Start</th><th>End</th><th>Status</th><th>Created</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Integer cid : contractIds) {
                                            for (Map<String, Object> row : details) {
                                                if (cid.equals(row.get("ContractID"))) {
                                    %>
                                    <tr>
                                        <td><%= row.get("ContractID") %></td>
                                        <td>
                                            <%= row.get("TenantID") %><br>
                                            Join: <%= row.get("JoinDate") %>
                                        </td>
                                        <td>
                                            <%= row.get("RoomNumber") %> (<%= row.get("RoomType") %>)<br>
                                            <%= row.get("Location") %>
                                        </td>
                                        <td><%= row.get("StartDate") %></td>
                                        <td><%= row.get("EndDate") %></td>
                                        <td><%= row.get("ContractStatus") %></td>
                                        <td><%= row.get("ContractCreatedAt") %></td>
                                    </tr>
                                    <% break; }}} %>
                                </tbody>
                            </table>
                            <% } %>
                        </div>
                    </div>

                    <!-- Bills -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5>Bills</h5>
                            <% if (billIds.isEmpty()) { %>
                            <div class="alert alert-info">No bills yet</div>
                            <% } else { %>
                            <table class="table table-striped table-hover table-bordered table-sm">
                                <thead>
                                    <tr>
                                        <th>Bill ID</th><th>Date</th><th>Total</th><th>Status</th>
                                        <th>Room Rent</th><th>Elec</th><th>Water</th><th>Wifi</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Integer bid : billIds) {
                                            for (Map<String, Object> row : details) {
                                                if (bid.equals(row.get("BillID"))) {
                                    %>
                                    <tr>
                                        <td><%= row.get("BillID") %></td>
                                        <td><%= row.get("BillDate") %></td>
                                        <td><%= row.get("TotalAmount") %></td>
                                        <td><%= row.get("BillStatus") %></td>
                                        <td><%= row.get("RoomRent") %></td>
                                        <td><%= row.get("ElectricityCost") %></td>
                                        <td><%= row.get("WaterCost") %></td>
                                        <td><%= row.get("WifiCost") %></td>
                                    </tr>
                                    <% break; }}} %>
                                </tbody>
                            </table>
                            <% } %>
                        </div>
                    </div>

                    <!-- Utility Readings -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5>Utility Readings</h5>
                            <% if (readingIds.isEmpty()) { %>
                            <div class="alert alert-info">No utility readings yet</div>
                            <% } else { %>
                            <table class="table table-striped table-hover table-bordered table-sm">
                                <thead>
                                    <tr>
                                        <th>ReadingID</th><th>Type</th><th>Date</th>
                                        <th>Old</th><th>New</th><th>Used</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Integer rid : readingIds) {
                                            for (Map<String, Object> row : details) {
                                                if (rid.equals(row.get("ReadingID"))) {
                                    %>
                                    <tr>
                                        <td><%= row.get("ReadingID") %></td>
                                        <td><%= row.get("UtilityName") %></td>
                                        <td><%= row.get("ReadingDate") %></td>
                                        <td><%= row.get("OldReading") %></td>
                                        <td><%= row.get("NewReading") %></td>
                                        <td><%= row.get("PriceUsed") %></td>
                                    </tr>
                                    <% break; }}} %>
                                </tbody>
                            </table>
                            <% } %>
                        </div>
                    </div>

                    <!-- Rental Requests -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5>Rental Requests</h5>
                            <% if (requestIds.isEmpty()) { %>
                            <div class="alert alert-info">No rental requests yet</div>
                            <% } else { %>
                            <table class="table table-striped table-hover table-bordered table-sm">
                                <thead>
                                    <tr>
                                        <th>ReqID</th><th>Date</th><th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Integer rqid : requestIds) {
                                            for (Map<String, Object> row : details) {
                                                if (rqid.equals(row.get("RequestID"))) {
                                    %>
                                    <tr>
                                        <td><%= row.get("RequestID") %></td>
                                        <td><%= row.get("RequestDate") %></td>
                                        <td><%= row.get("RequestStatus") %></td>
                                    </tr>
                                    <% break; }}} %>
                                </tbody>
                            </table>
                            <% } %>
                        </div>
                    </div>

                    <!-- Incurred Fees -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5>Incurred Fees</h5>
                            <% if (incurredFeeIds.isEmpty()) { %>
                            <div class="alert alert-info">No incurred fees yet</div>
                            <% } else { %>
                            <table class="table table-striped table-hover table-bordered table-sm">
                                <thead>
                                    <tr>
                                        <th>FeeID</th><th>Fee Name</th><th>Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (Integer fid : incurredFeeIds) {
                                            for (Map<String, Object> row : details) {
                                                if (fid.equals(row.get("IncurredFeeID"))) {
                                    %>
                                    <tr>
                                        <td><%= row.get("IncurredFeeID") %></td>
                                        <td><%= row.get("FeeName") %></td>
                                        <td><%= row.get("Amount") %></td>
                                    </tr>
                                    <% break; }}} %>
                                </tbody>
                            </table>
                            <% } %>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">No details found for this customer.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>