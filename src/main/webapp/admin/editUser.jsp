<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Manager</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"/>
        <style>
            body {
                background: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%);
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            .edit-container {
                max-width: 600px;
                margin: 40px auto;
                background: #fff;
                border-radius: 18px;
                box-shadow: 0 8px 32px rgba(102,166,255,0.1);
                padding: 38px 36px 30px 36px;
                position: relative;
                animation: slideIn 0.6s;
            }
            @keyframes slideIn {
                from {
                    opacity:0;
                    transform: translateY(30px);
                }
                to {
                    opacity:1;
                    transform: translateY(0);
                }
            }
            .edit-header {
                text-align: center;
                margin-bottom: 30px;
            }
            .edit-header h2 {
                color: #2471a3;
                font-weight: 800;
                letter-spacing: 1px;
                margin-bottom: 6px;
            }
            .edit-header p {
                color: #5a5a5a;
                font-size: 1.1rem;
            }
            .form-section {
                margin-bottom: 20px;
            }
            .form-floating > label {
                font-weight: 500;
            }
            .required {
                color: #dc3545;
                font-weight: bold;
            }
            .btn-custom {
                min-width: 140px;
                font-weight: 600;
                border-radius: 25px;
                padding: 10px 32px;
                font-size: 1rem;
                text-transform: uppercase;
                letter-spacing: 1px;
            }
            .btn-primary {
                background: linear-gradient(90deg,#2471a3,#66a6ff);
                border: none;
            }
            .btn-primary:hover {
                background: linear-gradient(90deg,#66a6ff,#2471a3);
            }
            .btn-secondary {
                background: #888;
                border: none;
            }
            .alert-info {
                margin-bottom: 18px;
            }
            .role-badge {
                margin-left: 7px;
                font-size: 13px;
                padding: 3px 12px;
                border-radius: 14px;
                background: #e1f5fe;
                color: #1a237e;
                font-weight: 600;
                letter-spacing: 0.5px;
            }
        </style>
    </head>
    <body>
        <div class="edit-container">
            <div class="edit-header">
                <h2>Edit Manager</h2>
                <p>Update manager account information</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            <c:if test="${user.role.roleName eq 'Admin'}">
                <div class="alert alert-info">
                    Admin account cannot be edited. (View only)
                </div>
            </c:if>

            <form 
                id="editUserForm"
                action="${pageContext.request.contextPath}/admin/account" 
                method="post" 
                novalidate
                <c:if test="${user.role.roleName eq 'Admin'}">onsubmit="return false;"</c:if>
                    >
                    <input type="hidden" name="action" value="edit"/>
                    <input type="hidden" name="userID" value="${user.userID}"/>
                <input type="hidden" name="roleID" value="${user.role.roleID}"/>
                <input type="hidden" id="originalStatus" value="${user.userStatus}"/>

                <!-- Full Name -->
                <div class="form-section">
                    <div class="form-floating mb-3">
                        <input type="text" 
                               id="userFullName" 
                               name="fullName" 
                               class="form-control"
                               placeholder="Full Name" 
                               value="${user.userFullName}"
                               <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                                   required />
                               <label for="userFullName">Full Name <span class="required">*</span></label>
                               <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <!-- Email -->
                    <div class="form-section">
                        <div class="form-floating mb-3">
                            <input type="email"
                                   id="email"
                                   name="email"
                                   class="form-control"
                                   placeholder="Email"
                                   value="${user.email}"
                            <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                                required />
                            <label for="email">Email <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <!-- Phone Number -->
                    <div class="form-section">
                        <div class="form-floating mb-3">
                            <input type="text"
                                   id="phoneNumber"
                                   name="phoneNumber"
                                   class="form-control"
                                   placeholder="Phone Number"
                                   value="${user.phoneNumber}"
                            <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                                required />
                            <label for="phoneNumber">Phone Number <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <!-- Role (readonly, just for info) -->
                    <div class="form-section">
                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   id="role"
                                   name="role"
                                   value="${user.role.roleName}" 
                            readonly
                            style="background-color:#eaf4fa;"/>
                        <label for="role">Role</label>
                    </div>
                </div>

                <c:if test="${user.role.roleName eq 'Manager'}">
                    <div class="form-section">
                        <div class="form-floating mb-3">
                            <select id="blockID" name="blockID" class="form-select" required>
                                <option value="">Select Block</option>
                                <c:forEach var="block" items="${blockList}">
                                    <option value="${block.blockID}" 
                                            <c:if test="${user.block != null && user.block.blockID == block.blockID}">selected</c:if>>
                                        ${block.blockName}
                                    </option>
                                </c:forEach>
                            </select>
                            <label for="blockID">Block <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </c:if>

                <!-- Status -->
                <div class="form-section">
                    <div class="form-floating mb-3">
                        <select id="status" name="status" class="form-select" <c:if test="${user.role.roleName eq 'Admin'}">disabled</c:if>>
                            <option value="Active" <c:if test="${user.userStatus eq 'Active'}">selected</c:if>>Active</option>
                            <option value="Disable" <c:if test="${user.userStatus eq 'Disable'}">selected</c:if>>Inactive</option>
                            </select>
                            <label for="status">Status</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="d-flex justify-content-center gap-3">
                        <button type="submit" class="btn btn-primary btn-custom"
                        <c:if test="${user.role.roleName eq 'Admin'}">disabled</c:if>
                            >Save</button>
                        <a href="${pageContext.request.contextPath}/admin/account" class="btn btn-secondary btn-custom">
                        Cancel
                    </a>
                </div>
            </form>
        </div>

        <!-- Reason Modal -->
        <div class="modal fade" id="reasonModal" tabindex="-1" aria-labelledby="reasonModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="reasonModalLabel">Reason for Status Change</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="statusChangeReason" class="form-label">Reason</label>
                            <textarea class="form-control" id="statusChangeReason" name="reason" rows="3" required placeholder="Enter reason for status change"></textarea>
                            <div class="invalid-feedback">Reason is required and must be at least 5 characters.</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" id="confirmStatusChange">Confirm</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script><!-- comment -->


        <script>
                    document.addEventListener("DOMContentLoaded", function () {
                        const form = document.getElementById('editUserForm');
                        const statusSelect = document.getElementById('status');
                        const originalStatus = document.getElementById('originalStatus').value;
                        let selectedStatus = statusSelect.value;
                        const reasonModal = new bootstrap.Modal(document.getElementById('reasonModal'));
                        const reasonInput = document.getElementById('statusChangeReason');
                        const confirmButton = document.getElementById('confirmStatusChange');

                        // Show modal when status changes
                        statusSelect.addEventListener('change', function (e) {
                            selectedStatus = e.target.value;
                            if (selectedStatus !== originalStatus && selectedStatus !== '') {
                                reasonModal.show();
                            }
                        });

                        // Handle confirm status change
                        confirmButton.addEventListener('click', function () {
                            const reason = reasonInput.value.trim();
                            if (reason.length < 5) {
                                reasonInput.classList.add('is-invalid');
                                return;
                            }
                            reasonInput.classList.remove('is-invalid');
                            // Remove any existing reason field to avoid duplicates
                            const existingReasonField = form.querySelector('input[name="reason"]');
                            if (existingReasonField) {
                                existingReasonField.remove();
                            }
                            // Add reason to form as hidden input
                            const reasonField = document.createElement('input');
                            reasonField.type = 'hidden';
                            reasonField.name = 'reason';
                            reasonField.value = reason;
                            form.appendChild(reasonField);
                            reasonModal.hide();
                        });

                        // Reset reason input when modal is hidden, but keep selected status
                        document.getElementById('reasonModal').addEventListener('hidden.bs.modal', function () {
                            reasonInput.value = '';
                            reasonInput.classList.remove('is-invalid');
                            // Only revert status if no reason was confirmed
                            if (!form.querySelector('input[name="reason"]')) {
                                statusSelect.value = originalStatus;
                                selectedStatus = originalStatus;
                            }
                        });

                        form.addEventListener('submit', function (e) {
                            let isValid = true;

                            // Full Name
                            const fullName = document.getElementById('userFullName');
                            if (fullName.value.trim() === "") {
                                showError(fullName, "Full name is required.");
                                isValid = false;
                            } else {
                                hideError(fullName);
                            }

                            // Email
                            const email = document.getElementById('email');
                            if (!/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
                                showError(email, "Email is not valid.");
                                isValid = false;
                            } else {
                                hideError(email);
                            }

                            // Phone number
                            const phone = document.getElementById('phoneNumber');
                            if (!/^\d{10,11}$/.test(phone.value.trim())) {
                                showError(phone, "Phone number must be 10-11 digits.");
                                isValid = false;
                            } else {
                                hideError(phone);
                            }

                            // Block (for Manager role)
                            const blockSelect = document.getElementById('blockID');
                            if (blockSelect && !blockSelect.value) {
                                showError(blockSelect, "Block is required for Manager.");
                                isValid = false;
                            } else if (blockSelect) {
                                hideError(blockSelect);
                            }

                            // Status
                            if (!statusSelect.value) {
                                showError(statusSelect, "Status is required.");
                                isValid = false;
                            } else if (statusSelect.value !== originalStatus && !form.querySelector('input[name="reason"]')) {
                                showError(statusSelect, "Reason is required for status change.");
                                reasonModal.show();
                                isValid = false;
                            } else {
                                hideError(statusSelect);
                            }

                            if (!isValid) {
                                e.preventDefault();
                                e.stopPropagation();
                            }
                        });
                        function showError(input, msg) {
                            input.classList.add('is-invalid');
                            const err = input.parentElement.querySelector('.invalid-feedback');
                            if (err)
                                err.innerText = msg;
                        }

                        function hideError(input) {
                            input.classList.remove('is-invalid');
                            const err = input.parentElement.querySelector('.invalid-feedback');
                            if (err)
                                err.innerText = "";
                        }
                    });
        </script>
    </body>
</html>
