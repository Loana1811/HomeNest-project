<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Edit Customer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
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
            from { opacity:0; transform: translateY(30px);}
            to { opacity:1; transform: translateY(0);}
        }
        .form-header {
            text-align: center;
            margin-bottom: 30px;
        }
        .form-header h2 {
            color: #2471a3;
            font-weight: 800;
            letter-spacing: 1px;
            margin-bottom: 6px;
        }
        .form-header p {
            color: #5a5a5a;
            font-size: 1.1rem;
        }
        .form-section { margin-bottom: 20px; }
        .section-title {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 14px;
            color: #2471a3;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .form-floating > label { font-weight: 500; }
        .required { color: #dc3545; font-weight: bold; }
        .btn-custom {
            min-width: 140px; font-weight: 600;
            border-radius: 25px; padding: 10px 32px; font-size: 1rem;
            text-transform: uppercase; letter-spacing: 1px;
        }
        .btn-primary {
            background: linear-gradient(90deg,#2471a3,#66a6ff); border: none;
        }
        .btn-primary:hover { background: linear-gradient(90deg,#66a6ff,#2471a3);}
        .btn-secondary { background: #888; border: none;}
        .alert-info { margin-bottom: 18px; }
        .status-question {
            font-size: 0.97rem;
            color: #2471a3;
            margin-top: 4px;
            margin-bottom: 0;
            font-style: italic;
            display: flex;
            align-items: center;
            gap: 4px;
        }
        .status-question i {
            font-size: 1.1em;
            color: #ffc107;
        }
        .btn-group-custom {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 18px;
        }
    </style>
</head>
<body>
   <div class="edit-container">
     <div class="form-header">
<h2><i class="fas fa-user-edit"></i> Edit Customer</h2>
<p>Update Customer Information and Details</p>
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
                    <i class="fas fa-user"></i> Basic info
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="text" id="customerFullName" name="fullName" class="form-control"
                                   placeholder="Họ tên đầy đủ" required 
                                   value="${not empty param.fullName ? param.fullName : (not empty requestScope.formFullName ? requestScope.formFullName : customer.customerFullName)}"/>
                            <label for="customerFullName">Full Name <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="email" id="email" name="email" class="form-control"
                                   placeholder="Địa chỉ Email" required 
                                   value="${not empty param.email ? param.email : (not empty requestScope.formEmail ? requestScope.formEmail : customer.email)}"/>
                            <label for="email">Email <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control"
                                   placeholder="Số điện thoại" required 
                                   value="${not empty param.phoneNumber ? param.phoneNumber : (not empty requestScope.formPhoneNumber ? requestScope.formPhoneNumber : customer.phoneNumber)}"/>
                            <label for="phoneNumber">Phone <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-floating mb-3">
                            <input type="text" id="cccd" name="cccd" class="form-control"
                                   placeholder="Số CCCD" 
                                   value="${not empty param.cccd ? param.cccd : (not empty requestScope.formCCCD ? requestScope.formCCCD : customer.CCCD)}"/>
                            <label for="cccd">CCCD</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="section-title">
                    <i class="fas fa-info-circle"></i> Personal information
                </div>
                <div class="row">
                    <div class="col-md-4">
                        <div class="form-floating mb-3">
                            <select id="gender" name="gender" class="form-select">
                                <option value="">Select Gender</option>
                                <option value="Male" <c:if test="${not empty param.gender and param.gender == 'Male' or not empty requestScope.formGender and requestScope.formGender == 'Male' or customer.gender == 'Male'}">selected</c:if>>Male</option>
                                <option value="Female" <c:if test="${not empty param.gender and param.gender == 'Female' or not empty requestScope.formGender and requestScope.formGender == 'Female' or customer.gender == 'Female'}">selected</c:if>>Female</option>
                                <option value="Other" <c:if test="${not empty param.gender and param.gender == 'Other' or not empty requestScope.formGender and requestScope.formGender == 'Other' or customer.gender == 'Other'}">selected</c:if>>Other</option>
                            </select>
                            <label for="gender">Gender</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-floating mb-3">
                            <input type="date" id="birthDate" name="birthDate" class="form-control"
                                   placeholder="Ngày sinh" 
                                   value="${not empty param.birthDate ? param.birthDate : (not empty requestScope.formBirthDate ? requestScope.formBirthDate : customer.birthDate)}"/>
                            <label for="birthDate">BirthDate</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-floating mb-3">
                            <select id="customerStatus" name="status" class="form-select" required>
                                <option value="">Select Status</option>
                                <option value="Active" <c:if test="${not empty param.status and param.status == 'Active' or not empty requestScope.formStatus and requestScope.formStatus == 'Active' or customer.customerStatus == 'Active'}">selected</c:if>>Active</option>
                                <option value="Disable" <c:if test="${not empty param.status and param.status == 'Disable' or not empty requestScope.formStatus and requestScope.formStatus == 'Disable' or customer.customerStatus == 'Disable'}">selected</c:if>>Inactive</option>
                            </select>
                            <label for="customerStatus">Status <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                        <div class="status-question">
                            <i class="fas fa-question-circle"></i>
                           Change status? You need to enter a reason when changing status.
                        </div>
                    </div>
                </div>
                <div class="form-floating mb-3">
                    <textarea id="address" name="address" class="form-control" placeholder="Địa chỉ"
                              style="height: 100px">${not empty param.address ? param.address : (not empty requestScope.formAddress ? requestScope.formAddress : customer.address)}</textarea>
                    <label for="address">Address</label>
                    <div class="invalid-feedback"></div>
                </div>
            </div>

            <div class="btn-group-custom">
                <button type="submit" class="btn btn-primary btn-custom">
                    <i class="fas fa-save me-2"></i> Edit customer
                </button>
                <a href="${pageContext.request.contextPath}/admin/account" class="btn btn-secondary btn-custom">
                    <i class="fas fa-arrow-left me-2"></i> Back to the list
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
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="statusChangeReason" class="form-label">Reason</label>
                        <textarea class="form-control" id="statusChangeReason" name="reason" rows="3" required placeholder="Nhập lý do thay đổi trạng thái (tối thiểu 5 ký tự)"></textarea>
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

    <!-- Error Modal -->
    <div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="errorModalLabel">Error</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
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
        document.addEventListener("DOMContentLoaded", function () {
            const form = document.getElementById("editCustomerForm");
            const fullName = document.getElementById("customerFullName");
            const email = document.getElementById("email");
            const phone = document.getElementById("phoneNumber");
            const cccdInput = document.getElementById("cccd");
            const birthDateInput = document.getElementById("birthDate");
            const statusSelect = document.getElementById("customerStatus");
            const originalStatus = document.getElementById("originalStatus").value;
            const reasonModal = new bootstrap.Modal(document.getElementById('reasonModal'));
            const reasonInput = document.getElementById("statusChangeReason");
            const confirmButton = document.getElementById("confirmStatusChange");
            const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
            let selectedStatus = statusSelect.value;

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

            // Hiển thị modal khi trạng thái thay đổi
            statusSelect.addEventListener('change', function (e) {
                selectedStatus = e.target.value;
                if (selectedStatus !== originalStatus && selectedStatus !== '') {
                    reasonModal.show();
                }
            });

            // Xử lý xác nhận thay đổi trạng thái
            confirmButton.addEventListener('click', function () {
                const reason = reasonInput.value.trim();
                if (reason.length < 5) {
                    reasonInput.classList.add('is-invalid');
                    return;
                }
                reasonInput.classList.remove('is-invalid');
                // Xóa trường lý do cũ để tránh trùng lặp
                const existingReasonField = form.querySelector('input[name="reason"]');
                if (existingReasonField) {
                    existingReasonField.remove();
                }
                // Thêm lý do vào form dưới dạng input ẩn
                const reasonField = document.createElement('input');
                reasonField.type = 'hidden';
                reasonField.name = 'reason';
                reasonField.value = reason;
                form.appendChild(reasonField);
                reasonModal.hide();
            });

            // Đặt lại input lý do khi modal bị ẩn, nhưng giữ trạng thái đã chọn
            document.getElementById('reasonModal').addEventListener('hidden.bs.modal', function () {
                reasonInput.value = '';
                reasonInput.classList.remove('is-invalid');
                // Chỉ quay lại trạng thái cũ nếu không có lý do được xác nhận
                if (!form.querySelector('input[name="reason"]')) {
                    statusSelect.value = originalStatus;
                    selectedStatus = originalStatus;
                }
            });

            form.addEventListener('submit', function (e) {
                let errorMsg = "";

                // Họ tên
                if (fullName.value.trim() === "") {
                    errorMsg += "Họ và tên là bắt buộc.\n";
                    showError(fullName, "Vui lòng nhập họ tên đầy đủ.");
                } else {
                    hideError(fullName);
                }

                // Email
                if (!/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
                    errorMsg += "Email không hợp lệ.\n";
                    showError(email, "Email không hợp lệ.");
                } else {
                    hideError(email);
                }

                // Số điện thoại
                if (!/^\d{10}$/.test(phone.value.trim())) {
                    errorMsg += "Số điện thoại phải gồm 10 chữ số.\n";
                    showError(phone, "Số điện thoại phải gồm 10 chữ số.");
                } else {
                    hideError(phone);
                }

                // CCCD
                const cccd = cccdInput.value.trim();
                if (cccd !== "") {
                    if (!/^\d{12}$/.test(cccd)) {
                        errorMsg += "CCCD phải đủ 12 số.\n";
                        showError(cccdInput, "CCCD phải đủ 12 số.");
                    } else {
                        let firstThree = parseInt(cccd.substring(0, 3));
                        if (firstThree < 1 || firstThree > 96) {
                            errorMsg += "Ba số đầu CCCD phải nằm trong khoảng 001 - 096.\n";
                            showError(cccdInput, "Ba số đầu CCCD phải nằm trong khoảng 001 - 096.");
                        } else {
                            hideError(cccdInput);
                        }
                    }
                } else {
                    hideError(cccdInput);
                }

                // Ngày sinh
                if (birthDateInput.value) {
                    let today = new Date();
                    let dob = new Date(birthDateInput.value);
                    let age = today.getFullYear() - dob.getFullYear();
                    let m = today.getMonth() - dob.getMonth();
                    if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
                        age--;
                    }
                    if (age < 15) {
                        errorMsg += "Khách hàng phải từ 15 tuổi trở lên.\n";
                        showError(birthDateInput, "Khách hàng phải từ 15 tuổi trở lên.");
                    } else if (age > 100) {
                        errorMsg += "Khách hàng không được lớn hơn 100 tuổi.\n";
                        showError(birthDateInput, "Khách hàng không được lớn hơn 100 tuổi.");
                    } else {
                        hideError(birthDateInput);
                    }
                } else {
                    errorMsg += "Vui lòng chọn ngày sinh.\n";
                    showError(birthDateInput, "Vui lòng chọn ngày sinh.");
                }

                // Trạng thái
                if (!statusSelect.value) {
                    errorMsg += "Trạng thái là bắt buộc.\n";
                    showError(statusSelect, "Vui lòng chọn trạng thái.");
                } else if (statusSelect.value !== originalStatus && !form.querySelector('input[name="reason"]')) {
                    errorMsg += "Lý do là bắt buộc cho thay đổi trạng thái.\n";
                    reasonModal.show();
                } else {
                    hideError(statusSelect);
                }

                if (errorMsg !== "") {
                    e.preventDefault();
                    document.getElementById("errorModalMessage").innerText = errorMsg;
                    errorModal.show();
                }
            });
        });
    </script>
</body>
</html>