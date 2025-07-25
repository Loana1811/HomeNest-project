<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tạo Quản Lý</title>
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
            <h2>Tạo Quản Lý</h2>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">${error}</div>
            </c:if>
            <form id="createManagerForm" action="${pageContext.request.contextPath}/admin/account" method="post">
                <input type="hidden" name="action" value="addManager" />
                <div class="mb-3">
                    <label class="form-label">Họ và Tên <span class="required">*</span></label>
                    <input type="text" name="fullName" class="form-control" required
                           placeholder="Nhập họ và tên"
                           value="${not empty param.fullName ? param.fullName : (not empty requestScope.formFullName ? requestScope.formFullName : '')}" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Email <span class="required">*</span></label>
                    <input type="email" name="email" class="form-control" required
                           placeholder="Nhập email"
                           value="${not empty param.email ? param.email : (not empty requestScope.formEmail ? requestScope.formEmail : '')}" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Số Điện Thoại <span class="required">*</span></label>
                    <input type="tel" name="phoneNumber" class="form-control" required
                           placeholder="Nhập số điện thoại"
                           value="${not empty param.phoneNumber ? param.phoneNumber : (not empty requestScope.formPhoneNumber ? requestScope.formPhoneNumber : '')}" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Khu Quản Lý <span class="required">*</span></label>
                    <select id="blockID" name="blockID" class="form-select" required>
                        <option value="">Chọn khu</option>
                        <c:forEach items="${blockList}" var="block">
                            <option value="${block.blockID}" 
                                    <c:if test="${param.blockID == block.blockID or requestScope.formBlockID == block.blockID}">selected</c:if>>
                                ${block.blockName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật Khẩu <span class="required">*</span></label>
                    <input type="password" id="password" name="password" class="form-control" required 
                           placeholder="Nhập mật khẩu"
                           value="${not empty requestScope.formPassword ? requestScope.formPassword : ''}" />
                </div>
                <div class="mb-3">
                    <label class="form-label">Xác Nhận Mật Khẩu <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required 
                           placeholder="Xác nhận mật khẩu"
                           value="${not empty requestScope.formConfirmPassword ? requestScope.formConfirmPassword : ''}" />
                </div>
                <button type="submit" class="btn btn-primary">Tạo Quản Lý</button>
                <a href="${pageContext.request.contextPath}/admin/account" class="btn btn-secondary">Quay Lại</a>
            </form>
        </div>
        <!-- Modal Popup (Bootstrap 5) -->
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
        <script>
            document.getElementById("createManagerForm").addEventListener("submit", function (event) {
                let password = document.getElementById("password");
                let confirmPassword = document.getElementById("confirmPassword");
                let phoneNumber = this.phoneNumber;
                let fullName = this.fullName;
                let email = this.email;
                let blockID = document.getElementById("blockID");

                // Danh sách tiền tố hợp lệ
                const validPrefixes = [
                    '032', '033', '034', '035', '036', '037', '038', '039', '086',
                    '081', '082', '083', '084', '085', '088',
                    '070', '076', '077', '078', '079', '089',
                    '052', '056', '058',
                    '059', '099'
                ];

                let errorMsg = "";
                if (fullName.value.trim() === "") {
                    errorMsg = "Họ và tên là bắt buộc.";
                } else if (email.value.trim() === "" || !/^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {
                    errorMsg = "Email hợp lệ là bắt buộc.";
                } else if (phoneNumber.value.trim() === "" || phoneNumber.value.trim().length !== 10 || 
                           !validPrefixes.some(prefix => phoneNumber.value.trim().startsWith(prefix))) {
                    errorMsg = "Số điện thoại phải có đúng 10 chữ số và bắt đầu bằng một trong các tiền tố: " + 
                               validPrefixes.join(", ") + ".";
                } else if (password.value.length < 8 || !/[A-Z]/.test(password.value) || !/[!@#$%^&*_-]/.test(password.value)) {
                    errorMsg = "Mật khẩu phải có ít nhất 8 ký tự, chứa ít nhất một chữ cái in hoa, và một ký tự đặc biệt (!@#$%^&*_-).";
                } else if (password.value !== confirmPassword.value) {
                    errorMsg = "Mật khẩu không khớp.";
                } else if (blockID.value === "") {
                    errorMsg = "Quản lý phải chọn một khu.";
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