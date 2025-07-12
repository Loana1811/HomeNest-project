<%-- 
    Document   : category-create
    Created on : Jun 13, 2025, 4:22:45 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Category" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Add Category</title>
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
            .card {
                max-width: 700px;
                margin: 30px auto;
                border-radius: 12px;
                box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
                border-top: 5px solid var(--teal);
            }
            .card-header {
                background-color: var(--teal);
                color: white;
                font-size: 24px;
                font-weight: bold;
                padding: 1rem 1.5rem;
                text-align: center;
            }
            .btn-teal {
                background-color: var(--teal);
                color: white;
                font-weight: bold;
            }
            .btn-teal:hover {
                background-color: #006666;
                color: white;
            }
            .btn-outline-teal {
                border-color: var(--teal);
                color: var(--teal);
                font-weight: bold;
            }
            .btn-outline-teal:hover {
                background-color: var(--light-bg);
            }
        </style>
    </head>
    <body>
        <!-- Sidebar -->
        <div class="sidebar">
            <h4>ADMIN</h4>
            <a href="rooms?action=list"><i class="bi bi-layout-text-window"></i> Manage Room</a>
            <a href="blocks?action=list"><i class="bi bi-building"></i> Manage Blocks</a>
            <a href="category?action=list"><i class="bi bi-tags"></i> Manage Categories</a>
        </div>

        <!-- Main Content -->
        <div class="main">
            <div class="container">
                <div class="card">
                    <div class="card-header">
                        <i class="bi bi-folder-plus"></i> Add New Category
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/admin/category?action=create">
                            <div class="mb-3">
                                <label for="categoryName" class="form-label">Name <span class="text-danger">*</span></label>
                                <input type="text" id="categoryName" name="categoryName" class="form-control" required>
                            </div>

                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <textarea id="description" name="description" class="form-control" placeholder="Write something..." rows="4"></textarea>
                            </div>

                            <div class="d-flex justify-content-end">
                                <a href="${pageContext.request.contextPath}/admin/category?action=list" class="btn btn-outline-teal me-2">Cancel</a>
                                <button type="submit" class="btn btn-teal">Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>