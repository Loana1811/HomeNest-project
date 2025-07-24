<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Bootstrap -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Giao diện CSS tùy chỉnh -->
<style>
    :root {
        --sidebar-bg: #003459;
        --sidebar-link-color: #ffffff;
        --sidebar-hover-bg: rgba(255, 255, 255, 0.3);
        --sidebar-hover-color: #0d6efd;
        --main-bg: #ecfcf9;
        --main-content-bg: #f5f9fc;
        --main-text: #1a1a1a;
        --primary-btn: #0d6efd;
        --success-btn: #28a745;
    }

    body {
        margin: 0;
        font-family: "Segoe UI", sans-serif;
        background-color: var(--main-bg);
        color: var(--main-text);
    }

    /* SIDEBAR */
    .sidebar {
        width: 240px;
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        background: var(--sidebar-bg);
        padding-top: 30px;
        overflow-y: auto;
        box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
        z-index: 1000;
    }

    .sidebar h5 {
        padding-left: 20px;
        margin-bottom: 1.5rem;
        color: #aad4ff;
        font-weight: bold;
        font-size: 1.8rem;
    }

    .sidebar .nav-link {
        color: var(--sidebar-link-color);
        padding: 16px 30px;
        font-weight: 600;
        font-size: 18.5px;
        border-left: 3px solid transparent;
        transition: all 0.25s ease;
    }

    .sidebar .nav-link:hover {
        background-color: var(--sidebar-hover-bg);
        color: var(--sidebar-hover-color);
        border-left: 3px solid var(--sidebar-hover-color);
        border-radius: 4px;
    }

    .sidebar .nav-link.active {
        background-color: var(--primary-btn);
        color: #fff !important;
        font-weight: bold;
        border-left: 3px solid #084298;
        border-radius: 4px;
    }

    /* MAIN CONTENT */
    .main-content {
        margin-left: 240px;
        padding: 30px;
        min-height: 100vh;
        background: var(--main-content-bg);
        color: var(--main-text);
    }

    /* BUTTONS */
    .btn-primary {
        background-color: var(--primary-btn);
        border-color: var(--primary-btn);
    }

    .btn-success {
        background-color: var(--success-btn);
        border-color: var(--success-btn);
    }

    .btn-outline-primary:hover {
        background-color: var(--primary-btn);
        color: white;
    }

    /* TABLE */
    .table thead {
        background-color: #def8f6;
    }

    .table th {
        color: #00404a;
        font-weight: 600;
    }

    .alert-info {
        background-color: #dffcf8;
        border-color: #b3ece7;
        color: #007c85;
    }
</style>

<!-- THANH BÊN (SIDEBAR) -->
<div class="sidebar">
    <h5>ADMIN</h5>
    <ul class="nav flex-column">
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/account">Quản lý tài khoản</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/rooms?action=list">Quản lý phòng</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/blocks?action=list">Quản lý khu</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/bill?action=list">Quản lý hóa đơn</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/utility?action=list">Quản lý tiện ích</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/usage">Quản lý sử dụng</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/notification?action=viewNotifications">Quản lý thông báo</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/Contracts">Quản lý hợp đồng</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/adminReport">Báo cáo</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/statistical">Thống kê</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/Logouts">Đăng xuất</a></li>
    </ul>
</div>
