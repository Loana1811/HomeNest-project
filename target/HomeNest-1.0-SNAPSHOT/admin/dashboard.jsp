<%-- 
    Document   : dashboard
    Created on : Jun 14, 2025, 6:10:42 PM
    Author     : kloane
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    Integer totalRooms = (Integer) request.getAttribute("totalRooms");
    Integer totalTenants = (Integer) request.getAttribute("totalTenants");
    Integer unpaidBills = (Integer) request.getAttribute("unpaidBills");
    Integer readings30 = (Integer) request.getAttribute("readings30");
    List<String> chartLabels = (List<String>) request.getAttribute("chartLabels");
    List<Number> chartData = (List<Number>) request.getAttribute("chartData");
    String ctx = request.getContextPath();

    StringBuilder lblJs = new StringBuilder();
    StringBuilder dataJs = new StringBuilder();
    if (chartLabels != null) {
        for (int i = 0; i < chartLabels.size(); i++) {
            lblJs.append("\"").append(chartLabels.get(i)).append("\"");
            if (i < chartLabels.size() - 1) {
                lblJs.append(",");
            }
        }
    }
    if (chartData != null) {
        for (int i = 0; i < chartData.size(); i++) {
            dataJs.append(chartData.get(i));
            if (i < chartData.size() - 1) {
                dataJs.append(",");
            }
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>🏠 HomeNest - Admin Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            body {
                padding-top: 56px;
                background-color: #f1f4f8;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            /* Sidebar Styling */
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

            /* Main content */
            main {
                margin-left: 260px;
                padding: 2rem;
            }

            /* Card Stats */
            .card-stat {
                border-radius: 16px;
                color: white;
                padding: 1.25rem;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: start;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
                transition: transform 0.2s;
            }

            .card-stat:hover {
                transform: scale(1.02);
            }

            .bg-gradient-primary {
                background: linear-gradient(135deg, #007bff, #0056b3);
            }
            .bg-gradient-success {
                background: linear-gradient(135deg, #28a745, #218838);
            }
            .bg-gradient-warning {
                background: linear-gradient(135deg, #ffc107, #e0a800);
                color: #212529;
            }
            .bg-gradient-info {
                background: linear-gradient(135deg, #17a2b8, #117a8b);
            }

            .card-stat h5 {
                margin-bottom: 0.3rem;
                font-weight: 500;
            }

            .card-stat h2 {
                font-weight: 700;
                font-size: 2.2rem;
            }

            /* Button */
            .btn-gradient {
                background: linear-gradient(135deg, #4a90e2, #357abd);
                border: none;
                color: white;
                font-weight: 500;
                padding: 0.6rem 1.4rem;
                border-radius: 8px;
            }

            .btn-gradient:hover {
                background: linear-gradient(135deg, #357abd, #4a90e2);
                color: #eaf4ff;
            }

            /* Chart Card */
            .card-chart {
                border-radius: 14px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                border: 1px solid #e0e0e0;
            }

            /* Responsive */
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

        <div class="sidebar">
            <h5 class="text-primary">Admin Menu</h5>
            <ul class="nav flex-column">
                
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/account">Accounts</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/rooms?action=list">Rooms</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/tenant?action=list">Tenants</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/bill?action=list">Bills</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/utility?action=list">Utilities</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/record-reading">Record Usage</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/usage">View Usage List</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/Contracts">Contract</a>
                </li>


            </ul>
        </div>


        <main>
            <h2>📊 Admin Dashboard</h2>
            <div class="row g-3 mb-4">
                <div class="col-md-3">
                    <div class="card text-white bg-primary p-3">
                        <h5>Total Rooms</h5>
                        <h2><%= totalRooms != null ? totalRooms : 0%></h2>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-white bg-success p-3">
                        <h5>Tenants</h5>
                        <h2><%= totalTenants != null ? totalTenants : 0%></h2>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-dark bg-warning p-3">
                        <h5>Unpaid Bills</h5>
                        <h2><%= unpaidBills != null ? unpaidBills : 0%></h2>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-white bg-info p-3">
                        <h5>Readings (30d)</h5>
                        <h2><%= readings30 != null ? readings30 : 0%></h2>
                    </div>
                </div>

                <a href="${pageContext.request.contextPath}/admin/usage"
                   class="btn btn-primary mb-4">
                    🚰📊 View Usage
                </a>

            </div>

            <div class="card">
                <div class="card-header">📈 Electricity Usage (Last 7 days)</div>
                <div class="card-body">
                    <canvas id="usageChart" style="width:100%; height:300px;"></canvas>
                </div>
            </div>
        </main>

        <script>
            const labels = [<%= lblJs%>];
            const data = [<%= dataJs%>];

            const ctx2 = document.getElementById('usageChart').getContext('2d');
            new Chart(ctx2, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                            label: 'kWh used',
                            data: data,
                            fill: true,
                            tension: 0.3
                        }]
                },
                options: {scales: {y: {beginAtZero: true}}}
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>