<%-- 
    Document   : room.jsp
    Created on : Jun 11, 2025
    Author     : kloane
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Room Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5">
    <h2 class="mb-4">Room Management</h2>

    <!-- Notifications -->
    <% if (request.getAttribute("message") != null) { %>
        <div class="alert alert-success" role="alert"><%= request.getAttribute("message") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="d-flex justify-content-between mb-3">
        <form action="${pageContext.request.contextPath}/admin/rooms" method="get" class="d-flex">
            <input type="text" name="search" value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>" class="form-control me-2" placeholder="Search by name or type">
            <button type="submit" class="btn btn-outline-primary">Search</button>
        </form>
        <a href="${pageContext.request.contextPath}/admin/room-create.jsp" class="btn btn-success">+ Add Room</a>
    </div>

    <table class="table table-bordered table-hover align-middle">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Price (VND)</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<?> rooms = (List<?>) request.getAttribute("rooms");
            if (rooms != null && !rooms.isEmpty()) {
                for (Object obj : rooms) {
                    Map room = (Map) obj;
        %>
            <tr>
                <td><%= room.get("id") %></td>
                <td><%= room.get("name") %></td>
                <td><%= room.get("type") %></td>
                <td><%= room.get("price") %></td>
                <td>
                    <span class="badge <%= "Available".equals(room.get("status")) ? "bg-success" : "bg-secondary" %>">
                        <%= room.get("status") %>
                    </span>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/rooms/edit?id=<%= room.get("id") %>" class="btn btn-primary btn-sm me-1">Edit</a>
                    <a href="${pageContext.request.contextPath}/admin/rooms/delete?id=<%= room.get("id") %>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this room?');">Delete</a>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr>
                <td colspan="6" class="text-center">No rooms found.</td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>
