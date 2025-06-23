<%-- 
    Document   : category-edit
    Created on : Jun 13, 2025, 6:45:24 PM
    Author     : ADMIN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Category" %>
<%
    Category category = (Category) request.getAttribute("category");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Category</title>
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
                font-weight: bold;
                border-radius: 30px;
            }
            .btn-teal:hover {
                background-color: #006666;
                color: white;
            }
            .card {
                max-width: 600px;
                margin: 0 auto;
                border-radius: 12px;
                box-shadow: 0 0 12px rgba(0, 0, 0, 0.05);
            }
            .card-header {
                background-color: #e0f7f7;
                color: var(--teal);
                font-weight: bold;
                font-size: 20px;
                text-align: center;
                padding: 1rem 1.5rem;
            }
        </style>
    </head>
    <body>
        <div class="sidebar">
            <h4>ADMIN</h4>
            <a href="rooms?action=list"><i class="bi bi-layout-text-window"></i> Manage Rooms</a>
            <a href="blocks?action=list"><i class="bi bi-building"></i> Manage Blocks</a>
            <a href="category?action=list"><i class="bi bi-tags"></i> Manage Categories</a>
        </div>

        <div class="main">
            <div class="card">
                <div class="card-header">
                    <i class="bi bi-pencil-square"></i> Edit Category
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/admin/category" method="post">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="categoryID" value="<%= category.getCategoriesID()%>">

                        <div class="mb-3">
                            <label class="form-label">Name <span class="text-danger">*</span></label>
                            <input type="text" name="categoryName" value="<%= category.getCategoriesName()%>" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea name="description" class="form-control" rows="4"><%= category.getDescription()%></textarea>
                        </div>

                        <div class="d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/admin/category?action=list" class="btn btn-outline-secondary">Cancel</a>
                            <button type="submit" class="btn btn-teal">Update</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>