<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="Model.UtilityUsageView" %>

<%
    Map<String, List<UtilityUsageView>> groupedUsages = (Map<String, List<UtilityUsageView>>) request.getAttribute("groupedUsages");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Utility Usage Records</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">

    <h2 class="mb-4">📊 Water & Electricity Usage Records</h2>
    <a href="dashboard" class="btn btn-secondary mb-3">← Back to Dashboard</a>

    <% if (groupedUsages == null) { %>
        <div class="alert alert-danger">❌ Data not found.</div>
    <% } else if (groupedUsages.isEmpty()) { %>
        <div class="alert alert-warning">⚠️ No usage records available.</div>
    <% } else {
        for (Map.Entry<String, List<UtilityUsageView>> entry : groupedUsages.entrySet()) {
            String blockName = entry.getKey();
            List<UtilityUsageView> usageList = entry.getValue();
    %>

    <div class="mb-4">
        <h4 class="text-primary">🏢 Block <%= blockName %></h4>
        <div class="table-responsive">
            <table class="table table-bordered table-hover align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>Room</th>
                        <th>Utility</th>
                        <th>Old</th>
                        <th>New</th>
                        <th>Used (₫)</th>
                        <th>By</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (UtilityUsageView u : usageList) { %>
                    <tr>
                        <td><%= u.getRoomNumber() %></td>
                        <td><%= u.getUtilityName() %></td>
                        <td><%= u.getOldIndex() %></td>
                        <td><%= u.getNewIndex() %></td>
                        <td><%= u.getPriceUsed() %></td>
                        <td><%= u.getChangedBy() %></td>
                        <td><%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(u.getReadingDate()) %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <% } } %>

</body>
</html>
