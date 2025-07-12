<%-- 
    Document   : view-profile
    Created on : Jun 23, 2025, 10:37:06 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // Không khai báo lại 'session' vì nó đã tồn tại sẵn trong JSP
    Customer customer = (Customer) session.getAttribute("customer");

    if (customer == null) {
        response.sendRedirect(request.getContextPath() + "/Login.jsp");
        return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Customer Profile</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <style>
            body {
                background-color: #d8f3dc;
                font-family: 'Segoe UI', sans-serif;
            }

            .container-profile {
                max-width: 850px;
                margin: 60px auto;
                background: #fff;
                border-radius: 20px;
                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                overflow: hidden;
            }

            .profile-header {
                background-color: #167c4c;
                padding: 40px;
                color: white;
                text-align: center;
                border-bottom: 5px solid #0f5132;
                border-top-left-radius: 20px;
                border-top-right-radius: 20px;
            }

            .profile-header h2 {
                font-weight: bold;
                font-size: 30px;
                margin-bottom: 5px;
            }

            .profile-header p {
                font-size: 16px;
                opacity: 0.95;
            }

            .profile-body {
                padding: 50px;
            }

            .profile-body .row {
                margin-bottom: 25px;
                padding-bottom: 15px;
                border-bottom: 1px solid #dee2e6;
            }

            .label {
                font-weight: 600;
                font-size: 17px;
                color: #343a40;
            }

            .value {
                font-size: 17px;
                color: #212529;
            }

            .badge-status {
                font-size: 14px;
                padding: 6px 14px;
                border-radius: 12px;
                background-color: #198754;
                color: white;
            }

            .btn-wrapper {
                text-align: center;
                margin-top: 30px;
            }

            .btn-custom {
                padding: 12px 30px;
                font-weight: 600;
                font-size: 16px;
                border-radius: 10px;
            }


        </style>
        <script>
            function toggleEditMode() {
                document.getElementById("viewSection").style.display = 'none';
                document.getElementById("editSection").style.display = 'block';
            }
            function cancelEdit() {
                document.getElementById("editSection").style.display = 'none';
                document.getElementById("viewSection").style.display = 'block';
            }
        </script>
    </head>
    <body>
    <c:if test="${not empty success}">
        <div class="position-fixed top-0 end-0 p-3" style="z-index: 1055;">
            <div class="toast show align-items-center text-white bg-success border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">
                        ${success}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        </div>
    </c:if>
    <div class="container-profile">
        <div class="profile-header">
            <h2>Customer Profile</h2>
            <p>Welcome, <%= customer.getCustomerFullName()%></p>
        </div>

        <div class="profile-body">
            <!-- View Mode -->
            <div id="viewSection">
                <div class="row"><div class="col-md-4 label">Full Name:</div><div class="col-md-8 value"><%= customer.getCustomerFullName()%></div></div>
                <div class="row"><div class="col-md-4 label">Email:</div><div class="col-md-8 value"><%= customer.getEmail()%></div></div>
                <div class="row"><div class="col-md-4 label">Phone:</div><div class="col-md-8 value"><%= customer.getPhoneNumber()%></div></div>
                <div class="row"><div class="col-md-4 label">CCCD:</div><div class="col-md-8 value"><%= customer.getCCCD()%></div></div>
                <div class="row"><div class="col-md-4 label">Gender:</div><div class="col-md-8 value"><%= customer.getGender()%></div></div>
                <div class="row"><div class="col-md-4 label">Birthday:</div><div class="col-md-8 value"><%= customer.getBirthDay() != null ? sdf.format(customer.getBirthDay()) : "N/A"%></div></div>
                <div class="row"><div class="col-md-4 label">Address:</div><div class="col-md-8 value"><%= customer.getAddress()%></div></div>
                <div class="row"><div class="col-md-4 label">Status:</div><div class="col-md-8 value"><span class="badge-status"><%= customer.getCustomerStatus()%></span></div></div>
                <div class="row mt-4">
                    <div class="col text-start">
                        <button class="btn btn-danger btn-custom" onclick="toggleEditMode()">Edit</button>
                    </div>
                    <div class="col text-end">
                        <a href="change-password.jsp" class="btn btn-success btn-custom">Change Password</a>
                    </div>
                </div>


            </div>

            <!-- Edit Mode -->
            <form id="editSection" action="<%= request.getContextPath()%>/update-profile" method="post" style="display:none;">
                <div class="row"><div class="col-md-4 label">Full Name:</div><div class="col-md-8"><input type="text" class="form-control" name="fullName" value="<%= customer.getCustomerFullName()%>" required></div></div>
                <div class="row"><div class="col-md-4 label">Email:</div><div class="col-md-8"><input type="email" class="form-control" name="email" value="<%= customer.getEmail()%>" required></div></div>
                <div class="row"><div class="col-md-4 label">Phone:</div><div class="col-md-8"><input type="text" class="form-control" name="phone" value="<%= customer.getPhoneNumber()%>" required></div></div>
                <div class="row"><div class="col-md-4 label">CCCD:</div><div class="col-md-8"><input type="text" class="form-control" name="cccd" value="<%= customer.getCCCD()%>"></div></div>
                <div class="row"><div class="col-md-4 label">Gender:</div>
                    <div class="col-md-8">
                        <select name="gender" class="form-select">
                            <option value="Nam" <%= "Nam".equals(customer.getGender()) ? "selected" : ""%>>Male</option>
                            <option value="Nữ" <%= "Nữ".equals(customer.getGender()) ? "selected" : ""%>>Female</option>
                            <option value="Khác" <%= "Khác".equals(customer.getGender()) ? "selected" : ""%>>Other</option>
                        </select>
                    </div>
                </div>
                <div class="row"><div class="col-md-4 label">Birthday:</div><div class="col-md-8"><input type="date" class="form-control" name="birthDate" value="<%= customer.getBirthDay() != null ? sdf.format(customer.getBirthDay()) : ""%>"></div></div>
                <div class="row"><div class="col-md-4 label">Address:</div><div class="col-md-8"><input type="text" class="form-control" name="address" value="<%= customer.getAddress()%>"></div></div>

                <div class="btn-wrapper">
                    <button type="submit" class="btn btn-success btn-custom me-2">Save</button>
                    <button type="button" class="btn btn-secondary btn-custom" onclick="cancelEdit()">Cancel</button>
                </div>
            </form>
        </div>
    </div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    window.onload = function () {
        const toastEl = document.querySelector('.toast');
        if (toastEl) {
            const bsToast = new bootstrap.Toast(toastEl, {
                delay: 3000,   // Tự động ẩn sau 3 giây
                autohide: true
            });
            bsToast.show();
        }
    };
</script>

</body>
</html>
