<%@ page contentType="text/html;charset=UTF-8" %>
<%
String ctx = request.getContextPath();
%>  
<%@ page import="java.util.*, model.Block" %>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

<%
    List<Block> blockList = (List<Block>) request.getAttribute("blockList");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Quản lý Khu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />

    <style>
        :root {
            --sidebar-bg: #003459;
            --light-bg: #ecfcf9;
            --highlight-bg: #e0f7f7;
            --teal: #007c91;
            --hover-dark: #006666;
        }

        body {
            background-color: var(--light-bg);
            font-family: 'Segoe UI', sans-serif;
            margin: 0;
        }

        .sidebar {
            width: 260px;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            background-color: var(--sidebar-bg);
            color: white;
            padding: 2rem 1rem;
        }

        .sidebar h4 {
            font-weight: bold;
            margin-bottom: 2rem;
            font-size: 1.8rem;
        }

        .sidebar a {
            color: white;
            text-decoration: none;
            display: block;
            margin: 1rem 0;
            font-weight: 500;
        }

        .sidebar a:hover {
            text-decoration: underline;
            color: #aad4ff;
        }

        .main {
            margin-left: 270px;
            padding: 2rem;
        }

        .btn-teal {
            background-color: var(--teal);
            color: white;
            border-radius: 30px;
        }

        .btn-teal:hover {
            background-color: var(--hover-dark);
            color: white;
        }

        .card-header {
            background-color: var(--highlight-bg);
            font-weight: 600;
            font-size: 18px;
        }

        .badge-full {
            background-color: #dc3545;
        }

        .badge-available {
            background-color: #198754;
        }

        .bi {
            margin-right: 5px;
        }

        @media (max-width: 768px) {
            .sidebar {
                display: none;
            }
            .main {
                margin-left: 0;
                padding: 1rem;
            }
        }
    </style>
</head>
<body>

<!-- Nội dung chính -->
<div class="main">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-dark">Quản lý Khu</h2>
        <div>
            <a href="rooms?action=new" class="btn btn-teal me-2">
                <i class="bi bi-plus-circle"></i> Thêm Phòng
            </a>
            <a href="blocks?action=new" class="btn btn-outline-dark">
                <i class="bi bi-building-add"></i> Thêm Khu
            </a>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-header">Danh sách các Khu</div>
        <div class="card-body p-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th><i class="bi bi-building"></i> Khu</th>
                        <th><i class="bi bi-grid-3x3-gap"></i> Số phòng</th>
                        <th><i class="bi bi-check-circle"></i> Phòng trống</th>
                        <th><i class="bi bi-info-circle"></i> Trạng thái</th>
                        <th class="text-center"><i class="bi bi-tools"></i> Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (blockList != null && !blockList.isEmpty()) {
                        for (Block b : blockList) {
                            boolean isFull = b.getAvailableRooms() == 0;
                    %>
                    <tr>
                        <td><%= b.getBlockName() %></td>
                        <td><%= b.getRoomCount() %></td>
                        <td><%= b.getAvailableRooms() %></td>
                        <td>
                            <span class="badge <%= isFull ? "badge-full" : "badge-available" %>">
                                <%= isFull ? "Đã đầy" : "Còn trống" %>
                            </span>
                        </td>
                        <td class="text-center">
                            <a href="blocks?action=edit&id=<%= b.getBlockID() %>" class="btn btn-warning btn-sm">
                                <i class="bi bi-pencil"></i> Sửa
                            </a>
                        </td>
                    </tr>
                    <% }
                    } else { %>
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">Không có khu nào được tìm thấy.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
