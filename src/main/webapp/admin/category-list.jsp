<%-- 
    Document   : category-list
    Created on : Jun 13, 2025, 5:39:18 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Category" %>
<html>
<head>
    <title>Category List</title>
    <style>
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f9f9f9;
            padding: 30px;
        }

        h2 {
            color: #2ecc71;
            text-align: center;
            margin-bottom: 30px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.05);
            overflow: hidden;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #e0e0e0;
            text-align: center;
        }

        th {
            background-color: #2ecc71;
            color: white;
        }

        tr:hover {
            background-color: #f0f9f5;
        }

        .no-data {
            text-align: center;
            padding: 20px;
            color: #999;
        }

        .action-buttons {
            display: flex;
            justify-content: center;
            gap: 10px;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 6px;
            font-weight: bold;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.2s ease;
        }

        .btn-edit {
            background-color: #27ae60;
            color: white;
        }

        .btn-edit:hover {
            background-color: #219150;
        }

        .btn-delete {
            background-color: #e74c3c;
            color: white;
        }

        .btn-delete:hover {
            background-color: #c0392b;
        }
    </style>
</head>
<body>

<h2>Category List</h2>

<%
    List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
    int index = 1;
%>

<table>
    <thead>
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
            for (Category c : categoryList) { %>
                <tr>
                    <td><%= index++ %></td>
                    <td><%= c.getCategoriesID() %></td>
                    <td><%= c.getCategoriesName() %></td>
                    <td><%= c.getDescription() %></td>
                    <td>
                        <div class="action-buttons">
                            <!-- Form Edit -->
                            <form action="${pageContext.request.contextPath}/admin/category" method="get" style="display:inline;">
                                <input type="hidden" name="action" value="edit">
                                <input type="hidden" name="id" value="<%= c.getCategoriesID() %>">
                                <button type="submit" class="btn btn-edit">Edit</button>
                            </form>

<!--                             Form Delete 
                            <form action="${pageContext.request.contextPath}/admin/category" method="get" style="display:inline;" onsubmit="return confirm('Are you sure?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<%= c.getCategoriesID() %>">
                                <button type="submit" class="btn btn-delete">Delete</button>
                            </form>-->
                        </div>
                    </td>
                </tr>
        <%  } 
           } else { %>
            <tr>
                <td colspan="5" class="no-data">No categories found.</td>
            </tr>
        <% } %>
    </tbody>
</table>

</body>
</html>
