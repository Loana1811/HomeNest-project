<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="model.User" %>
<%@ page import="model.Block" %>
<%
    // Get session attributes
    String userType = (String) session.getAttribute("userType");
    Integer idUser = (Integer) session.getAttribute("idUser");
    Integer idManager = null;
    User manager = null;
    String ctx = request.getContextPath();

    // Get idManager from query parameter
    String idManagerParam = request.getParameter("idManager");
    if (idManagerParam != null && !idManagerParam.isEmpty()) {
        try {
            idManager = Integer.parseInt(idManagerParam);
        } catch (NumberFormatException e) {
            idManager = null;
        }
    }

    // Restrict access to managers only
    if (userType == null || !"User".equals(userType) || idUser == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }

    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null || !"Manager".equals(currentUser.getRole().getRoleName())
            || (idManager != null && !idUser.equals(idManager))) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }

    // Fetch manager data
    if (idManager != null) {
        UserDAO userDAO = new UserDAO();
        try {
            manager = userDAO.getUserById(idManager);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error fetching manager data.");
        }
    }

    if (manager == null) {
        request.setAttribute("error", "Manager not found.");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Manager Dashboard</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
        <style>
            body {
                background-color: #ffffff;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                color: #333;
            }

            .sidebar {
                height: 100vh;
                position: fixed;
                top: 0;
                left: 0;
                background-color: lightblue; /* màu chủ đạo pastel xanh nhạt */
                padding-top: 20px;
                width: 250px;
                box-shadow: 2px 0 6px rgba(0, 0, 0, 0.05);
                border-right: 1px solid #b5d8e0;
            }

            .sidebar h4 {
                color: #003f5f;
                font-weight: bold;
            }

            .nav-link {
                color: #003f5f;
                padding: 12px 20px;
                font-weight: 500;
                border-radius: 10px;
                margin: 5px 15px;
                transition: background-color 0.3s ease;
            }

            .nav-link:hover {
                background-color: rgba(0, 63, 95, 0.1);
                color: #002f4f;
            }

            .nav-link.text-danger {
                color: #b80000 !important;
            }

            .content {
                margin-left: 260px;
                padding: 30px;
                background-color: #ffffff;
                min-height: 100vh;
            }

            .card {
                border-radius: 16px;
                box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
                border: none;
            }

            .card-title {
                color: #0077a6;
                font-size: 1.5rem;
                margin-bottom: 10px;
            }

            .card-text {
                font-size: 1rem;
                color: #444;
            }

            .alert-danger {
                border-radius: 10px;
                padding: 12px 20px;
                font-size: 1rem;
            }

            .btn-primary {
                background-color: #0077a6;
                border-color: #0077a6;
                font-weight: 500;
                border-radius: 10px;
                padding: 10px 24px;
                transition: background-color 0.3s ease;
            }

            .btn-primary:hover {
                background-color: #005f86;
                border-color: #005f86;
            }

            /* Cards màu pastel */
            .bg-pending {
                background-color: #e0f7ff !important; /* xanh nhạt pastel cho pending */
                color: #004c66 !important;
                border: 1px solid #cceaf5;
            }

            .bg-success {
                background-color: #d2f5e3 !important; /* xanh lá pastel cho resolved */
                color: #2f6f5f !important;
                border: 1px solid #bce8d4;
            }

            .bg-warning {
                background-color: #fff9e6 !important; /* vàng pastel cho active tenants */
                color: #7a5900 !important;
                border: 1px solid #f5e7b5;
            }

            .card .card-body {
                padding: 20px;
            }

            .card h5 {
                font-weight: bold;
            }

            .btn {
                font-size: 1rem;
            }
        </style>



    </head>
    <body>
        <div class="sidebar">
            <h4 class="text-center mb-4">Manager Menu</h4>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/dashboard.jsp?idManager=<%= manager.getUserID()%>">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/adminReport">Report Management</a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/notification?idManager=<%= idUser%>">Notification Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx %>/admin/usage"> Usage Management</a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/manager/manage-requests?idManager=<%= idUser%>">Rental Request Management</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-danger" href="<%= ctx%>/Logouts">Logout</a>
                </li>
            </ul>
        </div>

        <div class="content">
            <h2>Manager Dashboard</h2>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            <div class="card mb-4">
                <div class="card-body">
                    <h3 class="card-title">Welcome, <%= manager.getUserFullName()%></h3>
                    <p class="card-text"><strong>Manager ID:</strong> <%= manager.getUserID()%></p>
                    <p class="card-text"><strong>Email:</strong> <%= manager.getEmail() != null ? manager.getEmail() : "Not provided"%></p>
                    <p class="card-text"><strong>Phone:</strong> <%= manager.getPhoneNumber() != null ? manager.getPhoneNumber() : "Not provided"%></p>
                    <p class="card-text"><strong>Status:</strong> <%= manager.getUserStatus()%></p>
                    <p class="card-text"><strong>Assigned Block:</strong> <%= manager.getBlock() != null ? manager.getBlock().getBlockName() : "Not assigned"%></p>
                </div>
            </div>

            <div class="row">
                <div class="col-md-4">
                    <div class="card text-dark bg-pending mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Pending Reports</h5>
                            <p class="card-text">0</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card text-dark bg-resolved mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Resolved Reports</h5>
                            <p class="card-text">0</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card text-dark bg-active mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Active Tenants</h5>
                            <p class="card-text">0</p>
                        </div>
                    </div>
                </div>


                <a href="<%= ctx%>/adminReport" class="btn btn-primary">Go to Report Management</a>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>