<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer" %>
<%@ page import="model.Notification" %>
<%@ page import="java.util.List" %>

<%
    Customer customer = (Customer) session.getAttribute("customer");
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

<!-- Bootstrap CSS + Bootstrap Icons -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<!-- Header -->
<header class="py-2 px-4" "> <%-- Rất nhạt hơn #eaf0f6 --%>
    <div class="container-fluid d-flex justify-content-between align-items-center">
        
       

        <!-- Actions -->
        <div class="d-flex align-items-center gap-3">
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

                <a href="<%= request.getContextPath() %>/customer/view-profile" class="btn btn-outline-primary btn-sm">
                    Profile
                </a>
                <a href="<%= request.getContextPath() %>/Logouts" class="btn btn-outline-danger btn-sm">
                    Logout
                </a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/Login" class="btn btn-outline-success btn-sm">Login</a>
            <% } %>
        </div>
    </div>
</header>
