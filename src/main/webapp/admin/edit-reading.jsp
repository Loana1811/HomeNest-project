<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.UtilityReading" %>

<%
    UtilityReading r = (UtilityReading) request.getAttribute("reading");
    String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Utility Reading</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            background: linear-gradient(180deg, #b2fff2 0%, #d0f0ff 50%, #e6f9ff 100%);
            color: #1a1a1a;
        }

        .navbar {
            background-color: #c8f7ec;
            border-bottom: 1px solid #2c3e50;
        }

        .navbar-brand {
            font-weight: bold;
            color: #000;
        }

        .sidebar {
            width: 240px;
            position: fixed;
            top: 56px;
            bottom: 0;
            left: 0;
            background: linear-gradient(to bottom, #ecfcf9 0%, #e0fff8 60%, #ffffff 100%);
            padding-top: 20px;
            overflow-y: auto;
            border-right: 1px solid #c0e0e0;
            box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
            z-index: 1000;
        }

        .sidebar .nav-link {
            color: #004c5f;
            padding: 12px 20px;
            transition: all 0.25s ease;
            font-weight: 500;
            font-size: 15px;
        }

        .sidebar .nav-link:hover {
            background-color: rgba(255, 255, 255, 0.5);
            color: #0d6efd;
            border-left: 3px solid #0d6efd;
            border-radius: 4px;
        }

        .main-content {
            margin-left: 240px;
            padding: 90px 30px 30px;
            min-height: 100vh;
        }

        .card-header {
            background-color: #ffc107;
            font-weight: bold;
        }

        .btn-success {
            background-color: #28a745;
            border-color: #28a745;
        }
    </style>
</head>

<body>

<!-- MAIN CONTENT -->
<div class="main-content">
    <div class="card shadow-sm">
        <div class="card-header">✏️ Edit Utility Reading</div>
        <div class="card-body">
            <form method="post" action="">
                <input type="hidden" name="readingId" value="<%= r.getReadingId() %>" />

                <div class="mb-3">
                    <label class="form-label fw-semibold">Old Reading</label>
                    <input type="text" class="form-control" value="<%= r.getOldReading() %>" disabled />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">New Reading</label>
                    <input type="number" step="0.01" name="newReading" class="form-control" required />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Reason</label>
                    <input type="text" name="reason" class="form-control" placeholder="Enter reason for editing" required />
                </div>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                <% } %>

                <div class="d-flex justify-content-between">
                    <button type="submit" class="btn btn-success">💾 Save</button>
                    <a href="<%= ctx %>/admin/usage" class="btn btn-outline-secondary">← Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
