<%-- 
    Document   : utility-list
    Created on : Jun 13, 2025, 8:02:46 AM
    Author     : kloane
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.UtilityType" %>
<%
    List<UtilityType> utilities = (List<UtilityType>) request.getAttribute("utilities");
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Utility List</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    </head>
    <body class="container mt-4">
        <h3 class="mb-3">🧾 Manage Utilities</h3>

        <% if (utilities != null && !utilities.isEmpty()) { %>
        <table class="table table-bordered table-hover">
            <thead class="table-light">
                <tr>
                    <th>Name</th>
                    <th>Unit</th>
                    <th>Price (VND)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (UtilityType u : utilities) {%>
                <tr>
                    <td><%= u.getName()%></td>
                    <td><%= u.getUnit()%></td>
                    <td><%= String.format("%,.0f", u.getPrice())%></td>
                    <td>
                        <a href="utility?action=edit&id=<%= u.getId()%>" class="btn btn-sm btn-outline-primary">✏️</a>
                        <a href="utility?action=delete&id=<%= u.getId()%>" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this item?')">🗑️</a>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } else { %>
        <div class="alert alert-info">No utilities found.</div>
        <% }%>
        <div class="mt-4">
            <a href="utility?action=history" class="btn btn-outline-secondary">📜 View Price History</a>
        </div>


    </body>
</html>
