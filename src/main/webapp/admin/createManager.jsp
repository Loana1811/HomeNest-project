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
    <title>Create Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
   body {
    background: linear-gradient(to bottom right, #e6efff, #f7faff);
    font-family: 'Segoe UI', Tahoma, sans-serif;
}

.container {
    max-width: 650px;
    margin: 70px auto;
    padding: 40px;
    background: #ffffff;
    border-radius: 20px;
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
    animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(30px); }
    to { opacity: 1; transform: translateY(0); }
}

h2 {
    font-size: 2rem;
    font-weight: 700;
    margin-bottom: 30px;
    color: #1e3b8a;
    text-align: center;
}

label.form-label {
    font-weight: 600;
    margin-bottom: 6px;
    color: #1f2d4d;
}

.form-group {
    margin-bottom: 22px;
}

.form-control,
.form-select {
    border-radius: 30px;
    padding: 12px 20px;
    border: 1px solid #d1d5db;
    font-size: 16px;
    transition: 0.2s ease;
    box-shadow: none;
}

.form-control:focus,
.form-select:focus {
    border-color: #3b82f6;
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.2);
}

.required {
    color: #e11d48;
    font-weight: bold;
}

.btn {
    padding: 12px 28px;
    border-radius: 30px;
    font-weight: 600;
    font-size: 15px;
}

.btn-primary {
    background: linear-gradient(135deg, #1e3b8a, #2563eb);
    color: white;
    border: none;
}

.btn-primary:hover {
    background: linear-gradient(135deg, #153a85, #1e40af);
    transform: translateY(-1px);
}

.btn-secondary {
    background-color: #6c757d;
    color: white;
    border: none;
}

.btn-secondary:hover {
    background-color: #5a6268;
    transform: translateY(-1px);
}

.alert-danger {
    border-radius: 10px;
}

.modal-content {
    border-radius: 12px;
}

.modal-header.bg-danger {
    background-color: #dc3545 !important;
}

@media (max-width: 768px) {
    .container {
        margin: 30px 15px;
        padding: 25px;
    }

    h2 {
        font-size: 1.6rem;
    }
}
</style>
</head>
<body>

<div class="container">
    <h2>Create Manager</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <form id="createManagerForm" action="${pageContext.request.contextPath}/admin/account" method="post">
        <input type="hidden" name="action" value="addManager" />
       <div class="form-group">
            <label class="form-label">Full Name <span class="required">*</span></label>
            <input type="text" name="fullName" class="form-control" required placeholder="Enter full name"
                   value="${not empty param.fullName ? param.fullName : (not empty requestScope.formFullName ? requestScope.formFullName : '')}" />
        </div>
       <div class="form-group">
            <label class="form-label">Email <span class="required">*</span></label>
            <input type="email" name="email" class="form-control" required placeholder="Enter email"
                   value="${not empty param.email ? param.email : (not empty requestScope.formEmail ? requestScope.formEmail : '')}" />
        </div>
       <div class="form-group">
            <label class="form-label">Phone Number <span class="required">*</span></label>
            <input type="tel" name="phoneNumber" class="form-control" required placeholder="Enter phone number"
                   value="${not empty param.phoneNumber ? param.phoneNumber : (not empty requestScope.formPhoneNumber ? requestScope.formPhoneNumber : '')}" />
        </div>
        <div class="form-group">
            <label class="form-label">Assigned Block <span class="required">*</span></label>
            <select id="blockID" name="blockID" class="form-select" required>
                <option value="">Select block</option>
                <c:forEach items="${blockList}" var="block">
                    <option value="${block.blockID}" 
                        <c:if test="${param.blockID == block.blockID or requestScope.formBlockID == block.blockID}">selected</c:if>>
                        ${block.blockName}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label class="form-label">Password <span class="required">*</span></label>
            <input type="password" id="password" name="password" class="form-control" required placeholder="Enter password"
                   value="${not empty requestScope.formPassword ? requestScope.formPassword : ''}" />
        </div>
         <div class="form-group">
            <label class="form-label">Confirm Password <span class="required">*</span></label>
            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required placeholder="Confirm password"
                   value="${not empty requestScope.formConfirmPassword ? requestScope.formConfirmPassword : ''}" />
        </div>
        <button type="submit" class="btn btn-primary">Create Manager</button>
        <a href="${pageContext.request.contextPath}/admin/account" class="btn btn-secondary">Back</a>
    </form>
</div>

<!-- Modal for validation error -->
<div class="modal fade" id="errorModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Error</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
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
    document.getElementById("createManagerForm").addEventListener("submit", function (event) {
        const password = document.getElementById("password");
        const confirmPassword = document.getElementById("confirmPassword");
        const phoneNumber = this.phoneNumber;
        const fullName = this.fullName;
        const email = this.email;
        const blockID = document.getElementById("blockID");

        const validPrefixes = ['032','033','034','035','036','037','038','039','086','081','082','083','084','085','088','070','076','077','078','079','089','052','056','058','059','099'];

        let errorMsg = "";
        if (fullName.value.trim() === "") errorMsg = "Full name is required.";
        else if (!/^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$/.test(email.value.trim())) errorMsg = "Valid email is required.";
        else if (phoneNumber.value.trim().length !== 10 || !validPrefixes.some(p => phoneNumber.value.startsWith(p))) errorMsg = "Phone number is invalid.";
        else if (password.value.length < 8 || !/[A-Z]/.test(password.value) || !/[!@#$%^&*_-]/.test(password.value)) errorMsg = "Password must be strong.";
        else if (password.value !== confirmPassword.value) errorMsg = "Passwords do not match.";
        else if (blockID.value === "") errorMsg = "Block selection is required.";

        if (errorMsg !== "") {
            event.preventDefault();
            document.getElementById("errorModalMessage").innerText = errorMsg;
            new bootstrap.Modal(document.getElementById("errorModal")).show();
        }
    });
</script>
</body>
</html>
