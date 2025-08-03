<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Contract" %>
<%@ page import="model.Customer" %>
<%@ page import="model.Tenant" %>

<%
    List<Contract> contracts = (List<Contract>) request.getAttribute("contracts");
    Customer customer = (Customer) request.getAttribute("customer");
    Tenant tenant = (Tenant) request.getAttribute("tenant");
    String ctx = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Contract History</title>
    <link href="https://fonts.googleapis.com/css2?family=Segoe+UI:wght@400;600&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f0f9f4;
            color: #2e3c3e;
            margin: 0;
            padding: 40px 20px 80px;
        }

        h2 {
            font-size: 24px;
            font-weight: 600;
            color: #2e7d32;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: #ffffff;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05);
        }

        th, td {
            padding: 14px 20px;
            text-align: center;
        }

        th {
            background: #1b5e20;
            color: #ffffff;
            font-weight: 600;
            font-size: 15px;
        }

        tr:nth-child(even) {
            background-color: #e8f5e9;
        }

        tr:hover {
            background-color: #c8e6c9;
        }

        .btn {
            text-decoration: none;
            padding: 8px 14px;
            background-color: #43a047;
            color: white;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            transition: 0.3s ease;
        }

        .btn:hover {
            background-color: #2e7d32;
        }

        .no-data {
            font-style: italic;
            color: #888;
            padding: 20px;
            text-align: center;
        }

        @media (max-width: 768px) {
            table {
                font-size: 14px;
            }

            th, td {
                padding: 10px 8px;
            }

            h2 {
                font-size: 20px;
            }
        }
    </style>
</head>
<body>
    <h2>Contract History for</h2>
    <% if (contracts == null || contracts.isEmpty()) { %>
        <div class="no-data">No contract history found.</div>
    <% } else { %>
        <table>
            <tr>
                <th>Contract ID</th>
                <th>Room</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Status</th>
                <th>Created At</th>
                <th>Actions</th>
            </tr>
            <% for (Contract c : contracts) { %>
                <tr>
                    <td><%= c.getContractId() %></td>
                    <td><%= c.getRoomNumber() %></td>
                    <td><%= c.getStartDate() %></td>
                    <td><%= c.getEndDate() %></td>
                    <td><%= c.getContractStatus() %></td>
                    <td><%= c.getContractCreatedAt() %></td>
                   
                </tr>
            <% } %>
        </table>
    <% } %>
</body>
</html>

<%@ include file="/WEB-INF/inclu/footer.jsp" %>
