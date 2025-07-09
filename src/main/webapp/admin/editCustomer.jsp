<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Customer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"/>
    <style>
        :root {
            --primary: #3498db;
            --secondary: #2c3e50;
            --success: #28a745;
            --danger: #dc3545;
            --warning: #ffc107;
            --info: #17a2b8;
            --light: #f8f9fa;
            --dark: #343a40;
        }

        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .container {
            max-width: 800px;
            margin-top: 30px;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .form-header {
            text-align: center;
            margin-bottom: 40px;
            position: relative;
        }

        .form-header::before {
            content: '';
            position: absolute;
            bottom: -10px;
            left: 50%;
            transform: translateX(-50%);
            width: 80px;
            height: 4px;
            background: linear-gradient(90deg, var(--primary), var(--info));
            border-radius: 2px;
        }

        .form-header h2 {
            color: var(--dark);
            font-weight: 700;
            font-size: 2.5rem;
            margin-bottom: 10px;
            background: linear-gradient(135deg, var(--primary), var(--info));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .form-header p {
            color: #6c757d;
            font-size: 1.1rem;
            margin: 0;
        }

        .form-section {
            background: rgba(248, 249, 250, 0.8);
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 25px;
            border-left: 4px solid var(--primary);
        }

        .section-title {
            color: var(--dark);
            font-weight: 600;
            font-size: 1.3rem;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-floating {
            margin-bottom: 20px;
        }

        .form-floating > .form-control,
        .form-floating > .form-select {
            height: 60px;
            border: 2px solid #e9ecef;
            border-radius: 12px;
            font-size: 16px;
            transition: all 0.3s ease;
        }

        .form-floating > .form-control:focus,
        .form-floating > .form-select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 0.25rem rgba(52, 152, 219, 0.15);
            transform: translateY(-2px);
        }

        .form-floating > label {
            font-weight: 500;
            color: #6c757d;
            padding-left: 1rem;
        }

        .required {
            color: var(--danger);
            font-weight: bold;
        }

        .btn-group-custom {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 30px;
            border-radius: 25px;
            font-weight: 600;
            font-size: 16px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
            border: none;
            position: relative;
            overflow: hidden;
        }

        .btn::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
            transition: left 0.5s;
        }

        .btn:hover::before {
            left: 100%;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--primary), var(--info));
            color: white;
            min-width: 150px;
        }

        .btn-primary:hover {
            background: linear-gradient(135deg, var(--info), var(--primary));
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(52, 152, 219, 0.3);
        }

        .btn-secondary {
            background: linear-gradient(135deg, #6c757d, #495057);
            color: white;
            min-width: 120px;
        }

        .btn-secondary:hover {
            background: linear-gradient(135deg, #495057, #6c757d);
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(108, 117, 125, 0.3);
        }

        .alert {
            border-radius: 12px;
            border: none;
            padding: 15px 20px;
            margin-bottom: 25px;
            font-weight: 500;
        }

        .alert-danger {
            background: linear-gradient(135deg, rgba(220, 53, 69, 0.1), rgba(220, 53, 69, 0.05));
            color: var(--danger);
            border-left: 4px solid var(--danger);
        }

        .status-badge {
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .status-potential {
            background: linear-gradient(135deg, rgba(255, 193, 7, 0.2), rgba(255, 193, 7, 0.1));
            color: #856404;
            border: 1px solid rgba(255, 193, 7, 0.3);
        }

        .status-converted {
            background: linear-gradient(135deg, rgba(40, 167, 69, 0.2), rgba(40, 167, 69, 0.1));
            color: #155724;
            border: 1px solid rgba(40, 167, 69, 0.3);
        }

        @media (max-width: 768px) {
            .container {
                margin: 20px;
                padding: 25px;
            }

            .form-header h2 {
                font-size: 2rem;
            }

            .btn-group-custom {
                flex-direction: column;
                align-items: center;
            }

            .btn {
                width: 100%;
                max-width: 200px;
            }
        }

        .input-icon {
            position: relative;
        }

        .input-icon i {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
            z-index: 5;
        }

        .animate-form {
            animation: slideInUp 0.6s ease-out;
        }

        @keyframes slideInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body>
    <div class="container animate-form">
        <div class="form-header">
            <h2><i class="fas fa-user-edit"></i> Edit Customer</h2>
            <p>Update customer information and details</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                ${error}
            </div>
        </c:if>

        <form id="editCustomerForm" action="${pageContext.request.contextPath}/admin/account" method="post" novalidate>
            <input type="hidden" name="action" value="edit"/>
            <input type="hidden" name="customerID" value="${customer.customerID}"/>
            <input type="hidden" id="oldCustomerPassword" value="${customer.customerPassword}"/>
            <input type="hidden" id="originalStatus" value="${customer.customerStatus}"/>

            <div class="form-section">
                <div class="section-title">
                    <i class="fas fa-user"></i> Basic Information
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="text" id="customerFullName" name="fullName" class="form-control"
                                   placeholder="Full Name" required value="${customer.customerFullName}"/>
                            <label for="customerFullName">Full Name <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="email" id="email" name="email" class="form-control"
                                   placeholder="Email Address" required value="${customer.email}"/>
                            <label for="email">Email Address <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control"
                                   placeholder="Phone Number" required value="${customer.phoneNumber}"/>
                            <label for="phoneNumber">Phone Number <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="text" id="cccd" name="cccd" class="form-control"
                                   placeholder="ID Card Number" value="${customer.CCCD}"/>
                            <label for="cccd">ID Card Number (CCCD)</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="section-title">
                    <i class="fas fa-info-circle"></i> Personal Details
                </div>
                <div class="row">
                    <div class="col-md-4">
                        <div class="form-floating mb-3">
                            <select id="gender" name="gender" class="form-select">
                                <option value="">Select Gender</option>
                                <option value="Male" <c:if test="${customer.gender == 'Male'}">selected</c:if>>Male</option>
                                <option value="Female" <c:if test="${customer.gender == 'Female'}">selected</c:if>>Female</option>
                                <option value="Other" <c:if test="${customer.gender == 'Other'}">selected</c:if>>Other</option>
                            </select>
                            <label for="gender">Gender</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-floating mb-3">
                            <input type="date" id="birthDate" name="birthDate" class="form-control"
                                   placeholder="Birth Date" value="<c:out value='${customer.birthDate}'/>"/>
                            <label for="birthDate">Birth Date</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-floating mb-3">
                            <select id="customerStatus" name="status" class="form-select" required>
                                <option value="">Select Status</option>
                                <option value="Active" <c:if test="${customer.customerStatus == 'Active'}">selected</c:if>>Active</option>
                                <option value="Inactive" <c:if test="${customer.customerStatus == 'Disable'}">selected</c:if>>Inactive</option>
                            </select>
                            <label for="customerStatus">Status <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </div>
                <div class="form-floating mb-3">
                    <textarea id="address" name="address" class="form-control" placeholder="Address"
                              style="height: 100px">${customer.address}</textarea>
                    <label for="address">Address</label>
                    <div class="invalid-feedback"></div>
                </div>
            </div>

            <div class="btn-group-custom">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save me-2"></i> Update Customer
                </button>
                <a href="${pageContext.request.contextPath}/viewListAccount" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-2"></i> Back to List
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
                        <textarea class="form-control" id="statusChangeReason" name="reason" rows="3" required placeholder="Enter reason for status change (min 5 characters)"></textarea>
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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const form = document.getElementById('editCustomerForm');
            const statusSelect = document.getElementById('customerStatus');
            const originalStatus = document.getElementById('originalStatus').value;
            let selectedStatus = statusSelect.value;
            const reasonModal = new bootstrap.Modal(document.getElementById('reasonModal'), {
                backdrop: 'static', // Ngăn đóng khi click ngoài
                keyboard: false     // Ngăn đóng khi nhấn Esc
            });
            const reasonInput = document.getElementById('statusChangeReason');
            const confirmButton = document.getElementById('confirmStatusChange');

            // Debug log để kiểm tra
            console.log('Modal initialized:', reasonModal);
            console.log('Status Select:', statusSelect);
            console.log('Original Status:', originalStatus);
            console.log('Reason Input:', reasonInput);
            console.log('Confirm Button:', confirmButton);

            // Kiểm tra DOM trước khi gắn sự kiện
            if (!statusSelect || !reasonModal || !reasonInput || !confirmButton) {
                console.error('One or more elements not found:', {
                    statusSelect: statusSelect,
                    reasonModal: reasonModal,
                    reasonInput: reasonInput,
                    confirmButton: confirmButton
                });
                return;
            }

            // Hiển thị modal khi thay đổi trạng thái
            statusSelect.addEventListener('change', function (e) {
                selectedStatus = e.target.value;
                console.log('Status changed to:', selectedStatus);
                if (selectedStatus !== originalStatus && selectedStatus !== '') {
                    reasonModal.show();
                    reasonInput.value = ''; // Reset lý do khi mở modal
                    reasonInput.classList.remove('is-invalid');
                }
            });

            // Xử lý khi xác nhận lý do
            confirmButton.addEventListener('click', function () {
                const reason = reasonInput.value.trim();
                console.log('Reason entered:', reason);
                if (reason.length < 5) {
                    reasonInput.classList.add('is-invalid');
                    return;
                }
                reasonInput.classList.remove('is-invalid');
                const existingReasonField = form.querySelector('input[name="reason"]');
                if (existingReasonField) existingReasonField.remove();
                const reasonField = document.createElement('input');
                reasonField.type = 'hidden';
                reasonField.name = 'reason';
                reasonField.value = reason;
                form.appendChild(reasonField);
                reasonModal.hide();
            });

            // Đặt lại khi modal bị ẩn
            document.getElementById('reasonModal').addEventListener('hidden.bs.modal', function () {
                reasonInput.value = '';
                reasonInput.classList.remove('is-invalid');
                if (!form.querySelector('input[name="reason"]') && selectedStatus !== originalStatus) {
                    statusSelect.value = originalStatus;
                    selectedStatus = originalStatus;
                    showError(statusSelect, "Status change requires a valid reason.");
                }
            });

            // Xử lý submit form
            form.addEventListener('submit', function (e) {
                let isValid = true;

                // Full Name
                const fullName = document.getElementById('customerFullName');
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
                if (!/^\d{10}$/.test(phone.value.trim())) {
                    showError(phone, "Phone number must be 10 digits.");
                    isValid = false;
                } else {
                    hideError(phone);
                }

                // CCCD
                const cccd = document.getElementById('cccd');
                if (cccd.value.trim() !== "" && !/^\d{12}$/.test(cccd.value.trim())) {
                    showError(cccd, "CCCD must be 12 digits if provided.");
                    isValid = false;
                } else {
                    hideError(cccd);
                }

                // Birth Date
                const birthDateInput = document.getElementById('birthDate');
                if (birthDateInput.value) {
                    let today = new Date();
                    let dob = new Date(birthDateInput.value);
                    let age = today.getFullYear() - dob.getFullYear();
                    let m = today.getMonth() - dob.getMonth();
                    if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
                        age--;
                    }
                    if (age < 18) {
                        showError(birthDateInput, "Customer must be at least 18 years old.");
                        isValid = false;
                    } else {
                        hideError(birthDateInput);
                    }
                } else {
                    showError(birthDateInput, "Birth date is required.");
                    isValid = false;
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
                if (err) err.innerText = msg;
            }

            function hideError(input) {
                input.classList.remove('is-invalid');
                const err = input.parentElement.querySelector('.invalid-feedback');
                if (err) err.innerText = "";
            }
        });
    </script>
</body>
</html>