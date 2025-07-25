<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chỉnh Sửa Quản Lý</title>
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
                <h2>Chỉnh Sửa Quản Lý</h2>
                <p>Cập nhật thông tin tài khoản quản lý</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            <c:if test="${user.role.roleName eq 'Admin'}">
                <div class="alert alert-info">
                    Tài khoản Admin không thể chỉnh sửa. (Chỉ xem)
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

                <!-- Họ và Tên -->
                <div class="form-section">
                    <div class="form-floating mb-3">
                        <input type="text" 
                               id="userFullName" 
                               name="fullName" 
                               class="form-control"
                               placeholder="Họ và Tên" 
                               value="${not empty param.fullName ? param.fullName : (not empty requestScope.formFullName ? requestScope.formFullName : user.userFullName)}"
                               <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                                   required />
                               <label for="userFullName">Họ và Tên <span class="required">*</span></label>
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
                                   value="${not empty param.email ? param.email : (not empty requestScope.formEmail ? requestScope.formEmail : user.email)}"
                            <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                                required />
                            <label for="email">Email <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <!-- Số Điện Thoại -->
                    <div class="form-section">
                        <div class="form-floating mb-3">
                            <input type="text"
                                   id="phoneNumber"
                                   name="phoneNumber"
                                   class="form-control"
                                   placeholder="Số Điện Thoại"
                                   value="${not empty param.phoneNumber ? param.phoneNumber : (not empty requestScope.formPhoneNumber ? requestScope.formPhoneNumber : user.phoneNumber)}"
                            <c:if test="${user.role.roleName eq 'Admin'}">readonly</c:if>
                                required />
                            <label for="phoneNumber">Số Điện Thoại <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                    <!-- Vai Trò (chỉ đọc, chỉ thông tin) -->
                    <div class="form-section">
                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   id="role"
                                   name="role"
                                   value="${user.role.roleName}" 
                            readonly
                            style="background-color:#eaf4fa;"/>
                        <label for="role">Vai Trò</label>
                    </div>
                </div>

                <c:if test="${user.role.roleName eq 'Manager'}">
                    <div class="form-section">
                        <div class="form-floating mb-3">
                            <select id="blockID" name="blockID" class="form-select" required>
                                <option value="">Chọn Khu</option>
                                <c:forEach var="block" items="${blockList}">
                                    <option value="${block.blockID}" 
                                            <c:if test="${param.blockID == block.blockID or requestScope.formBlockID == block.blockID or (user.block != null and user.block.blockID == block.blockID)}">selected</c:if>>
                                        ${block.blockName}
                                    </option>
                                </c:forEach>
                            </select>
                            <label for="blockID">Khu <span class="required">*</span></label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </c:if>

                <!-- Trạng Thái -->
                <div class="form-section">
                    <div class="form-floating mb-3">
                        <select id="status" name="status" class="form-select" <c:if test="${user.role.roleName eq 'Admin'}">disabled</c:if>>
                            <option value="Active" <c:if test="${param.status eq 'Active' or requestScope.formStatus eq 'Active' or user.userStatus eq 'Active'}">selected</c:if>>Hoạt Động</option>
                            <option value="Disable" <c:if test="${param.status eq 'Disable' or requestScope.formStatus eq 'Disable' or user.userStatus eq 'Disable'}">selected</c:if>>Không Hoạt Động</option>
                            </select>
                            <label for="status">Trạng Thái</label>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>

                    <!-- Nút Hành Động -->
                    <div class="d-flex justify-content-center gap-3">
                        <button type="submit" class="btn btn-primary btn-custom"
                        <c:if test="${user.role.roleName eq 'Admin'}">disabled</c:if>
                            >Lưu</button>
                        <a href="${pageContext.request.contextPath}/admin/account" class="btn btn-secondary btn-custom">
                        Hủy
                    </a>
                </div>
            </form>
        </div>

        <!-- Modal Lý Do -->
        <div class="modal fade" id="reasonModal" tabindex="-1" aria-labelledby="reasonModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="reasonModalLabel">Lý Do Thay Đổi Trạng Thái</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="statusChangeReason" class="form-label">Lý Do</label>
                            <textarea class="form-control" id="statusChangeReason" name="reason" rows="3" required placeholder="Nhập lý do thay đổi trạng thái"></textarea>
                            <div class="invalid-feedback">Lý do là bắt buộc và phải có ít nhất 5 ký tự.</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" id="confirmStatusChange">Xác Nhận</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Lỗi -->
        <div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="errorModalLabel">Lỗi</h5>
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
                const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));

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

                    // Họ và Tên
                    const fullName = document.getElementById('userFullName');
                    if (fullName.value.trim() === "") {
                        errorMsg += "Họ và tên là bắt buộc.\n";
                    }

                    // Email
                    const email = document.getElementById('email');
                    if (!/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
                        errorMsg += "Email không hợp lệ.\n";
                    }

                    // Số điện thoại
                    const phone = document.getElementById('phoneNumber');
                    if (!/^\d{10,11}$/.test(phone.value.trim())) {
                        errorMsg += "Số điện thoại phải từ 10-11 chữ số.\n";
                    }

                    // Khu (cho vai trò Quản Lý)
                    const blockSelect = document.getElementById('blockID');
                    if (blockSelect && blockSelect.value === "") {
                        errorMsg += "Khu là bắt buộc cho Quản Lý.\n";
                    }

                    // Trạng thái
                    if (!statusSelect.value) {
                        errorMsg += "Trạng thái là bắt buộc.\n";
                    } else if (statusSelect.value !== originalStatus && !form.querySelector('input[name="reason"]')) {
                        errorMsg += "Lý do là bắt buộc cho thay đổi trạng thái.\n";
                        reasonModal.show();
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