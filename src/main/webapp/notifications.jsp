<%-- 
    Document   : notifications
    Created on : Jul 8, 2025, 5:33:38 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Notification" %>
<%@ page import="java.util.List" %>
<%
    List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Your Notifications</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="bg-light py-4">

<div class="container">
    <h3 class="mb-4">Your Notifications</h3>

    <% if (notifications == null || notifications.isEmpty()) { %>
        <div class="alert alert-info">You have no notifications.</div>
    <% } else { %>
        <ul class="list-group">
            <% for (Notification n : notifications) { %>
                <li class="list-group-item d-flex justify-content-between align-items-start 
                           <%= n.isIsRead() ? "" : "list-group-item-warning" %>">
                    <div class="ms-2 me-auto">
                        <div class="fw-bold"><%= n.getTitle() %></div>
                        <%= n.getMessage() %><br/>
                        <small class="text-muted">From: <%= n.getSentBy() %> | At: <%= n.getNotificationCreateAt() %></small>
                    </div>
                    <% if (!n.isIsRead()) { %>
                        <span class="badge bg-danger rounded-pill">New</span>
                    <% } %>
                </li>
            <% } %>
        </ul>
    <% } %>
</div>

</body>
</html>

