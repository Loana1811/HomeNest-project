<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String username = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("username".equals(cookie.getName())) {
                username = cookie.getValue();
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đăng Nhập</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
        <style>
            body {
                background: #f0f2f5;
            }
            .login-wrapper {
                max-width: 420px;
                margin: 100px auto;
                padding: 25px 30px;
                background: #fff;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            }
            .title {
                margin-bottom: 20px;
            }
            .google-btn {
                background-color: #4285F4;
                color: white;
                font-weight: bold;
                border: none;
            }
            .google-btn:hover {
                background-color: #3367D6;
            }
        </style>
    </head>
    <body>

        <div class="login-wrapper">
            <h4 class="text-center title">Đăng Nhập</h4>

            <!-- Hiển thị lỗi nếu có -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger text-center">${error}</div>
            </c:if>

            <!-- Form đăng nhập truyền thống -->
            <form action="Login" method="post">
                <div class="form-group mb-3">
                    <label for="user_name">Email</label>
                    <input type="text" class="form-control" name="_username" id="user_name"
                           value="<%= username%>" placeholder="Nhập email đăng nhập" required>
                </div>
                <div class="form-group mb-3">
                    <label for="login_password">Mật khẩu</label>
                    <input type="password" class="form-control" name="_password" id="login_password"
                           placeholder="Nhập mật khẩu" required>
                </div>
                <div class="form-group form-check mb-3">
                    <input type="checkbox" class="form-check-input" id="remember" name="remember">
                    <label class="form-check-label" for="remember">Ghi nhớ đăng nhập</label>
                </div>
                <div class="d-grid mb-2">
                    <button type="submit" class="btn btn-primary">Đăng Nhập</button>
                </div>
            </form>

            <!-- Nút đăng nhập bằng Google -->
            <div class="text-center mt-3">
                <form action="https://accounts.google.com/o/oauth2/auth" method="get">
                    <input type="hidden" name="scope" value="email profile">
                    <input type="hidden" name="redirect_uri" value="http://localhost:8080/SWP301/login-google">
                    <input type="hidden" name="response_type" value="code">
                    <input type="hidden" name="client_id" value="105183721878-bqd17ufa649vuo2qqogsjiqbuboskpin.apps.googleusercontent.com">
                    <input type="hidden" name="approval_prompt" value="force">
                    <input type="hidden" name="access_type" value="offline">
                    <button type="submit" class="btn google-btn w-100">Đăng nhập bằng Google</button>
                </form>
            </div>

            <div class="d-grid mt-3">
                <a href="SortTour" class="btn btn-secondary">Quay lại Trang chủ</a>
            </div>
            <!-- Nút quên mật khẩu -->
            <div class="text-end mb-3">
                <a href="<%= request.getContextPath() %>/forgot_password" class="text-decoration-none">Quên mật khẩu?</a>
            </div>
            <div class="text-center mt-3">
                Chưa có tài khoản? <a href="<%= request.getContextPath()%>/Registers">Đăng ký</a>
            </div>
        </div>

    </body>
</html>
