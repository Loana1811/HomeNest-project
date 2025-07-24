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
                background-color: #f5f7fa;
                font-family: Arial, sans-serif;
            }
            .container {
                max-width: 600px;
                margin-top: 50px;
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            }
            h2 {
                margin-bottom: 20px;
                color: #007bff;
            }
            .required {
                color: #e74c3c;
            }
        </style>
    </head>
    <body>
        
        <div class="container">
            <h2>Create Manager</h2>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">${error}</div>
            </c:if>
            <form id="createManagerForm" action="${pageContext.request.contextPath}/admin/account" method="post">
                <input type="hidden" name="action" value="addManager" />
                <div class="mb-3">
                    <label class="form-label">Full Name <span class="required">*</span></label>
                    <input type="text" name="fullName" class="form-control" required
                           placeholder="Enter full name"
                           value="${not empty param.fullName ? param.fullName : (not empty fullName ? fullName : '')}" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Email <span class="required">*</span></label>
                    <input type="email" name="email" class="form-control" required
                           placeholder="Enter email"
                           value="${not empty param.email ? param.email : (not empty email ? email : '')}" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Phone Number <span class="required">*</span></label>
                    <input type="tel" name="phoneNumber" class="form-control" required
                           placeholder="Enter phone number"
                           value="${not empty param.phoneNumber ? param.phoneNumber : (not empty phoneNumber ? phoneNumber : '')}" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Managed Block <span class="required">*</span></label>
                    <select id="blockID" name="blockID" class="form-select" required>
                        <option value="">Select a block</option>
                        <c:forEach items="${blockList}" var="block">
                            <option value="${block.blockID}" <c:if test="${param.blockID == block.blockID or blockID == block.blockID}">selected</c:if>>
                                ${block.blockName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label">Password <span class="required">*</span></label>
                    <input type="password" id="password" name="password" class="form-control" required placeholder="Enter password" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Confirm Password <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required placeholder="Confirm password" />
                </div>
                <button type="submit" class="btn btn-primary">Create Manager</button>
                <a href="${pageContext.request.contextPath}/admin/account" class="btn btn-secondary">Back</a>
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
            document.getElementById("createManagerForm").addEventListener("submit", function (event) {
                let password = document.getElementById("password");
                let confirmPassword = document.getElementById("confirmPassword");
                let phoneNumber = this.phoneNumber;
                let fullName = this.fullName;
                let email = this.email;
                let blockID = document.getElementById("blockID");

                let errorMsg = "";
                if (fullName.value.trim() === "") {
                    errorMsg = "Full name is required.";
                } else if (email.value.trim() === "" || !/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
                    errorMsg = "Valid email is required.";
                } else if (phoneNumber.value.trim() === "" || !/^\d{10}$/.test(phoneNumber.value.trim())) {
                    errorMsg = "Phone number must be exactly 10 digits.";
                } else if (password.value.length < 8 || !/[A-Z]/.test(password.value) || !/[!@#$%^&*_-]/.test(password.value)) {
                    errorMsg = "Password must be at least 8 characters, contain at least one uppercase letter, and one special character (!@#$%^&*_-).";
                } else if (password.value !== confirmPassword.value) {
                    errorMsg = "Passwords do not match.";
                } else if (blockID.value === "") {
                    errorMsg = "Manager must select a block.";
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