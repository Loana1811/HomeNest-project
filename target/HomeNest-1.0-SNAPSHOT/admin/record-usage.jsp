<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="model.UtilityUsageView" %>
<%@ page import="model.Block" %>
<%
    Map<String, List<UtilityUsageView>> groupedUsages = (Map<String, List<UtilityUsageView>>) request.getAttribute("groupedUsages");
    List<Block> blocks = (List<Block>) request.getAttribute("blocks");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tenant Usage Summary</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            padding-top: 56px;
            background-color: #f8f9fa;
        }
        .sidebar {
            position: fixed;
            top: 56px;
            left: 0;
            bottom: 0;
            width: 240px;
            background-color: #fff;
            border-right: 1px solid #dee2e6;
            padding: 1rem;
        }
        .main-content {
            margin-left: 260px;
            padding: 2rem 1rem;
        }
        .modal-dialog {
            max-width: 800px;
        }
    </style>
</head>
<body>

 <!-- NAVBAR -->
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
            <div class="container-fluid">
                <a class="navbar-brand" href="#">🏠 HomeNest</a>
                <button class="navbar-toggler" type="button"
                        data-bs-toggle="collapse" data-bs-target="#navMenu">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navMenu">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="<%= ctx%>/admin/logout">Logout</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- SIDEBAR -->
        <div class="sidebar">
            <h5 class="text-primary">Admin Menu</h5>
            <ul class="nav flex-column">
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/viewListAccount">Accounts</a></li>
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/admin/rooms?action=list">Rooms</a></li>
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/admin/tenant?action=list">Tenants</a></li>
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/admin/bill?action=list">Bills</a></li>
                <li class="nav-item"><a class="nav-link active" href="<%= ctx%>/admin/utility?action=list">Utilities</a></li>
            
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/admin/usage">View Usage List</a></li>
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/Contracts">Contract</a></li>
            </ul>
        </div>

<!-- MAIN CONTENT -->
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h4 class="fw-bold text-success">
                <i class="bi bi-bar-chart-fill me-2"></i>Tenant Usage in the Month
            </h4>
            <p class="text-muted fst-italic">Monthly usage statistics</p>
        </div>
        <button class="btn btn-primary" onclick="openRecordModal()">
            <i class="bi bi-plus-circle me-1"></i> Record Utility
        </button>
    </div>

    <% if (groupedUsages == null || groupedUsages.isEmpty()) { %>
        <div class="alert alert-warning">⚠ No usage data available.</div>
    <% } else {
        for (Map.Entry<String, List<UtilityUsageView>> entry : groupedUsages.entrySet()) {
            String blockName = entry.getKey();
            List<UtilityUsageView> usageList = entry.getValue();
    %>
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white fw-bold">
            🏢 Block: <%= blockName %>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered table-hover text-center align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Room</th>
                            <th>Utility</th>
                            <th>Previous</th>
                            <th>Current</th>
                            <th>Used</th>
                            <th>Total (₫)</th>
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
                            <td><%= u.getNewIndex() - u.getOldIndex() %></td>
                            <td><%= u.getPriceUsed() %></td>
                            <td><%= u.getChangedBy() %></td>
                            <td><%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(u.getReadingDate()) %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <% }} %>

    <!-- Modal -->
    <div class="modal fade" id="recordModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content" id="recordModalContent"></div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function openRecordModal() {
    var blockId = document.getElementById('blockId')?.value || '';
    var roomId = document.getElementById('roomId')?.value || '';
    var typeId = document.getElementById('typeId')?.value || '';
    var readingMonth = '';

    // Nếu usage list không có input readingMonth thì ép lấy tháng hiện tại
    try {
        readingMonth = document.getElementById('readingMonth')?.value;
    } catch (e) { readingMonth = ''; }
    if (!readingMonth || readingMonth === '') {
        var d = new Date();
        readingMonth = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0');
    }

    let url = '<%= ctx %>/admin/record-reading?';
    url += 'blockId=' + encodeURIComponent(blockId);
    url += '&roomId=' + encodeURIComponent(roomId);
    url += '&typeId=' + encodeURIComponent(typeId);
    url += '&readingMonth=' + encodeURIComponent(readingMonth);

    fetch(url)
        .then(response => response.text())
        .then(html => {
            document.getElementById('recordModalContent').innerHTML = html;
            new bootstrap.Modal(document.getElementById('recordModal')).show();
        })
        .catch(() => alert('Failed to load record form!'));
}



</script>
</body>
</html>
