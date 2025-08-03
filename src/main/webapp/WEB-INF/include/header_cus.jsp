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

<style>
    .btn-custom {
        padding: 0.5rem 0.8rem;
        font-size: 1rem;
    }
</style>

<div class="d-flex justify-content-end align-items-center gap-3">
    <% if (customer != null) { %>
    <span class="fw-semibold text-dark">Hello, <%= customer.getCustomerFullName() %></span>

    <div class="dropdown">
        <a class="btn btn-warning rounded-pill text-white position-relative btn-custom dropdown-toggle"
           href="#" role="button"
           id="dropdownNotification"
           data-bs-toggle="dropdown"
           aria-expanded="false">
            <i class="bi bi-bell-fill"></i>
            <% if (unreadCount > 0) { %>
            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                <%= unreadCount %>
            </span>
            <% } %>
        </a>

        <ul class="dropdown-menu dropdown-menu-end p-3 shadow"
            aria-labelledby="dropdownNotification"
            style="width: 360px; max-height: 400px; overflow-y: auto;">
            <h6 class="dropdown-header">Thông báo</h6>
            <% if (notifications != null && !notifications.isEmpty()) { 
         for (Notification n : notifications) { %>
            <li class="mb-2 pb-2 border-bottom">
                <div class="d-flex justify-content-between">
                    <div class="me-2">
                        <div class="fw-bold text-dark"><%= n.getTitle() %></div>
                        <div class="text-muted small"><%= n.getMessage() %></div>
                        <div class="text-muted small"><%= n.getNotificationCreatedAt() %></div>
                    </div>
                    <% if (!n.isRead()) { %>
                    <span class="badge bg-success rounded-pill">Mới</span>

                    <% } %>
                </div>
            </li>
            <% } } else { %>
            <li class="text-muted text-center">Không có thông báo</li>
                <% } %>
            <li><hr class="dropdown-divider"></li>
            <li class="text-center">
                <a href="<%= request.getContextPath() %>/customer/notifications" class="btn btn-sm btn-outline-secondary rounded-pill">Xem tất cả</a>
            </li>
        </ul>
    </div>






    <a href="<%= request.getContextPath() %>/customer/confirm-payment"
       class="btn btn-success rounded-pill text-white btn-custom" title="Confirm Payment">
        <i class="bi bi-cash-stack"></i>
    </a>

    <a href="<%= request.getContextPath() %>/customer/view-profile"
       class="btn rounded-pill text-white btn-custom"
       style="background-color: #363636; border: none;" title="Profile">
        <i class="bi bi-person-circle"></i>
    </a>


    <a href="<%= request.getContextPath() %>/Logouts"
       class="btn btn-danger rounded-pill text-white btn-custom" title="Logout">
        <i class="bi bi-box-arrow-right"></i>
    </a>

    <% } else { %>
    <a href="<%= request.getContextPath() %>/Login"
       class="btn btn-success rounded-pill text-white btn-custom">Login</a>
    <% } %>
</div>
    