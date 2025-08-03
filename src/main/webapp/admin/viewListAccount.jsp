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
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Account Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

       <style>
    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap');

    :root {
        --primary: #1d4ed8;
        --success: #28a745;
        --danger: #dc3545;
        --light-bg: #f1f5fb;
        --card-bg: #ffffff;
        --header-bg: #eaf3ff;
        --border: #cddfee;
        --text-dark: #1e3a8a;
        --text-muted: #6b7280;
        --shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
    }

    body {
        font-family: 'Poppins', sans-serif;
        background-color: var(--light-bg);
        color: var(--text-dark);
        margin: 0;
    }

    .container {
        max-width: 1200px;
        margin: 80px auto 30px 260px;
        padding: 30px;
        background: var(--card-bg);
        border-radius: 18px;
        box-shadow: var(--shadow);
        animation: fadeIn 0.5s ease;
    }

    @keyframes fadeIn {
        from { opacity: 0; transform: translateY(20px); }
        to { opacity: 1; transform: translateY(0); }
    }

    .header {
        background-color: var(--header-bg);
        border-radius: 14px;
        padding: 24px;
        text-align: center;
        margin-bottom: 35px;
        box-shadow: var(--shadow);
    }

    .header h1 {
        margin: 0;
        font-size: 2rem;
        font-weight: 600;
        color: var(--primary);
    }

    .dashboard-stats {
        display: flex;
        gap: 20px;
        flex-wrap: wrap;
        margin-bottom: 35px;
    }

    .stat-card {
        background: var(--header-bg);
        border: 1px solid var(--border);
        border-radius: 14px;
        flex: 1;
        min-width: 200px;
        padding: 20px;
        box-shadow: var(--shadow);
        transition: all 0.3s ease;
        cursor: default;
    }

    .stat-card:hover {
        transform: translateY(-6px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
    }

    .stat-icon {
        font-size: 2rem;
        margin-bottom: 12px;
        color: var(--primary);
    }

    .stat-number {
        font-size: 1.9rem;
        font-weight: 600;
    }

    .stat-label {
        color: var(--text-muted);
        font-size: 1rem;
        margin-top: 4px;
        font-weight: 500;
    }

    .create-buttons {
        display: flex;
        justify-content: center;
        gap: 18px;
        margin: 25px 0;
    }

    .btn-create {
        background: linear-gradient(to right, #1d4ed8, #2563eb);
        color: white;
        padding: 10px 24px;
        font-weight: 500;
        border: none;
        border-radius: 10px;
        transition: all 0.3s ease;
    }

    .btn-create:hover {
        background: linear-gradient(to right, #1944c5, #1e3fa0);
        transform: scale(1.03);
    }

    table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
        background: var(--card-bg);
        border-radius: 12px;
        overflow: hidden;
        box-shadow: var(--shadow);
    }

    th, td {
        padding: 14px 16px;
        border: 1px solid var(--border);
        text-align: left;
        font-size: 0.95rem;
    }

    th {
        background-color: var(--header-bg);
        font-weight: 600;
        color: var(--text-dark);
    }

    tr:nth-child(even) {
        background-color: #f8fbff;
    }

    tr:hover {
        background-color: rgba(29, 78, 216, 0.05);
    }

    .status-badge {
        padding: 6px 12px;
        border-radius: 9999px;
        font-size: 0.85rem;
        font-weight: 500;
    }

    .status-active {
        background-color: rgba(40, 167, 69, 0.12);
        color: var(--success);
    }

    .status-inactive {
        background-color: rgba(220, 53, 69, 0.12);
        color: var(--danger);
    }

    .btn-sm {
        padding: 6px 10px;
        font-size: 0.875rem;
        border-radius: 8px;
    }

    .btn-outline-success {
        color: var(--success);
        border: 1px solid var(--success);
        background: none;
    }

    .btn-outline-success:hover {
        background: var(--success);
        color: white;
    }

    .btn-info {
        background-color: var(--primary);
        color: white;
        border: none;
    }

    .btn-info:hover {
        background-color: #1e40af;
    }

    .btn-outline-warning {
        color: #ffc107;
        border: 1px solid #ffc107;
        background: none;
    }

    .btn-outline-warning:hover {
        background-color: #ffc107;
        color: white;
    }

    .section-title {
        margin-top: 45px;
        font-size: 1.5rem;
        font-weight: 600;
        color: var(--primary);
        border-bottom: 2px solid var(--border);
        padding-bottom: 10px;
        margin-bottom: 18px;
    }

    @media (max-width: 768px) {
        .container {
            margin-left: 0;
            margin-top: 60px;
            padding: 20px;
        }

        .dashboard-stats {
            flex-direction: column;
        }

        .stat-card {
            width: 100%;
        }

        .create-buttons {
            flex-direction: column;
        }

        .section-title {
            font-size: 1.3rem;
        }

        th, td {
            font-size: 0.9rem;
        }
    }
 .action-buttons {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    gap: 6px;
}

.action-buttons .btn {
    white-space: nowrap;
    padding: 4px 8px;
}


</style>


    </head>
    <body>
        <div class="container">
            <!-- Header -->
            <div class="header">
                <h1>Account Management</h1>
            </div>

            <!-- Dashboard Stats -->
            <div class="dashboard-stats">
                <div class="stat-card">
                    <div class="stat-icon stat-green"><i class="fas fa-user-tie"></i></div>
                    <div class="stat-number">${totalCustomers}</div>
                    <div class="stat-label">Total Customers</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon stat-blue"><i class="fas fa-user-check"></i></div>
                    <div class="stat-number">${activeCustomers}</div>
                    <div class="stat-label">Active</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon stat-grey"><i class="fas fa-user-slash"></i></div>
                    <div class="stat-number">${inactiveCustomers}</div>
                    <div class="stat-label">Inactive</div>
                </div>
            </div>

            <!-- Create Button -->
            <div class="create-buttons">
                <a href="${pageContext.request.contextPath}/admin/account?action=createManager" class="btn btn-create text-white">
                    <i class="fas fa-user-plus me-2"></i> Create Manager
                </a>
                <a href="${pageContext.request.contextPath}/admin/account?action=createCustomer" class="btn btn-create text-white">
                    <i class="fas fa-user-plus me-2"></i> Create Customer
                </a>
            </div>

            <!-- Users Table -->
            <h2 class="section-title">System Users List</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th><th>Full Name</th><th>Email</th><th>Phone Number</th>
                        <th>Role</th><th>Block</th><th>Status</th><th>Created Date</th><th>Actions</th>
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
                                                <span class="status-badge status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${user.userCreatedAt}</td>
                                    <td>
                                        <c:if test="${user.role.roleID == 2}">
                                            <a href="${pageContext.request.contextPath}/admin/account?action=editUser&userID=${user.userID}"
                                               class="btn btn-sm btn-outline-success">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="9" class="no-data">No user data available</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- Customers Table -->
            <h2 class="section-title">Customers List</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th><th>Full Name</th><th>Phone Number</th><th>Email</th>
                        <th>ID Card</th><th>Gender</th><th>Status</th><th>Actions</th>
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
                                                <span class="status-badge status-active">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-inactive">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
    <div class="action-buttons">
        <a href="${pageContext.request.contextPath}/admin/account?action=editCustomer&customerID=${customer.customerID}"
           class="btn btn-sm btn-outline-success">
            <i class="fas fa-edit"></i>
        </a>
        <a href="${pageContext.request.contextPath}/admin/account?action=viewDetail&customerID=${customer.customerID}"
           class="btn btn-info btn-sm">
            <i class="fas fa-eye"></i>
        </a>
        <button type="button" class="btn btn-sm btn-outline-warning"
                onclick="showDisableCustomerModal(${customer.customerID}, '${customer.customerFullName}')">
            <i class="fas fa-user-slash"></i>
        </button>
    </div>
</td>
    
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="8" class="no-data">No customer data available</td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- Disable User Modal -->
        <div class="modal fade" id="disableUserModal" tabindex="-1" aria-labelledby="disableUserLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form id="disableUserForm" method="post">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="disableUserLabel">Disable User</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="userID" id="disableUserID">
                            <p><b>User:</b> <span id="disableUserName"></span></p>
                            <div class="mb-2">
                                <label for="disableUserReason" class="form-label">Reason</label>
                                <textarea class="form-control" id="disableUserReason" name="reason" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-warning">Disable</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Disable Customer Modal -->
        <div class="modal fade" id="disableCustomerModal" tabindex="-1" aria-labelledby="disableCustomerLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form id="disableCustomerForm" method="post">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="disableCustomerLabel">Disable Customer</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="customerID" id="disableCustomerID">
                            <p><b>Customer:</b> <span id="disableCustomerName"></span></p>
                            <div class="mb-2">
                                <label for="disableCustomerReason" class="form-label">Reason</label>
                                <textarea class="form-control" id="disableCustomerReason" name="reason" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-warning">Disable</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                    // Show/submit logic for modals
                                                    function showDisableUserModal(id, name) {
                                                        document.getElementById('disableUserID').value = id;
                                                        document.getElementById('disableUserName').innerText = name;
                                                        document.getElementById('disableUserForm').action =
                                                                '${pageContext.request.contextPath}/admin/account?action=disableUser';
                                                        new bootstrap.Modal(document.getElementById('disableUserModal')).show();
                                                    }
                                                    function showDisableCustomerModal(id, name) {
                                                        document.getElementById('disableCustomerID').value = id;
                                                        document.getElementById('disableCustomerName').innerText = name;
                                                        document.getElementById('disableCustomerForm').action =
                                                                '${pageContext.request.contextPath}/admin/account?action=disableCustomer';
                                                        new bootstrap.Modal(document.getElementById('disableCustomerModal')).show();
                                                    }

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
    
<div id="notificationToast" class="toast position-fixed bottom-0 end-0 m-4" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="toast-header">
        <strong class="me-auto"></strong>
        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
    <div class="toast-body">
       
    </div>
</div>

    </body>
</html>