<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Customer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f5f7fa;
            font-family: 'Segoe UI', sans-serif;
        }

        .container {
            max-width: 620px;
            margin: 40px auto;
            background: white;
            padding: 35px;
            border-radius: 16px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }

        h2 {
            text-align: center;
            color: #1e3b8a;
            margin-bottom: 30px;
            font-weight: 700;
        }

        .form-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 6px;
        }

        .form-control {
            border-radius: 999px;
            border: 1px solid #cbd5e1;
            padding: 12px 20px;
            font-size: 15px;
            transition: border-color 0.3s, box-shadow 0.3s;
            background-color: #fff;
        }

        .form-control:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.2);
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-select {
            border-radius: 999px;
            padding: 12px 20px;
        }

        .required {
            color: #e11d48;
        }

        .btn {
            border-radius: 999px;
            padding: 12px 25px;
            font-weight: 600;
            font-size: 15px;
        }

        .btn-primary {
            background-color: #1e3b8a;
            color: white;
            border: none;
        }

        .btn-primary:hover {
            background-color: #163273;
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
            margin-left: 10px;
        }

        .btn-secondary:hover {
            background-color: #555e64;
        }

        .alert {
            font-size: 14px;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Create Customer</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">${error}</div>
    </c:if>

    <form id="createCustomerForm" action="${ctx}/admin/account" method="post">
        <input type="hidden" name="action" value="addCustomer" />

        <div class="form-group">
            <label class="form-label">Full Name <span class="required">*</span></label>
            <input type="text" name="fullName" class="form-control" required
                   value="${not empty param.fullName ? param.fullName : (not empty requestScope.formFullName ? requestScope.formFullName : '')}" />
        </div>

        <div class="form-group">
            <label class="form-label">Email <span class="required">*</span></label>
            <input type="email" name="email" class="form-control" required
                   value="${not empty param.email ? param.email : (not empty requestScope.formEmail ? requestScope.formEmail : '')}" />
        </div>

        <div class="form-group">
            <label class="form-label">Phone Number <span class="required">*</span></label>
            <input type="tel" name="phoneNumber" class="form-control" required
                   value="${not empty param.phoneNumber ? param.phoneNumber : (not empty requestScope.formPhoneNumber ? requestScope.formPhoneNumber : '')}" />
        </div>

        <div class="form-group">
            <label class="form-label">National ID</label>
            <input type="text" name="cccd" class="form-control"
                   value="${not empty param.cccd ? param.cccd : (not empty requestScope.formCCCD ? requestScope.formCCCD : '')}" />
        </div>

        <div class="form-group">
            <label class="form-label">Gender</label>
            <select name="gender" class="form-select">
                <option value="">Select gender</option>
                <option value="Nam" ${param.gender == 'Nam' ? 'selected' : (requestScope.formGender == 'Nam' ? 'selected' : '')}>Male</option>
                <option value="Nữ" ${param.gender == 'Nữ' ? 'selected' : (requestScope.formGender == 'Nữ' ? 'selected' : '')}>Female</option>
                <option value="Khác" ${param.gender == 'Khác' ? 'selected' : (requestScope.formGender == 'Khác' ? 'selected' : '')}>Other</option>
            </select>
        </div>

        <div class="form-group">
            <label class="form-label">Date of Birth <span class="required">*</span></label>
            <input type="date" name="birthDate" id="birthDate" class="form-control" required
                   value="${not empty param.birthDate ? param.birthDate : (not empty requestScope.formBirthDate ? requestScope.formBirthDate : '')}" />
        </div>

        <div class="form-group">
            <label class="form-label">Address</label>
            <input type="text" name="address" class="form-control"
                   value="${not empty param.address ? param.address : (not empty requestScope.formAddress ? requestScope.formAddress : '')}" />
        </div>

        <div class="form-group">
            <label class="form-label">Password <span class="required">*</span></label>
            <input type="password" name="password" id="password" class="form-control" required
                   value="${not empty requestScope.formPassword ? requestScope.formPassword : ''}" />
        </div>

        <div class="form-group">
            <label class="form-label">Confirm Password <span class="required">*</span></label>
            <input type="password" name="confirmPassword" id="confirmPassword" class="form-control" required
                   value="${not empty requestScope.formConfirmPassword ? requestScope.formConfirmPassword : ''}" />
        </div>

        <div class="d-flex justify-content-center mt-3">
            <button type="submit" class="btn btn-primary">Create</button>
            <a href="<%= request.getContextPath() %>/admin/account" class="btn btn-secondary">Back</a>
        </div>
    </form>
</div>


<!-- Error Modal -->
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
        let cccd = this.cccd.value.trim();

        const validPrefixes = [
            '032', '033', '034', '035', '036', '037', '038', '039', '086',
            '081', '082', '083', '084', '085', '088',
            '070', '076', '077', '078', '079', '089',
            '052', '056', '058', '059', '099'
        ];

        let errorMsg = "";
        if (fullName.value.trim() === "") {
            errorMsg = "Full name is required.";
        } else if (email.value.trim() === "" || !/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
            errorMsg = "Valid email is required.";
        } else if (phoneNumber.value.trim() === "" || phoneNumber.value.trim().length !== 10 || 
                   !validPrefixes.some(prefix => phoneNumber.value.trim().startsWith(prefix))) {
            errorMsg = "Phone number must be exactly 10 digits and start with a valid prefix: " + validPrefixes.join(", ") + ".";
        } else if (!birthDate.value) {
            errorMsg = "Date of birth is required.";
        } else {
            let today = new Date();
            let dob = new Date(birthDate.value);
            let age = today.getFullYear() - dob.getFullYear();
            let m = today.getMonth() - dob.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) { age--; }
            if (age < 15) {
                errorMsg = "Customer must be at least 15 years old.";
            } else if (age > 100) {
                errorMsg = "Customer age must not exceed 100.";
            }
        }

        if (cccd !== "") {
            if (!/^\d{12}$/.test(cccd)) {
                errorMsg = "National ID must be exactly 12 digits.";
            } else {
                let firstThree = parseInt(cccd.substring(0, 3));
                if (firstThree < 1 || firstThree > 96) {
                    errorMsg = "Invalid National ID: first three digits must be between 001 and 096.";
                }
            }
        }

        if (password.value.length < 6 || !/[A-Z]/.test(password.value) || !/[!@#$%^&*_-]/.test(password.value)) {
            errorMsg = "Password must be at least 6 characters, include one uppercase letter and one special character (!@#$%^&*_-).";
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
