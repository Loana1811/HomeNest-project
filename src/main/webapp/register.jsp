<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Đăng Ký</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f4f4f4;
                font-family: 'Segoe UI', sans-serif;
            }
            .member-wrapper {
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 40px 0;
            }
            .member-content {
                background: #fff;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 500px;
            }
            .title {
                margin-bottom: 20px;
            }
            .form-footer {
                margin-top: 20px;
            }
            .error-message {
                display: none;
                color: red;
                font-size: 0.9em;
            }
        </style>
    </head>
    <body>

        <header class="p-3 bg-light mb-4 border-bottom">
            <div class="container">
                <div class="d-flex flex-wrap align-items-center justify-content-between">
                    <a href="<%= request.getContextPath()%>/SortTour" class="d-flex align-items-center mb-2 mb-lg-0 text-dark text-decoration-none">
                        <img src="<%= request.getContextPath()%>/images/Logo_G3.png" alt="SaigonTourist Logo" width="150">
                    </a>
                </div>
            </div>
        </header>

        <div class="member-wrapper">
            <div class="member-content">
                <h4 class="text-center title">Đăng Ký</h4>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger text-center" role="alert">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success text-center" role="alert">${success}</div>
                </c:if>

                <form id="register-form" method="POST" action="<%= request.getContextPath()%>/Registers">
                    <div class="form-group mb-3">
                        <label for="username">Tên tài khoản</label>
                        <input type="text" class="form-control" name="username" id="username" required placeholder="Nhập tên tài khoản">
                        <div class="error-message" id="username-error">Vui lòng nhập tên tài khoản.</div>
                    </div>

                    <div class="form-group mb-3">
                        <label for="email">Email</label>
                        <input type="email" class="form-control" name="email" id="email" required placeholder="Nhập email">
                        <div class="error-message" id="email-error">Vui lòng nhập email hợp lệ.</div>
                    </div>

                    <div class="form-group mb-3">
                        <label for="full_name">Họ và tên</label>
                        <input type="text" class="form-control" name="full_name" id="full_name" required placeholder="Nhập họ và tên">
                        <div class="error-message" id="full_name-error">Vui lòng nhập họ và tên.</div>
                    </div>

                    <div class="form-group mb-3">
                        <label for="phone_number">Số điện thoại</label>
                        <input type="text" class="form-control" name="phone_number" id="phone_number" required placeholder="Nhập số điện thoại">
                        <div class="error-message" id="phone_number-error">Vui lòng nhập số điện thoại.</div>
                    </div>

                    <div class="form-group mb-3">
                        <label for="password">Mật khẩu</label>
                        <input type="password" class="form-control" name="password" id="password" required placeholder="Nhập mật khẩu">
                        <div class="error-message" id="password-error">Vui lòng nhập mật khẩu.</div>
                    </div>

                    <div class="form-group mb-3">
                        <label for="confirm_password">Xác nhận mật khẩu</label>
                        <input type="password" class="form-control" name="confirm_password" id="confirm_password" required placeholder="Nhập lại mật khẩu">
                        <div class="error-message" id="confirm_password-error">Vui lòng xác nhận mật khẩu.</div>
                    </div>

                    <div class="form-footer">
                        <button type="submit" class="btn btn-primary w-100">Đăng Ký</button>
                        <a href="<%= request.getContextPath()%>/Registers" class="btn btn-danger w-100 mt-2">Hủy</a>
                        <div class="text-center mt-3">
                            Đã có tài khoản? <a href="login" class="btn btn-link">Đăng Nhập</a>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.getElementById('register-form').addEventListener('submit', function (event) {
                let isValid = true;
                const fields = ['username', 'email', 'full_name', 'phone_number', 'password', 'confirm_password'];

                fields.forEach(field => {
                    const input = document.getElementById(field);
                    const error = document.getElementById(field + '-error');
                    if (!input.value.trim()) {
                        isValid = false;
                        error.style.display = 'block';
                    } else {
                        error.style.display = 'none';
                    }
                });

                if (!isValid) {
                    event.preventDefault();
                }
            });
        </script>

    </body>
</html>
