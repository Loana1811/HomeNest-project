<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="model.UtilityUsageView" %>

<%
    Map<String, List<UtilityUsageView>> groupedUsages = (Map<String, List<UtilityUsageView>>) request.getAttribute("groupedUsages");
    String ctx = request.getContextPath();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Usage Summary</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <style>
            body {
                padding-top: 56px;
                background-color: #f8f9fa;
                font-family: 'Segoe UI', sans-serif;
            }
            .sidebar {
                width: 240px;
                position: fixed;
                top: 56px;
                left: 0;
                bottom: 0;
                background-color: #ffffff;
                border-right: 1px solid #e0e0e0;
                padding: 1rem;
                box-shadow: 2px 0 6px rgba(0, 0, 0, 0.05);
                z-index: 1030;
            }
            .sidebar h5 {
                font-weight: 700;
                margin-bottom: 1rem;
            }
            .sidebar .nav-link {
                padding: 10px 14px;
                margin-bottom: 6px;
                color: #333;
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.2s;
            }
            .sidebar .nav-link:hover,
            .sidebar .nav-link.active {
                background: #0d6efd;
                color: white;
            }
            main {
                margin-left: 260px;
                padding: 2rem 1rem;
                max-width: 1200px;
            }
            @media (max-width: 768px) {
                .sidebar {
                    display: none;
                }
                main {
                    margin-left: 0;
                }
            }
        </style>
    </head>
    <body>
        <!-- NAVBAR -->
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
            <div class="container-fluid">
                <a class="navbar-brand" href="#">🏠 HomeNest</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
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
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/admin/utility?action=list">Utilities</a></li>
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/admin/record-reading">Record Usage</a></li>
                <li class="nav-item"><a class="nav-link active" href="<%= ctx%>/admin/usage">View Usage List</a></li>
                <li class="nav-item"><a class="nav-link" href="<%= ctx%>/Contracts">Contract</a></li>
            </ul>
        </div>

        <!-- MAIN CONTENT -->
        <main>
            <div class="mb-4">
                <h3 class="fw-bold text-success">
                    <i class="bi bi-bar-chart-fill me-2"></i>Tenant Usage in the Month
                </h3>
                <p class="text-muted fst-italic">Monthly usage statistics</p>
            </div>

            <!-- Toolbar -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <label class="form-label fw-semibold">Billing Month</label>
                    <input type="month" class="form-control" value="2025-06" style="width: 200px;" />
                </div>
                <button class="btn btn-success">
                    <i class="bi bi-file-earmark-excel-fill me-1"></i> Export to Excel
                </button>
            </div>

            <% if (groupedUsages == null) { %>
            <div class="alert alert-danger">❌ Data not found.</div>
            <% } else if (groupedUsages.isEmpty()) { %>
            <div class="alert alert-warning">⚠️ No usage records available.</div>
            <% } else {
                for (Map.Entry<String, List<UtilityUsageView>> entry : groupedUsages.entrySet()) {
                    String blockName = entry.getKey();
                    List<UtilityUsageView> usageList = entry.getValue();
            %>
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-primary text-white fw-bold">
                    🏢 Block: <%= blockName%>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle">
                            <thead class="table-light">
                                <tr class="text-center align-middle">
                                    <th rowspan="2">Room Name</th>
                                    <th rowspan="2">Utility</th>
                                    <th colspan="2">Reading</th>
                                    <th rowspan="2">Used</th>
                                    <th rowspan="2">Total Price(₫)</th>
                                    <th rowspan="2">Record By</th>
                                    <th rowspan="2">Reading Date</th>
                                </tr>
                                <tr class="text-center">
                                    <th>Previous</th>
                                    <th>Current</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (UtilityUsageView u : usageList) {%>
                                <tr>
                                    <td><%= u.getRoomNumber()%></td>
                                    <td><%= u.getUtilityName()%></td>
                                    <td><%= u.getOldIndex()%></td>
                                    <td><%= u.getNewIndex()%></td>
                                    <td><%= u.getNewIndex() - u.getOldIndex()%></td>
                                    <td><%= u.getPriceUsed()%></td>
                                    <td><%= u.getChangedBy()%></td>
                                    <td><%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(u.getReadingDate())%></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <% }
        }%>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
