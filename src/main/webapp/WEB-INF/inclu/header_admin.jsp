<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Admin Dashboard</title>

  <!-- Bootstrap -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

  <!-- Font Awesome (Icons) -->
  <!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />-->
  <style>
    :root {
      --primary-color: #1e3b8a;
      --secondary-color: #3f5fa6;
      --tertiary-color: #7c94d1;
      --light-color: #c7d2fe;
      --background-color: #f5f9fc;
      --text-color: #111827;
      --sidebar-color: #003459;
      --white: #ffffff;
      --shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      --border-radius: 12px;
    }

    * {
      transition: all 0.25s ease-in-out;
    }

  body {
    margin: 0;
    font-family: "Segoe UI", sans-serif;
    background: var(--background-color);
    color: var(--text-color);
    overflow-y: auto; /* ? Cho phép cu?n ?? hi?n th? toàn trang */
}
    
   .sidebar {
  width: 250px;
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  height: 100vh; /* ??m b?o full chi?u cao viewport */
  overflow-y: auto;
  overflow-x: hidden; /* không cho thanh ngang */
  background: linear-gradient(to bottom, var(--sidebar-color), var(--primary-color), var(--tertiary-color));
  padding: 20px 0;
  box-shadow: var(--shadow);
  z-index: 1000;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}


    .sidebar .nav-link {
      display: flex;
      align-items: center;
      gap: 12px;
      margin: 8px 16px;
      padding: 10px 20px;
      color: var(--light-color);
      font-size: 14.5px;
      font-weight: 500;
      border-radius: var(--border-radius);
      background-color: transparent;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      transition: background 0.3s ease, color 0.3s ease, transform 0.2s ease;
    }

    .sidebar .nav-link:hover {
      background-color: rgba(255, 255, 255, 0.08);
      color: var(--white);
      transform: translateX(3px);
    }

    .sidebar .nav-link.active {
      background-color: var(--light-color);
      color: var(--text-color);
      font-weight: 600;
      border-left: 5px solid var(--primary-color);
      box-shadow: inset 0 0 0 1px rgba(255,255,255,0.2);
    }

    .sidebar .nav-link i {
      width: 18px;
      text-align: center;
    }

    .main-content {
      margin-left: 250px;
      padding: 40px;
      min-height: 100vh;
      background: linear-gradient(to bottom, var(--background-color), var(--light-color));
    }

    .btn-primary {
      background-color: var(--primary-color);
      border-color: var(--primary-color);
    }

    .btn-primary:hover {
      background-color: var(--secondary-color);
      border-color: var(--secondary-color);
    }

    .btn-outline-primary {
      color: var(--primary-color);
      border-color: var(--primary-color);
    }

    .btn-outline-primary:hover {
      background-color: var(--primary-color);
      color: var(--white);
    }

    .table thead {
      background-color: var(--light-color);
    }

    .table th {
      color: var(--primary-color);
    }

    .alert-info {
      background-color: #e0f3ff;
      border-color: #a0d9ec;
      color: var(--primary-color);
    }
   .sidebar .welcome-box {
  text-align: center;
  padding: 20px 0 12px;
  margin-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar .welcome-box .logo-icon {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.sidebar .welcome-box .welcome-text {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
}


.sidebar .nav-link:hover {
  background: linear-gradient(90deg, rgba(255,255,255,0.15), rgba(255,255,255,0.05));
  color: #ffffff;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2), inset 1px 0 0 rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(2px);
}

  </style>
</head>
<body>

  <!-- Sidebar -->
  <div class="sidebar">
       <div class="welcome-box">
  <img src="${pageContext.request.contextPath}/img/logo.jpg" alt="Logo" class="logo-icon"/>

  <div class="welcome-text">Welcome, Admin</div>
</div>


   <ul class="nav flex-column">
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/blocks">Block Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/account">Accounts Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/rooms?action=list">Rooms Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/bill?action=list">Bills Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/utility?action=list">Utilities Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/usage">Usage Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/notification?action=viewNotifications">Notification Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/Contracts">Contracts Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/adminReport">Report Management</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/admin/statistical">Statistical</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<%= ctx %>/Logouts">Logout</a>
  </li>
</ul>

  </div>



</body>
</html>
