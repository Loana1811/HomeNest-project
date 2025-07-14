<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer" %>
<%@ page import="model.Notification" %>
<%@ page import="java.util.List" %>

<%
    Customer customer = (Customer) session.getAttribute("currentCustomer");
    List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");

    int unreadCount = 0;
    if (notifications != null) {
        for (Notification n : notifications) {
            if (!n.isRead()) {
                unreadCount++;
            }
        }
    }
%>

<div class="d-flex justify-content-end align-items-center gap-3">
    <% if (customer != null) { %>
        <span class="fw-semibold text-dark">Hello, <%= customer.getCustomerFullName() %></span>

        <a href="<%= request.getContextPath() %>/customer/notifications"
           class="btn btn-outline-warning btn-sm position-relative">
            <i class="bi bi-bell-fill"></i>
            <% if (unreadCount > 0) { %>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                    <%= unreadCount %>
                </span>
            <% } %>
        </a>

        <a href="<%= request.getContextPath() %>/customer/view-profile" class="btn btn-outline-primary btn-sm">Profile</a>
        <a href="<%= request.getContextPath() %>/Logouts" class="btn btn-outline-danger btn-sm">Logout</a>
    <% } else { %>
        <a href="<%= request.getContextPath() %>/Login" class="btn btn-outline-success btn-sm">Login</a>
    <% } %>
</div>
