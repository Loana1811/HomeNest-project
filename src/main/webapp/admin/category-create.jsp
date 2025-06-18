<%-- 
    Document   : category-create
    Created on : Jun 13, 2025, 4:22:45 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Category" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f0f9f5;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .form-container {
            max-width: 600px;
            margin: 60px auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            border-top: 5px solid #2ecc71;
        }

        .form-title {
            font-size: 26px;
            font-weight: bold;
            margin-bottom: 30px;
            color: #27ae60;
            text-align: center;
        }

        .form-group label {
            font-weight: 600;
        }

        .form-control:focus {
            border-color: #2ecc71;
            box-shadow: 0 0 0 0.15rem rgba(46, 204, 113, 0.25);
        }

        .btn-submit {
            background-color: #27ae60;
            color: white;
            font-weight: bold;
        }

        .btn-submit:hover {
            background-color: #219150;
        }

        .btn-cancel {
            border: 2px solid #27ae60;
            color: #27ae60;
            font-weight: bold;
        }

        .btn-cancel:hover {
            background-color: #eafaf1;
        }

        textarea.form-control {
            resize: vertical;
            min-height: 100px;
        }
    </style>
</head>
<body>

    <div class="form-container">
        <div class="form-title">Add New Category</div>

        <form method="post" action="${pageContext.request.contextPath}/admin/category?action=create">
            <div class="form-group">
                <label for="categoryName">Name <span class="text-danger">*</span></label>
                <input type="text" id="categoryName" name="categoryName" class="form-control" required>
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description" class="form-control" placeholder="Write something..."></textarea>
            </div>

            <div class="d-flex justify-content-end gap-2 mt-4">
                <a href="${pageContext.request.contextPath}/admin/category?action=list" class="btn btn-cancel mr-2">Cancel</a>
                <button type="submit" class="btn btn-submit">Submit</button>
            </div>
        </form>
    </div>

</body>
</html>
