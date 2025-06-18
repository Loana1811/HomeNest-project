<%-- 
    Document   : edit_block.jsp
    Created on : Jun 16, 2025
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Block" %>

<%
    Block block = (Block) request.getAttribute("block");
    String msg = (String) request.getAttribute("msg");
    if (block == null) {
%>
<div class="alert alert-danger text-center m-5">⚠️ No block data available to edit.</div>
<%
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Block</title>

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        :root {
            --teal: rgb(0, 128, 128);
        }

        .bg-teal {
            background-color: var(--teal) !important;
        }

        .btn-teal {
            background-color: var(--teal);
            color: white;
            border: none;
        }

        .btn-teal:hover {
            background-color: #006666;
            color: white;
        }
    </style>
</head>

<body class="bg-light">
    <div class="container mt-5">
        <% if (msg != null) { %>
        <div class="alert alert-success text-center"><%= msg %></div>
        <% } %>

        <div class="card shadow">
            <div class="card-header bg-teal text-white">
                <h4 class="mb-0">🏢 Edit Block</h4>
            </div>
            <div class="card-body">
                <form action="blocks" method="post" onsubmit="return confirm('Are you sure you want to update this block?');">
                    <input type="hidden" name="action" value="update" />
                    <input type="hidden" name="id" value="<%= block.getBlockID() %>" />

                    <!-- Block Name -->
                    <div class="mb-3">
                        <label for="blockName" class="form-label">Block Name:</label>
                        <input type="text" class="form-control" id="blockName" name="blockName"
                               value="<%= block.getBlockName() %>" required />
                    </div>

                    <!-- Max Rooms -->
                    <div class="mb-3">
                        <label for="maxRooms" class="form-label">Max Number of Rooms:</label>
                        <input type="number" class="form-control" id="maxRooms" name="maxRooms"
                               min="1" value="<%= block.getMaxRooms() %>" required />
                    </div>

                    <button type="submit" class="btn btn-teal w-100">Update Block</button>
                </form>
            </div>
        </div>
    </div>

    <!-- Optional Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>