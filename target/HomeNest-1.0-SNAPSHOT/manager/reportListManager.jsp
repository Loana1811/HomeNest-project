
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.Report"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Manager Report Management</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }

        h1 {
            color: #333;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }

        .filter-section {
            margin-bottom: 20px;
            padding: 15px;
            background-color: #f9f9f9;
            border-radius: 5px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #2196F3; /* Different color for manager */
            color: white;
            position: sticky;
            top: 0;
        }

        tr:hover {
            background-color: #f5f5f5;
        }

        .status-pending {
            color: #FF9800;
            font-weight: bold;
        }

        .status-inprogress {
            color: #2196F3;
            font-weight: bold;
        }

        .status-resolved {
            color: #4CAF50;
            font-weight: bold;
        }

        .status-closed {
            color: #607D8B;
            font-weight: bold;
        }

        select {
            padding: 6px;
            border-radius: 4px;
            border: 1px solid #ddd;
        }

        .btn {
            padding: 6px 12px;
            background-color: #2196F3; /* Different color for manager */
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .btn:hover {
            background-color: #1976D2;
        }

        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
        }

        .success {
            background-color: #dff0d8;
            color: #3c763d;
        }

        .error {
            background-color: #f2dede;
            color: #a94442;
        }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: #777;
        }

        .action-form {
            display: flex;
            gap: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Manager Report Management</h1>

        <%-- Display success/error messages --%>
        <%
            String message = (String) session.getAttribute("message");
            String error = (String) session.getAttribute("error");
            Logger logger = Logger.getLogger(this.getClass().getName()); // Corrected to use the JSP's compiled class

            if (message != null) {
        %>
            <div class="message success">
                <%= message %>
            </div>
        <%
                session.removeAttribute("message");
            }

            if (error != null) {
        %>
            <div class="message error">
                <%= error %>
            </div>
        <%
                session.removeAttribute("error");
                logger.log(Level.WARNING, "Error displayed: " + error);
            }
        %>

        <div class="filter-section">
            <form method="get" action="adminReport">
                <label for="statusFilter">Filter by Status:</label>
                <select name="statusFilter" id="statusFilter">
                    <option value="" <%= "".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>All Statuses</option>
                    <option value="Pending" <%= "Pending".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>Pending</option>
                    <option value="InProgress" <%= "InProgress".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>In Progress</option>
                    <option value="Resolved" <%= "Resolved".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>Resolved</option>
                    <option value="Closed" <%= "Closed".equals(request.getAttribute("currentFilter")) ? "selected" : "" %>>Closed</option>
                </select>

                <button type="submit" class="btn">Apply Filter</button>
            </form>
        </div>

        <table>
            <thead>
                <tr>
                    <th>Report ID</th>
                    <th>Customer</th>
                    <th>Room Number</th>
                    <th>Contract Period</th>
                    <th>Issue Description</th>
                    <th>Report Date</th>
                    <th>Status</th>
                    <th>Resolved By</th>
                    <th>Resolution Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Report> reports = (List<Report>) request.getAttribute("reports");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    SimpleDateFormat contractDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    if (reports == null) {
                        logger.log(Level.SEVERE, "Reports attribute is null");
                    } else if (reports.isEmpty()) {
                        logger.log(Level.INFO, "No reports found");
                    } else {
                        for (Report report : reports) {
                            if (report == null) {
                                logger.log(Level.SEVERE, "Null report object in list");
                                continue;
                            }
                %>
                <tr>
                    <td><%= report.getReportID() %></td>
                    <td><%= report.getCustomerName() != null ? report.getCustomerName() : "N/A" %></td>
                    <td><%= report.getRoomNumber() != null ? report.getRoomNumber() : "N/A" %></td>
                    <td>
                        <% if (report.getStartDate() != null) { %>
                            <%= contractDateFormat.format(report.getStartDate()) %> - 
                            <%= report.getEndDate() != null ? contractDateFormat.format(report.getEndDate()) : "Ongoing" %>
                        <% } else { %>
                            N/A
                        <% } %>
                    </td>
                    <td><%= report.getIssueDescription() != null ? report.getIssueDescription() : "N/A" %></td>
                    <td><%= report.getReportCreatedAt() != null ? dateFormat.format(report.getReportCreatedAt()) : "N/A" %></td>
                    <td class="status-<%= report.getReportStatus() != null ? report.getReportStatus().toLowerCase() : "" %>">
                        <%= report.getReportStatus() != null ? report.getReportStatus() : "N/A" %>
                    </td>
                    <td>
                        <% if (report.getResolvedBy() != null) {
                            if (report.getResolvedBy() == 1) { %>
                                Admin
                            <% } else if (report.getResolvedBy() == 2) { %>
                                Manager
                            <% } else { %>
                                Unknown
                            <% }
                        } else { %>
                            -
                        <% } %>
                    </td>
                    <td>
                        <% if (report.getResolvedDate() != null) { %>
                            <%= dateFormat.format(report.getResolvedDate()) %>
                        <% } else { %>
                            -
                        <% } %>
                    </td>
                    <td>
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
                    </td>
                </tr>
                <%
                        }
                    }
                    if (reports == null || reports.isEmpty()) {
                %>
                <tr>
                    <td colspan="10" class="empty-state">
                        No reports found for your assigned block
                    </td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>
