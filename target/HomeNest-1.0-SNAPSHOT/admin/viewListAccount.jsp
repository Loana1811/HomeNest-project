<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Customer"%>
<%@page import="model.User"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Tài Khoản</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            :root {
                --primary: #3498db;
                --secondary: #2c3e50;
                --success: #28a745;
                --info: #17a2b8;
                --warning: #ffc107;
                --danger: #dc3545;
                --light: #f8f9fa;
                --dark: #343a40;
                --gradient-primary: linear-gradient(135deg, #007bff, #0056b3);
                --gradient-success: linear-gradient(135deg, #28a745, #218838);
                --gradient-warning: linear-gradient(135deg, #ffc107, #e0a800);
                --gradient-info: linear-gradient(135deg, #17a2b8, #117a8b);
            }

            body {
                background-color: #f5f7fa;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                margin: 0;
            }

            .container {
                max-width: 1200px;
                margin: 2rem auto;
                padding: 20px;
                background-color: white;
                border-radius: 14px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            }

            .header {
                background: var(--gradient-primary);
                color: white;
                padding: 2rem;
                margin-bottom: 2rem;
                border-radius: 0 0 20px 20px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            }

            .header h1 {
                margin: 0;
                font-size: 2rem;
                font-weight: 600;
            }

            .dashboard-stats {
                display: flex;
                gap: 32px;
                justify-content: flex-start;
                margin-bottom: 32px;
                flex-wrap: wrap;
            }

            .stat-card {
                background: white;
                border-radius: 18px;
                box-shadow: 0 2px 24px rgba(169, 184, 200, 0.13);
                min-width: 210px;
                padding: 28px 24px 20px 24px;
                display: flex;
                flex-direction: column;
                align-items: flex-start;
                transition: transform 0.2s, box-shadow 0.15s;
            }

            .stat-card:hover {
                transform: translateY(-5px) scale(1.03);
                box-shadow: 0 8px 32px rgba(41, 150, 218, 0.1), 0 2px 12px rgba(180, 198, 247, 0.2);
            }

            .stat-icon {
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 12px;
                width: 52px;
                height: 52px;
                font-size: 2rem;
                margin-bottom: 18px;
            }

            .stat-blue {
                background: #e1f1fb;
                color: #2596e0;
            }

            .stat-green {
                background: #e2f9ea;
                color: #1fb865;
            }

            .stat-grey {
                background: #e7e9ed;
                color: #75797d;
            }

            .stat-number {
                font-size: 2rem;
                font-weight: 700;
                color: #222c23;
                margin-bottom: 3px;
                margin-left: 4px;
            }

            .stat-label {
                font-size: 1.08rem;
                color: #868e96;
                font-weight: 500;
                margin-left: 4px;
                letter-spacing: 0.2px;
            }

            .create-buttons {
                text-align: center;
                margin: 20px 0;
            }

            .btn-create {
                background: var(--gradient-success);
                color: white;
                padding: 10px 20px;
                margin: 0 10px;
                border-radius: 8px;
                font-weight: 500;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                transition: background 0.3s, color 0.3s;
            }

            .btn-create:hover {
                background: linear-gradient(135deg, #218838, #28a745);
                color: #eaf4ff;
            }

            .section-title {
                font-size: 1.5rem;
                font-weight: 600;
                color: var(--dark);
                margin: 1.5rem 0 1rem;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
                background: white;
                border-radius: 8px;
                overflow: hidden;
            }

            th, td {
                border: 1px solid #e0e0e0;
                padding: 12px;
                text-align: left;
            }

            th {
                background: var(--gradient-primary);
                color: white;
                font-weight: 600;
            }

            tr:nth-child(even) {
                background-color: #f9f9f9;
            }

            tr:hover {
                background-color: rgba(52, 152, 219, 0.1);
            }

            .status-badge {
                padding: 0.4em 0.8em;
                border-radius: 20px;
                font-size: 0.85em;
                font-weight: 500;
                display: inline-block;
            }

            .status-active {
                background-color: rgba(40, 167, 69, 0.15);
                color: var(--success);
            }

            .status-inactive {
                background-color: rgba(220, 53, 69, 0.15);
                color: var(--danger);
            }

            .btn-sm {
                padding: 5px 10px;
                font-size: 0.875rem;
                border-radius: 5px;
            }

            .btn-outline-success {
                border-color: var(--success);
                color: var(--success);
            }

            .btn-outline-success:hover {
                background: var(--gradient-success);
                color: white;
            }

            .btn-info {
                background: var(--gradient-info);
                color: white;
                border: none;
            }

            .btn-info:hover {
                background: linear-gradient(135deg, #117a8b, #17a2b8);
                color: #eaf4ff;
            }

            .btn-outline-warning {
                border-color: var(--warning);
                color: var(--warning);
            }

            .btn-outline-warning:hover {
                background: var(--gradient-warning);
                color: #212529;
            }

            .modal-content {
                border-radius: 10px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            }

            .modal-header {
                background: var(--gradient-primary);
                color: white;
                border-radius: 10px 10px 0 0;
            }

            .modal-footer .btn-warning {
                background: var(--gradient-warning);
                color: #212529;
                border: none;
            }

            .modal-footer .btn-warning:hover {
                background: linear-gradient(135deg, #e0a800, #ffc107);
            }

            .no-data {
                text-align: center;
                padding: 2rem;
                color: #6c757d;
                font-style: italic;
            }

            @media (max-width: 900px) {
                .dashboard-stats {
                    gap: 18px;
                }

                .stat-card {
                    min-width: 140px;
                    padding: 16px 14px 10px 14px;
                }

                .stat-icon {
                    width: 42px;
                    height: 42px;
                    font-size: 1.5rem;
                    margin-bottom: 11px;
                }

                .stat-number {
                    font-size: 1.28rem;
                }

                .container {
                    margin: 1rem;
                    padding: 15px;
                }

                .header {
                    padding: 1.5rem;
                }
            }

            @media (max-width: 768px) {
                .sidebar {
                    display: none;
                }

                main {
                    margin-left: 0;
                }

                table {
                    font-size: 0.9rem;
                }

                th, td {
                    padding: 8px;
                }
            }

        </style>
    </head>
    <body>
        <div class="container">
            <!-- Tiêu đề -->
            <div class="header">
                <h1>Quản Lý Tài Khoản</h1>
            </div>

            <!-- Thống kê bảng điều khiển -->
            <div class="dashboard-stats">
                <div class="stat-card">
                    <div class="stat-icon stat-green"><i class="fas fa-user-tie"></i></div>
                    <div class="stat-number">${totalCustomers}</div>
                    <div class="stat-label">Tổng Khách Hàng</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon stat-blue"><i class="fas fa-user-check"></i></div>
                    <div class="stat-number">${activeCustomers}</div>
                    <div class="stat-label">Hoạt Động</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon stat-grey"><i class="fas fa-user-slash"></i></div>
                    <div class="stat-number">${inactiveCustomers}</div>
                    <div class="stat-label">Không Hoạt Động</div>
                </div>
            </div>

            <!-- Nút Tạo -->
            <div class="create-buttons">
                <a href="${pageContext.request.contextPath}/admin/account?action=createManager" class="btn btn-create text-white">
                    <i class="fas fa-user-plus me-2"></i> Tạo Quản Lý
                </a>
                <a href="${pageContext.request.contextPath}/admin/account?action=createCustomer" class="btn btn-create text-white">
                    <i class="fas fa-user-plus me-2"></i> Tạo Khách Hàng
                </a>
            </div>

            <!-- Bảng Người Dùng -->
            <h2 class="section-title">Danh Sách Người Dùng Hệ Thống</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th><th>Họ và Tên</th><th>Email</th><th>Số Điện Thoại</th>
                        <th>Vai Trò</th><th>Khu</th><th>Trạng Thái</th><th>Ngày Tạo</th><th>Hành Động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty userList}">
                            <c:forEach items="${userList}" var="user">
                                <tr>
                                    <td>${user.userID}</td>
                                    <td>${user.userFullName}</td>
                                    <td>${user.email}</td>
                                    <td>${user.phoneNumber}</td>
                                    <td>${user.role.roleName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.role.roleID == 2}">
                                                ${user.block != null ? user.block.blockName : "N/A"}
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.userStatus eq 'Active'}">
                                                <span class="status-badge status-active">Hoạt Động</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-inactive">Không Hoạt Động</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${user.userCreatedAt}</td>
                                    <td>
                                        <c:if test="${user.role.roleID == 2}">
                                            <a href="${pageContext.request.contextPath}/admin/account?action=editUser&userID=${user.userID}"
                                               class="btn btn-sm btn-outline-success">
                                                <i class="fas fa-pen-to-square"></i>
                                            </a>
                                            <c:if test="${user.userStatus eq 'Active'}">
                                                <button type="button" class="btn btn-sm btn-outline-warning"
                                                        onclick="showDisableUserModal(${user.userID}, '${user.userFullName}')">
                                                    <i class="fas fa-user-xmark"></i>
                                                </button>
                                            </c:if>
                                        </c:if>
                                    </td>

                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="9" class="no-data">Không có dữ liệu người dùng</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- Bảng Khách Hàng -->
            <h2 class="section-title">Danh Sách Khách Hàng</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th><th>Họ và Tên</th><th>Số Điện Thoại</th><th>Email</th>
                        <th>CCCD</th><th>Giới Tính</th><th>Trạng Thái</th><th>Hành Động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty customerList}">
                            <c:forEach items="${customerList}" var="customer">
                                <tr>
                                    <td>${customer.customerID}</td>
                                    <td>${customer.customerFullName}</td>
                                    <td>${customer.phoneNumber}</td>
                                    <td>${customer.email}</td>
                                    <td>${customer.CCCD}</td>
                                    <td>${customer.gender}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${customer.customerStatus eq 'Active'}">
                                                <span class="status-badge status-active">Hoạt Động</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-inactive">Không Hoạt Động</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/account?action=editCustomer&customerID=${customer.customerID}"
                                           class="btn btn-sm btn-outline-success">
                                            <i class="fas fa-pen-to-square"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/account?action=viewDetail&customerID=${customer.customerID}"
                                           class="btn btn-sm btn-info">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <c:if test="${customer.customerStatus eq 'Active'}">
                                            <button type="button" class="btn btn-sm btn-outline-warning"
                                                    onclick="showDisableCustomerModal(${customer.customerID}, '${customer.customerFullName}')">
                                                <i class="fas fa-user-xmark"></i>
                                            </button>
                                        </c:if>


                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="8" class="no-data">Không có dữ liệu khách hàng</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- Modal Vô Hiệu Hóa Người Dùng -->
        <div class="modal fade" id="disableUserModal" tabindex="-1" aria-labelledby="disableUserLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form id="disableUserForm" method="post">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="disableUserLabel">Vô Hiệu Hóa Người Dùng</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="userID" id="disableUserID">
                            <p><b>Người Dùng:</b> <span id="disableUserName"></span></p>
                            <div class="mb-2">
                                <label for="disableUserReason" class="form-label">Lý Do</label>
                                <textarea class="form-control" id="disableUserReason" name="reason" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-warning">Vô Hiệu Hóa</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal Vô Hiệu Hóa Khách Hàng -->
        <div class="modal fade" id="disableCustomerModal" tabindex="-1" aria-labelledby="disableCustomerLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form id="disableCustomerForm" method="post">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="disableCustomerLabel">Vô Hiệu Hóa Khách Hàng</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="customerID" id="disableCustomerID">
                            <p><b>Khách Hàng:</b> <span id="disableCustomerName"></span></p>
                            <div class="mb-2">
                                <label for="disableCustomerReason" class="form-label">Lý Do</label>
                                <textarea class="form-control" id="disableCustomerReason" name="reason" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-warning">Vô Hiệu Hóa</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Toast Container -->
        <div class="toast-container position-fixed bottom-0 end-0 p-3">
            <div id="notificationToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <strong class="me-auto">Thông báo</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                        // Hiển thị/xử lý logic cho modal
                                                        function showDisableUserModal(id, name) {
                                                            document.getElementById('disableUserID').value = id;
                                                            document.getElementById('disableUserName').innerText = name;
                                                            document.getElementById('disableUserForm').action = '${pageContext.request.contextPath}/admin/account?action=disableUser';
                                                            new bootstrap.Modal(document.getElementById('disableUserModal')).show();
                                                        }
                                                        function showDisableCustomerModal(id, name) {
                                                            document.getElementById('disableCustomerID').value = id;
                                                            document.getElementById('disableCustomerName').innerText = name;
                                                            document.getElementById('disableCustomerForm').action = '${pageContext.request.contextPath}/admin/account?action=disableCustomer';
                                                            new bootstrap.Modal(document.getElementById('disableCustomerModal')).show();
                                                        }

                                                        // Hiển thị toast nếu có error hoặc success
                                                        window.addEventListener('load', function () {
                                                            var toastEl = document.getElementById('notificationToast');
                                                            var toastBody = toastEl.querySelector('.toast-body');
                                                            var toastHeader = toastEl.querySelector('.toast-header strong');
                                                            var error = "${param.error}";
                                                            var success = "${param.success}";

                                                            if (error) {
                                                                toastEl.classList.add('text-bg-danger');
                                                                toastHeader.innerText = "Lỗi";
                                                                toastBody.innerText = error;
                                                                var toast = new bootstrap.Toast(toastEl);
                                                                toast.show();
                                                            } else if (success) {
                                                                toastEl.classList.add('text-bg-success');
                                                                toastHeader.innerText = "Thành công";
                                                                toastBody.innerText = success;
                                                                var toast = new bootstrap.Toast(toastEl);
                                                                toast.show();
                                                            }
                                                        });
        </script>
    </body>
  
</html>
