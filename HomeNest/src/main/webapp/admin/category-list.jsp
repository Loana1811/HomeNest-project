<%-- 
    Document   : category-list
    Created on : Jun 13, 2025, 5:39:18 PM
    Author     : ADMIN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Category" %>
<%
    List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
    int index = 1;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Category List</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <style>
            :root {
                --teal: rgb(0, 128, 128);
                --light-bg: #f5f7fa;
            }
            body {
                background-color: var(--light-bg);
                font-family: 'Segoe UI', sans-serif;
            }
            .sidebar {
                width: 260px;
                height: 100vh;
                background-color: var(--teal);
                color: white;
                position: fixed;
                top: 0;
                left: 0;
                padding: 2rem 1rem;
            }
            .sidebar h4 {
                font-weight: bold;
                margin-bottom: 2rem;
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
                background-color: #006666;
                color: white;
            }
            table {
                background-color: white;
                border-radius: 12px;
                box-shadow: 0 0 12px rgba(0, 0, 0, 0.05);
                overflow: hidden;
            }
            .sidebar-link {
                display: block;
                padding: 10px 14px;
                color: white;
                text-decoration: none;
                font-weight: 500;
                border-radius: 25px;
                margin: 5px 0;
                transition: background 0.3s;
            }

            .sidebar-link:hover {
                background-color: rgba(255, 255, 255, 0.2);
            }

            .sidebar-link.active {
                background-color: #eaeaea; /* Màu xám nhạt */
                color: #333;               /* Màu chữ xám đậm */
                font-weight: bold;
            }
            /* Header màu pastel xanh ngọc */
            thead.table-header-green th {
                background-color: #007f80; /* xanh pastel nhẹ */
                color: #000;
                font-weight: bold;
                vertical-align: middle;
                border-bottom: 2px solid #b5e7d4;
            }

            /* Bo tròn cho đầu bảng */
            thead.table-header-green th:first-child {
                border-top-left-radius: 10px;
            }
            thead.table-header-green th:last-child {
                border-top-right-radius: 10px;
            }


        </style>
    </head>
    <body>
        <!-- Sidebar -->
        <div class="sidebar">
            <h4>ADMIN</h4>
            <a href="rooms?action=list" class="<%= request.getRequestURI().contains("rooms") ? "sidebar-link active" : "sidebar-link"%>">
                <i class="bi bi-layout-text-window"></i> Manage Rooms
            </a>
            <a href="blocks?action=list" class="<%= request.getRequestURI().contains("blocks") ? "sidebar-link active" : "sidebar-link"%>">
                <i class="bi bi-building"></i> Manage Blocks
            </a>
            <a href="category?action=list" class="<%= request.getRequestURI().contains("category") ? "sidebar-link active" : "sidebar-link"%>">
                <i class="bi bi-tags"></i> Manage Categories
            </a>
        </div>

        <!-- Main Content (đặt bên ngoài sidebar) -->
        <div class="main">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="text-dark">Category List</h2>
                <a href="${pageContext.request.contextPath}/admin/category?action=create" class="btn btn-teal">
                    <i class="bi bi-plus-circle"></i> Add Category
                </a>
                    
                    

            </div>

            <div class="card">
                <div class="card-body">
                    <table class="table table-bordered table-hover">
                        <thead class=" table-header-green">
                            <tr>
                                <th>#</th>
                                <th>Category ID</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (categoryList != null && !categoryList.isEmpty()) {
                                    for (Category c : categoryList) {%>
                            <tr>
                                <td><%= index++%></td>
                                <td><%= c.getCategoriesID()%></td>
                                <td><%= c.getCategoriesName()%></td>
                                <td><%= c.getDescription()%></td>
                                <td>
                                    <div class="d-flex gap-2">
                                        <form action="${pageContext.request.contextPath}/admin/category" method="get" class="d-inline">
                                            <input type="hidden" name="action" value="edit">
                                            <input type="hidden" name="id" value="<%= c.getCategoriesID() %>">
                                            <button type="submit" class="btn btn-teal rounded-pill px-3 py-1 d-flex align-items-center gap-1">
                                                <i class="bi bi-pencil"></i> Edit
                                            </button>
                                        </form>


                                    </div>
                                </td>
                            </tr>
                            <% }
                            } else { %>
                            <tr>
                                <td colspan="5" class="text-center text-muted py-4">No categories found.</td>
                            </tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>

</html>