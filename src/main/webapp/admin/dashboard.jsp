<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();
%>

<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>🏠 HomeNest - Admin Panel</title>
    <!-- Bootstrap + Font Awesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
       :root {
  --primary-color: #1e3b8a;         /* ✅ Màu chủ đạo: xanh navy đậm – dùng cho header, button, title */
  --secondary-color: #c7d2fe;       /* ✅ Màu phụ: xanh pastel nhạt – dùng cho background nhẹ, nhấn phụ */
  --accent-color: #2a4d69;          /* ✅ Màu nhấn: xanh đen xám – dùng hover, viền, icon */
  --text-color: #111827;            /* ✅ Màu chữ chính: gần đen – độ tương phản cao, dễ đọc */
  --white: #ffffff;                 /* ✅ Trắng chuẩn – nền hoặc chữ khi contrast cao */
  --background-color: #f5f9fc;      /* ✅ Nền chung: trắng xanh nhạt – tạo cảm giác sáng, sạch */
  --sidebar-color: #003459;         /* ✅ Sidebar: xanh navy sẫm – dùng cho nền sidebar cố định */
  --shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* ✅ Bóng nhẹ – dùng cho card, box */
  --border-radius: 12px;            /* ✅ Bo góc chuẩn – dùng cho card, nút, khung input */
}


        body {
            font-family: 'Segoe UI', sans-serif;
            background: var(--background-color);
            color: var(--text-color);
            margin: 0;
        }

        /* Sidebar */
        #sidebar {
            width: 240px;
            position: fixed;
            height: 100vh;
            background-color: var(--primary-color);
            padding: 20px;
            transition: background 0.3s ease;
        }

        #sidebar .nav-link {
            font-weight: 500;
            border-radius: var(--border-radius);
            color: var(--text-color);
            transition: background 0.25s ease, color 0.25s ease;
        }

        #sidebar .nav-link:hover,
        #sidebar .nav-link.active {
            background: linear-gradient(135deg, var(--accent-color), var(--primary-color));
            color: #fff !important;
        }

        #sidebar .nav-link i {
            width: 20px;
        }

        .main-content {
            margin-left: 240px;
            padding: 40px;
            min-height: 100vh;
            color: #1a1a1a;
        }

        @media (max-width: 768px) {
            #sidebar {
                display: none;
            }

            .main-content {
                margin: 0;
                padding: 20px;
            }
        }

        .dropdown-toggle::after {
            margin-left: 8px;
        }
    </style>
</head>
<body>

<!-- SIDEBAR -->
<nav id="sidebar" class="d-flex flex-column text-white">
    <a href="${ctx}/admin/dashboard" class="d-flex align-items-center mb-4 text-white text-decoration-none">
        <i class="fas fa-home me-2"></i><span class="fs-5 fw-bold">HomeNest Admin</span>
    </a>
    <ul class="nav nav-pills flex-column mb-auto">
        <li><a href="${ctx}/admin/dashboard" class="nav-link active"><i class="fas fa-tachometer-alt me-2"></i> Dashboard</a></li>
        <li><a href="${ctx}/admin/rooms" class="nav-link"><i class="fas fa-door-open me-2"></i> Rooms</a></li>
        <li><a href="${ctx}/admin/tenants" class="nav-link"><i class="fas fa-users me-2"></i> Tenants</a></li>
        <li><a href="${ctx}/admin/bills" class="nav-link"><i class="fas fa-file-invoice-dollar me-2"></i> Bills</a></li>
        <li><a href="${ctx}/admin/usage" class="nav-link"><i class="fas fa-bolt me-2"></i> Usage</a></li>
        <li><a href="${ctx}/admin/settings" class="nav-link"><i class="fas fa-cogs me-2"></i> Settings</a></li>
    </ul>
    <hr>
    <div class="dropdown">
        <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" data-bs-toggle="dropdown">
            <img src="https://via.placeholder.com/32" alt="" class="rounded-circle me-2">
            <strong>Admin</strong>
        </a>
        <ul class="dropdown-menu dropdown-menu-dark text-small shadow">
            <li><a class="dropdown-item" href="#">Profile</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="${ctx}/Logout">Sign out</a></li>
        </ul>
    </div>
</nav>

<!-- MAIN CONTENT -->
<main class="main-content">
    <h2>👋 Welcome to HomeNest Admin Panel</h2>
    <p class="text-muted">Use the sidebar to navigate through the system.</p>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
