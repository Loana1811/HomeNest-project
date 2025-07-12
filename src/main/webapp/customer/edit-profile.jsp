<%-- 
    Document   : edit-profile
    Created on : Jun 24, 2025, 2:54:58 AM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <div class="card shadow">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0">Edit Profile</h4>
        </div>
        <div class="card-body">
            <form action="update-profile" method="post">
                <input type="hidden" name="customerID" value="<%= customer.getCustomerID() %>">

                <div class="mb-3">
                    <label class="form-label">Full Name</label>
                    <input type="text" name="fullName" class="form-control" value="<%= customer.getCustomerFullName() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Phone</label>
                    <input type="text" name="phone" class="form-control" value="<%= customer.getPhoneNumber() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">CCCD</label>
                    <input type="text" name="cccd" class="form-control" value="<%= customer.getCCCD() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Gender</label>
                    <select name="gender" class="form-select">
                        <option value="Nam" <%= "Nam".equals(customer.getGender()) ? "selected" : "" %>>Nam</option>
                        <option value="Nữ" <%= "Nữ".equals(customer.getGender()) ? "selected" : "" %>>Nữ</option>
                        <option value="Khác" <%= "Khác".equals(customer.getGender()) ? "selected" : "" %>>Khác</option>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Birthday</label>
                    <input type="date" name="birthDay" class="form-control" value="<%= customer.getBirthDay() != null ? sdf.format(customer.getBirthDay()) : "" %>">
                </div>

                <div class="mb-3">
                    <label class="form-label">Address</label>
                    <input type="text" name="address" class="form-control" value="<%= customer.getAddress() %>">
                </div>

                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-control" value="<%= customer.getEmail() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Status</label>
                    <select name="status" class="form-select">
                        <option value="Active" <%= "Active".equals(customer.getCustomerStatus()) ? "selected" : "" %>>Active</option>
                        <option value="Converted" <%= "Converted".equals(customer.getCustomerStatus()) ? "selected" : "" %>>Converted</option>
                        <option value="Inactive" <%= "Inactive".equals(customer.getCustomerStatus()) ? "selected" : "" %>>Inactive</option>
                    </select>
                </div>

                <div class="d-flex justify-content-end">
                    <a href="profile.jsp" class="btn btn-secondary me-2">Cancel</a>
                    <button type="submit" class="btn btn-success">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
