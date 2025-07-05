<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Customer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f7fa; font-family: Arial, sans-serif; }
        .container { max-width: 600px; margin-top: 50px; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);}
        h2 { margin-bottom: 20px; color: #007bff; }
        .required { color: #e74c3c; }
    </style>
</head>
<body>
<div class="container">
    <h2>Create Customer</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">${error}</div>
    </c:if>
    
        <form id="createCustomerForm" action="${pageContext.request.contextPath}/admin/account" method="post">
    <input type="hidden" name="action" value="addCustomer" />
    <div class="mb-3">
        <label class="form-label">Full Name <span class="required">*</span></label>
        <input type="text" name="fullName" class="form-control" required placeholder="Enter your full name"
               value="${not empty param.fullName ? param.fullName : (not empty fullName ? fullName : '')}" />
    </div>
    <div class="mb-3">
        <label class="form-label">Email <span class="required">*</span></label>
        <input type="email" name="email" class="form-control" required placeholder="Enter your email"
               value="${not empty param.email ? param.email : (not empty email ? email : '')}" />
    </div>
    <div class="mb-3">
        <label class="form-label">Phone Number <span class="required">*</span></label>
        <input type="tel" name="phoneNumber" class="form-control" required placeholder="Enter your phone number"
               value="${not empty param.phoneNumber ? param.phoneNumber : (not empty phoneNumber ? phoneNumber : '')}" />
    </div>
    <div class="mb-3">
        <label class="form-label">CCCD</label>
        <input type="text" name="cccd" class="form-control" placeholder="Enter CCCD (optional)"
               value="${not empty param.cccd ? param.cccd : (not empty cccd ? cccd : '')}" />
    </div>
    <div class="mb-3">
        <label class="form-label">Gender</label>
        <select name="gender" class="form-select">
            <option value="">Select gender</option>
            <option value="Nam" ${param.gender == 'Nam' ? 'selected' : ''}>Nam</option>
            <option value="Nữ" ${param.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
            <option value="Khác" ${param.gender == 'Khác' ? 'selected' : ''}>Khác</option>
        </select>
    </div>
    <div class="mb-3">
        <label class="form-label">Birth Date <span class="required">*</span></label>
        <input type="date" id="birthDate" name="birthDate" class="form-control" required
               value="${not empty param.birthDate ? param.birthDate : (not empty birthDate ? birthDate : '')}" />
    </div>
    <div class="mb-3">
        <label class="form-label">Address</label>
        <input type="text" name="address" class="form-control" placeholder="Enter address (optional)"
               value="${not empty param.address ? param.address : (not empty address ? address : '')}" />
    </div>
    <div class="mb-3">
        <label class="form-label">Password <span class="required">*</span></label>
        <input type="password" id="password" name="password" class="form-control" required placeholder="Enter your password" />
    </div>
    <div class="mb-3">
        <label class="form-label">Confirm Password <span class="required">*</span></label>
        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required placeholder="Confirm your password" />
    </div>
    <button type="submit" class="btn btn-primary">Create Customer</button>
    <a href="${pageContext.request.contextPath}/viewListAccount" class="btn btn-secondary">Back</a>
</form>

</div>
<!-- Modal Popup (Bootstrap 5) -->
<div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="errorModalLabel">Error</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="errorModalMessage"></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-danger" data-bs-dismiss="modal">OK</button>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById("createCustomerForm").addEventListener("submit", function (event) {
        let password = document.getElementById("password");
        let confirmPassword = document.getElementById("confirmPassword");
        let phoneNumber = this.phoneNumber;
        let fullName = this.fullName;
        let email = this.email;
        let birthDate = document.getElementById("birthDate");

        let errorMsg = "";
        if (fullName.value.trim() === "") {
            errorMsg = "Full name is required.";
        } else if (email.value.trim() === "" || !/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
            errorMsg = "Valid email is required.";
        } else if (phoneNumber.value.trim() === "" || !/^\d{10}$/.test(phoneNumber.value.trim())) {
            errorMsg = "Phone number must be exactly 10 digits.";
        } else if (!birthDate.value) {
            errorMsg = "Birth date is required for customer.";
        } else {
            let today = new Date();
            let dob = new Date(birthDate.value);
            let age = today.getFullYear() - dob.getFullYear();
            let m = today.getMonth() - dob.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) { age--; }
            if (age < 15) errorMsg = "Customer must be at least 15 years old.";
        }
        if (password.value.length < 6) {
            errorMsg = "Password must be at least 6 characters.";
        } else if (password.value !== confirmPassword.value) {
            errorMsg = "Passwords do not match.";
        }

        if (errorMsg !== "") {
            event.preventDefault();
            document.getElementById("errorModalMessage").innerText = errorMsg;
            var errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
            errorModal.show();
            return false;
        }
    });
</script>
</body>
</html>

