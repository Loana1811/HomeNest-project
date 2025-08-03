<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.Report"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Report Management</title>
    <style>
        :root {
            --primary-color: #1e3b8a;
            --secondary-color: #3f5fa6;
            --light-bg: #f4f7fc;
            --border-color: #e0e0e0;
            --success-bg: #d1fae5;
            --error-bg: #fee2e2;
        }

        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: var(--light-bg);
            margin: 0;
            padding: 80px 20px 20px 250px; /* avoid sidebar/header */
        }

        .container {
            max-width: 1200px;
            margin: auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
            animation: fadeIn 0.5s ease-in-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h1 {
            color: var(--primary-color);
            font-weight: 700;
            text-align: center;
            margin-bottom: 25px;
            text-transform: uppercase;
        }

        .filter-section {
            background-color: #eef2ff;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid var(--border-color);
            text-align: left;
        }

        th {
            background: linear-gradient(to right, var(--primary-color), var(--secondary-color));
            color: white;
            position: sticky;
            top: 0;
        }

        tbody tr:hover {
            background-color: #f0f4ff;
            transition: background-color 0.3s ease;
        }

        .status-pending {
            color: #f59e0b;
            font-weight: 600;
        }

        .status-inprogress {
            color: #3b82f6;
            font-weight: 600;
        }

        .status-resolved {
            color: #10b981;
            font-weight: 600;
        }

        .status-closed {
            color: #6b7280;
            font-weight: 600;
        }

        select {
            padding: 6px 10px;
            border-radius: 6px;
            border: 1px solid #ccc;
        }

        .btn {
            padding: 6px 14px;
            border-radius: 6px;
            background-color: var(--primary-color);
            color: white;
            border: none;
            transition: background-color 0.3s ease, transform 0.2s;
            cursor: pointer;
        }

        .btn:hover {
            background-color: #162e6f;
            transform: translateY(-1px);
        }

        .message {
            padding: 12px;
            margin-bottom: 15px;
            border-radius: 6px;
        }

        .success {
            background-color: var(--success-bg);
            color: #065f46;
        }

        .error {
            background-color: var(--error-bg);
            color: #991b1b;
        }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: #888;
        }

        .action-form {
            display: flex;
            gap: 6px;
        }

        .disabled-form {
            opacity: 0.6;
            pointer-events: none;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Report Management</h1>

    <%-- Messages --%>
    <%
        String message = (String) session.getAttribute("message");
        String error = (String) session.getAttribute("error");
    %>
    <% if (message != null) { %>
        <div class="message success"><%= message %></div>
        <% session.removeAttribute("message"); %>
    <% } %>
    <% if (error != null) { %>
        <div class="message error"><%= error %></div>
        <% session.removeAttribute("error"); %>
    <% } %>

    <div class="filter-section">
        <form method="get" action="adminReport">
            <label for="statusFilter">Filter by Status:</label>
            <select name="statusFilter" id="statusFilter">
                <option value="" <%= "".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>All</option>
                <option value="Pending" <%= "Pending".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>Pending</option>
                <option value="InProgress" <%= "InProgress".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>In Progress</option>
                <option value="Resolved" <%= "Resolved".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>Resolved</option>
                <option value="Closed" <%= "Closed".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>Closed</option>
            </select>
            <button type="submit" class="btn">Apply</button>
        </form>
    </div>

    <table>
        <thead>
        <tr>
            <th>Customer</th>
            <th>Room</th>
            <th>Contract Period</th>
            <th>Description</th>
            <th>Reported At</th>
            <th>Status</th>
            <th>Handled By</th>
            <th>Resolved At</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Report> reports = (List<Report>) request.getAttribute("reports");
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (reports != null && !reports.isEmpty()) {
                for (Report report : reports) {
        %>
        <tr>
            <td><%= report.getCustomerName() != null ? report.getCustomerName() : "N/A" %></td>
            <td><%= report.getRoomNumber() != null ? report.getRoomNumber() : "N/A" %></td>
            <td>
                <% if (report.getStartDate() != null) { %>
                    <%= sdf.format(report.getStartDate()) %> -
                    <%= report.getEndDate() != null ? sdf.format(report.getEndDate()) : "Ongoing" %>
                <% } else { %>
                    N/A
                <% } %>
            </td>
            <td><%= report.getIssueDescription() != null ? report.getIssueDescription() : "N/A" %></td>
            <td><%= report.getReportCreatedAt() != null ? df.format(report.getReportCreatedAt()) : "N/A" %></td>
            <td class="status-<%= report.getReportStatus().toLowerCase() %>"><%= report.getReportStatus() %></td>
            <td>
                <% if (report.getResolvedBy() != null) {
                    if (report.getResolvedBy() == 1) out.print("Admin");
                    else if (report.getResolvedBy() == 2) out.print("Manager");
                    else out.print("Unknown");
                } else { %>-<% } %>
            </td>
            <td><%= report.getResolvedDate() != null ? df.format(report.getResolvedDate()) : "-" %></td>
            <td>
                <% if ("Closed".equals(report.getReportStatus())) { %>
                <div class="action-form disabled-form">
                    <select disabled><option selected>Closed</option></select>
                    <button class="btn" disabled>Update</button>
                </div>
                <% } else { %>
                <form class="action-form" method="post" action="adminReport">
                    <input type="hidden" name="reportID" value="<%= report.getReportID() %>"/>
                    <input type="hidden" name="action" value="resolve"/>
                    <select name="reportStatus">
                        <option value="Pending" <%= "Pending".equals(report.getReportStatus()) ? "selected" : "" %>>Pending</option>
                        <option value="InProgress" <%= "InProgress".equals(report.getReportStatus()) ? "selected" : "" %>>In Progress</option>
                        <option value="Resolved" <%= "Resolved".equals(report.getReportStatus()) ? "selected" : "" %>>Resolved</option>
                        <option value="Closed" <%= "Closed".equals(report.getReportStatus()) ? "selected" : "" %>>Closed</option>
                    </select>
                    <button type="submit" class="btn">Update</button>
                </form>
                <% } %>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="9" class="empty-state">No reports found</td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
</body>
</html>
