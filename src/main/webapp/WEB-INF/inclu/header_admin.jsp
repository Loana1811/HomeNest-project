
<!-- Bootstrap -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Custom Dark CSS -->
<style>
  body {
    margin: 0;
    font-family: "Segoe UI", sans-serif;
    background-color: #ecfcf9;
    color: #1a1a1a;
}

.navbar {
    background-color:#c8f7ec;
    border-bottom: 1px solid #2c3e50;
}

.navbar-brand,
.nav-link {
    color: #5c7691 important;
}

.nav-link:hover {
    color: #1abc9c !important;
}

/* SIDEBAR */
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
    transition: background 0.3s ease;
}

.sidebar h5 {
    padding-left: 20px;
    margin-bottom: 1rem;
    color: #007c91;
    font-weight: bold;
}

.sidebar .nav-link {
    color: #004c5f; /* Xanh ??m sáng d? nhìn h?n */
    padding: 12px 20px;
    transition: all 0.25s ease;
    font-weight: 500;
    font-size: 15px;
    border-left: 3px solid transparent;
}

.sidebar .nav-link:hover {
    background-color: rgba(255, 255, 255, 0.5);
    color: #0d6efd;
    border-left: 3px solid #0d6efd;
    border-radius: 4px;
}

.sidebar .nav-link.active {
    background-color: #0d6efd;
    color: #ffffff !important;
    font-weight: bold;
    border-left: 3px solid #0a58ca;
    border-radius: 4px;
}



/* MAIN CONTENT */
.main-content {
    margin-left: 240px;
    padding: 70px 30px 30px;
        min-height: 100vh;
        background: linear-gradient(180deg, #b2fff2 0%, #d0f0ff 50%, #e6f9ff 100%);
    color: #1a1a1a;
}


/* BUTTONS */
.btn-primary {
    background-color: #0d6efd;
    border-color: #0d6efd;
}

.btn-success {
    background-color: #28a745;
    border-color: #28a745;
}

.btn-outline-primary:hover {
    background-color: #0d6efd;
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

<!-- NAVBAR -->
<nav class="navbar navbar-expand-lg fixed-top">
    <div class="container-fluid">
        <a class="navbar-brand" href="<%= ctx %>/admin/dashboard"> HomeNest</a>
        <button class="navbar-toggler bg-light" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu" aria-controls="navMenu" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navMenu">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx %>/Logouts">Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- SIDEBAR -->
<div class="sidebar">
    <h5>Admin Menu</h5>
    <ul class="nav flex-column">
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/account"> Accounts Management</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/rooms?action=list"> Rooms Management</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/bill?action=list">Bills Management</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/utility?action=list">Utilities Management</a></li>
        
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/usage"> Usage Management</a></li>
      
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/notification?action=viewNotifications"> Notification Management</a></li>
        <li class="nav-item"><a class="nav-link" href="<%= ctx %>/Contracts">Contracts Management</a></li>
          <li class="nav-item"><a class="nav-link" href="<%= ctx %>/adminReport">Report</a></li>
           <li class="nav-item"><a class="nav-link" href="<%= ctx %>/admin/statistical">Statistical</a></li>
    </ul>
</div>


