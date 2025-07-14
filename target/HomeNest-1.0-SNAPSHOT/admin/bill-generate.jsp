<%-- 
    Document   : bill-generate
    Created on : Jun 24, 2025, 9:33:29 PM
    Author     : kloane
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>🧾 Generate Monthly Bills</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<!-- Main content -->
<div class="container mt-5">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0">🧾 Generate Monthly Bills</h4>
        </div>
        <div class="card-body">

            <% if ("generated".equals(msg)) { %>
                <div class="alert alert-success">✅ Bills generated successfully!</div>
            <% } else if ("error".equals(msg)) { %>
                <div class="alert alert-danger">❌ Failed to generate bills!</div>
            <% } %>

            <form method="post" action="<%= ctx %>/admin/bill-generate">
                <div class="mb-3">
                    <label for="month" class="form-label">📅 Select Month</label>
                    <input type="month" id="month" name="month" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-success">
                    <i class="bi bi-cash-coin me-1"></i> Generate Bills
                </button>
                <a href="<%= ctx %>/admin/bill?action=list" class="btn btn-secondary ms-2">Back to Bill List</a>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</body>
</html>
