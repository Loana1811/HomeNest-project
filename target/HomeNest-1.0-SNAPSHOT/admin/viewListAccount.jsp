<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.Customer"%>
<%@page import="model.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
     :root {
    --primary: #2463eb;
    --secondary: #212a36;
    --success: #19c37d;
    --info: #38bdf8;
    --warning: #fbbf24;
    --danger: #ef4444;
    --light: #f1f5f9;
    --dark: #1e293b;

    --bg-body: #f3f6fb;
    --card-bg: #fff;
    --shadow-card: 0 4px 20px 0 #13316418, 0 1.5px 2px 0 #1a22352e;
}

body {
    background-color: var(--bg-body);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    padding-top: 70px; /* Space for fixed navbar */
}

.header {
    background: linear-gradient(135deg, var(--primary) 60%, var(--secondary) 100%);
    color: white;
    padding: 2.5rem 0 2rem 0;
    margin-bottom: 2.5rem;
    border-radius: 0 0 24px 24px;
    box-shadow: 0 8px 32px 0 #13316416;
    text-shadow: 0 1px 8px #14203444;
    letter-spacing: 1px;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 32px 18px;
    background-color: var(--card-bg);
    border-radius: 14px;
    box-shadow: var(--shadow-card);
}

.create-buttons {
    text-align: center;
    margin: 30px 0 24px 0;
}

.btn {
    padding: 10px 26px;
    margin: 0 8px 7px 8px;
    border-radius: 6px;
    font-weight: 600;
    font-size: 1.09rem;
    transition: background 0.22s, box-shadow 0.15s, transform 0.15s;
    box-shadow: 0 2px 8px #13316412;
}

.btn-create {
    background: linear-gradient(135deg, var(--success), #14b86e 85%);
    color: white;
    border: none;
}
.btn-create:hover, .btn-create:focus {
    background: linear-gradient(135deg, #14b86e 65%, var(--success) 100%);
    color: #fff;
    box-shadow: 0 4px 18px #19c37d26;
    transform: translateY(-2px) scale(1.04);
}

/* Table Styles */
table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
    margin: 25px 0 16px 0;
    background: var(--card-bg);
    box-shadow: 0 1px 4px #d2d8e51f;
    border-radius: 8px;
    overflow: hidden;
}

th, td {
    border-bottom: 1px solid #e5e7eb;
    padding: 14px 14px;
    text-align: left;
}

th {
    background-color: var(--primary);
    color: white;
    font-weight: bold;
    border: none;
    letter-spacing: 0.4px;
}

tr:last-child td {
    border-bottom: none;
}
tr:nth-child(even) {
    background-color: var(--light);
}
tr:hover {
    background-color: #2563eb12;
}

.status-badge {
    display: inline-block;
    padding: 0.36em 1.1em;
    border-radius: 20px;
    font-size: 0.93em;
    font-weight: 500;
    box-shadow: 0 1px 5px #d2d8e526;
}

.status-active {
    background: linear-gradient(90deg, #d9fbe7, #e8fcf0 70%);
    color: var(--success);
    border: 1px solid #19c37d44;
}
.status-inactive {
    background: linear-gradient(90deg, #fde8e9, #fff4f4 70%);
    color: var(--danger);
    border: 1px solid #ef444444;
}
.status-potential {
    background: linear-gradient(90deg, #fff8e1, #fff6d4 70%);
    color: var(--warning);
    border: 1px solid #fbbf2444;
}
.status-converted {
    background: linear-gradient(90deg, #e3f8fd, #e0f7ff 70%);
    color: var(--info);
    border: 1px solid #38bdf844;
}

.empty-state {
    text-align: center;
    padding: 2.8rem;
    color: #7c8189;
    font-size: 1.17em;
}
.empty-state i {
    font-size: 4rem;
    margin-bottom: 0.5rem;
    color: #cfd8e3;
}

.dashboard-stats {
    display: flex;
    gap: 36px;
    justify-content: flex-start;
    margin-bottom: 38px;
    flex-wrap: wrap;
}
.stat-card {
    background: var(--card-bg);
    border-radius: 19px;
    box-shadow: var(--shadow-card);
    min-width: 210px;
    padding: 32px 28px 22px 28px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    transition: transform 0.17s, box-shadow 0.18s;
    border: none;
    margin-bottom: 10px;
}
.stat-card:hover {
    transform: translateY(-5px) scale(1.05);
    box-shadow: 0 8px 38px #2463eb16, 0 2px 12px #b4c6f733;
}

.stat-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    width: 54px;
    height: 54px;
    font-size: 2.15rem;
    margin-bottom: 17px;
}
.stat-blue {
    background: #e6edfa;
    color: #2463eb;
}
.stat-green {
    background: #e5fcf1;
    color: #19c37d;
}
.stat-grey {
    background: #f0f1f3;
    color: #64748b;
}
.stat-number {
    font-size: 2.1rem;
    font-weight: 800;
    color: #212b36;
    margin-bottom: 5px;
    margin-left: 2px;
    letter-spacing: -1.2px;
}
.stat-label {
    font-size: 1.04rem;
    color: #76818b;
    font-weight: 500;
    margin-left: 4px;
    letter-spacing: 0.1px;
}

.alert {
    margin-bottom: 23px;
    font-size: 1.02em;
}

/* Sidebar */
.sidebar {
    position: fixed;
    top: 70px;
    left: 0;
    width: 248px;
    height: calc(100vh - 70px);
    background-color: var(--card-bg);
    padding: 23px 18px;
    border-right: 1.5px solid #e5e7eb;
    box-shadow: 1px 0 6px #b4c6f717;
    overflow-y: auto;
    z-index: 10;
}
.sidebar h5 {
    margin-bottom: 20px;
    color: var(--primary);
    letter-spacing: 0.6px;
}
.sidebar .nav-link {
    color: #2e3a4a;
    padding: 10px 18px;
    border-radius: 5px;
    margin-bottom: 4px;
    transition: background 0.22s, color 0.15s;
    font-weight: 500;
    font-size: 1.06em;
}
.sidebar .nav-link:hover, .sidebar .nav-link.active {
    background: linear-gradient(90deg, var(--primary) 70%, #3b82f633 100%);
    color: #fff;
}

.content {
    margin-left: 250px;
    padding: 18px 10px;
}

@media (max-width: 1050px) {
    .dashboard-stats {
        gap: 16px;
    }
    .stat-card {
        min-width: 150px;
        padding: 16px 8px 10px 14px;
    }
    .stat-icon {
        width: 40px;
        height: 40px;
        font-size: 1.38rem;
    }
    .sidebar {
        width: 190px;
    }
    .content {
        margin-left: 190px;
    }
}

@media (max-width: 768px) {
    .sidebar {
        position: static;
        width: 100%;
        height: auto;
        border-right: none;
        border-bottom: 1.5px solid #e5e7eb;
        box-shadow: none;
        margin-bottom: 10px;
        padding: 16px 10px;
    }
    .content {
        margin-left: 0;
        padding: 8px 2px;
    }
    .container {
        padding: 18px 3px;
    }
    .dashboard-stats {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;
    }
}

/* Modal: soft shadow & border */
.modal-content {
    border-radius: 14px;
    box-shadow: 0 6px 32px #0a225944, 0 2px 6px #2463eb16;
    border: 1px solid #e5e7eb;
}

.btn-close {
    opacity: 0.85;
}

.section-title {
    margin-top: 32px;
    margin-bottom: 10px;
    color: var(--primary);
    font-weight: 700;
    letter-spacing: 0.2px;
}

.btn-outline-success, .btn-outline-info, .btn-outline-warning, .btn-outline-danger {
    border-width: 2px;
    font-weight: 500;
}
.btn-outline-success:hover {
    background: var(--success);
    color: #fff;
    border-color: var(--success);
}
.btn-outline-info:hover {
    background: var(--info);
    color: #fff;
    border-color: var(--info);
}
.btn-outline-warning:hover {
    background: var(--warning);
    color: #fff;
    border-color: var(--warning);
}
.btn-outline-danger:hover {
    background: var(--danger);
    color: #fff;
    border-color: var(--danger);
}

    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container-fluid">
            <a class="navbar-brand" href="<%= ctx %>/admin/dashboard">🏠 HomeNest</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu" aria-controls="navMenu" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navMenu">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="<%= ctx %>/admin/logout">Logout</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Sidebar -->
    <div class="sidebar">
        <h5 class="text-primary">Admin Menu</h5>
        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link active" href="<%= ctx %>/admin/account">Accounts</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= ctx %>/admin/rooms?action=list">Rooms</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= ctx %>/admin/tenant?action=list">Tenants</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= ctx %>/admin/bill?action=list">Bills</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= ctx %>/admin/utility?action=list">Utilities</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= ctx %>/admin/record-reading">Record Usage</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= ctx %>/admin/usage">View Usage List</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= ctx %>/admin/contracts">Contracts</a>
            </li>
        </ul>
    </div>

    <!-- Content -->
    <div class="content">
        <div class="container">
            <!-- Header -->
            <div class="header">
                <h1>Account Management</h1>
            </div>

            <!-- Success Message -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <c:out value="${success}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <c:out value="${error}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

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

            <!-- Create Buttons -->
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
                        <th>ID</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Phone Number</th>
                        <th>Role</th>
                        <th>Block</th>
                        <th>Status</th>
                        <th>Created Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty userList}">
                            <c:forEach items="${userList}" var="user">
                                <tr>
                                    <td><c:out value="${user.userID}" /></td>
                                    <td><c:out value="${user.userFullName}" /></td>
                                    <td><c:out value="${user.email}" /></td>
                                    <td><c:out value="${user.phoneNumber}" /></td>
                                    <td><c:out value="${user.role.roleName}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.role.roleID == 2}">
                                                <c:out value="${user.block != null ? user.block.blockName : 'N/A'}" />
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
                                    <td><c:out value="${user.userCreatedAt}" /></td>
                                    <td>
                                        <c:if test="${user.role.roleID == 2}">
                                            <a href="${pageContext.request.contextPath}/admin/account?action=editUser&userID=${user.userID}"
                                               class="btn btn-sm btn-outline-success" title="Edit">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <c:if test="${user.userStatus ne 'Inactive'}">
                                                <button type="button" class="btn btn-sm btn-outline-warning"
                                                        onclick="showDisableUserModal(${user.userID}, '<c:out value="${fn:escapeXml(user.userFullName)}" />')"
                                                        title="Disable">
                                                    <i class="fas fa-user-slash"></i>
                                                </button>
                                            </c:if>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="9" class="empty-state">
                                    <i class="fas fa-users-slash"></i><br>No user data available
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- Customers Table -->
            <h2 class="section-title">Customers List</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Full Name</th>
                        <th>Phone Number</th>
                        <th>Email</th>
                        <th>ID Card</th>
                        <th>Gender</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty customerList}">
                            <c:forEach items="${customerList}" var="customer">
                                <tr>
                                    <td><c:out value="${customer.customerID}" /></td>
                                    <td><c:out value="${customer.customerFullName}" /></td>
                                    <td><c:out value="${customer.phoneNumber}" /></td>
                                    <td><c:out value="${customer.email}" /></td>
                                    <td><c:out value="${customer.CCCD}" /></td>
                                    <td><c:out value="${customer.gender}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${customer.customerStatus eq 'Active'}">
                                                <span class="status-badge status-active">Active</span>
                                            </c:when>
                                            <c:when test="${customer.customerStatus eq 'Inactive'}">
                                                <span class="status-badge status-inactive">Inactive</span>
                                            </c:when>
                                            <c:when test="${customer.customerStatus eq 'Potential'}">
                                                <span class="status-badge status-potential">Potential</span>
                                            </c:when>
                                            <c:when test="${customer.customerStatus eq 'Converted'}">
                                                <span class="status-badge status-converted">Converted</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/account?action=editCustomer&customerID=${customer.customerID}"
                                           class="btn btn-sm btn-outline-success" title="Edit">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/account?action=viewDetail&customerID=${customer.customerID}"
                                           class="btn btn-sm btn-outline-info" title="View Details">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <c:if test="${customer.customerStatus ne 'Inactive'}">
                                            <button type="button" class="btn btn-sm btn-outline-warning"
                                                    onclick="showDisableCustomerModal(${customer.customerID}, '<c:out value="${fn:escapeXml(customer.customerFullName)}" />')"
                                                    <c:if test="${cannotDeleteCustomerIds.contains(customer.customerID)}">disabled title="Customer has an active contract"</c:if>
                                                    title="Disable">
                                                <i class="fas fa-user-slash"></i>
                                            </button>
                                        </c:if>
                                        <button type="button" class="btn btn-sm btn-outline-danger"
                                                onclick="confirmDeleteCustomer(${customer.customerID}, '<c:out value="${fn:escapeXml(customer.customerFullName)}" />')"
                                                <c:if test="${cannotDeleteCustomerIds.contains(customer.customerID)}">disabled title="Customer has an active contract or rental request"</c:if>
                                                title="Delete">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" class="empty-state">
                                    <i class="fas fa-users-slash"></i><br>No customer data available
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- Disable User Modal -->
        <div class="modal fade" id="disableUserModal" tabindex="-1" aria-labelledby="disableUserLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form id="disableUserForm" method="post" action="${pageContext.request.contextPath}/admin/account" onsubmit="return validateDisableUserForm()">
                    <input type="hidden" name="action" value="disableUser">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="disableUserLabel">Disable User</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="userID" id="disableUserID">
                            <p><strong>User:</strong> <span id="disableUserName"></span></p>
                            <div class="mb-3">
                                <label for="disableUserReason" class="form-label">Reason</label>
                                <textarea class="form-control" id="disableUserReason" name="reason" rows="3" required placeholder="Enter reason for disabling"></textarea>
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
                <form id="disableCustomerForm" method="post" action="${pageContext.request.contextPath}/admin/account" onsubmit="return validateDisableCustomerForm()">
                    <input type="hidden" name="action" value="disableCustomer">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="disableCustomerLabel">Disable Customer</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="customerID" id="disableCustomerID">
                            <p><strong>Customer:</strong> <span id="disableCustomerName"></span></p>
                            <div class="mb-3">
                                <label for="disableCustomerReason" class="form-label">Reason</label>
                                <textarea class="form-control" id="disableCustomerReason" name="reason" rows="3" required placeholder="Enter reason for disabling"></textarea>
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
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script>
        // Show Disable User Modal
        function showDisableUserModal(id, name) {
            document.getElementById('disableUserID').value = id;
            document.getElementById('disableUserName').innerText = name;
            document.getElementById('disableUserReason').value = '';
            new bootstrap.Modal(document.getElementById('disableUserModal')).show();
        }

        // Show Disable Customer Modal
        function showDisableCustomerModal(id, name) {
            document.getElementById('disableCustomerID').value = id;
            document.getElementById('disableCustomerName').innerText = name;
            document.getElementById('disableCustomerReason').value = '';
            new bootstrap.Modal(document.getElementById('disableCustomerModal')).show();
        }

        // Validate Disable User Form
        function validateDisableUserForm() {
            const reason = document.getElementById('disableUserReason').value.trim();
            if (!reason) {
                alert('Please provide a reason for disabling the user.');
                return false;
            }
            if (reason.length < 5) {
                alert('Reason must be at least 5 characters long.');
                return false;
            }
            console.log('Submitting user disable form with userID:', document.getElementById('disableUserID').value, 'reason:', reason);
            return true;
        }

        // Validate Disable Customer Form
        function validateDisableCustomerForm() {
            const reason = document.getElementById('disableCustomerReason').value.trim();
            if (!reason) {
                alert('Please provide a reason for disabling the customer.');
                return false;
            }
            if (reason.length < 5) {
                alert('Reason must be at least 5 characters long.');
                return false;
            }
            console.log('Submitting customer disable form with customerID:', document.getElementById('disableCustomerID').value, 'reason:', reason);
            return true;
        }

        // Confirm Delete Customer
        function confirmDeleteCustomer(id, name) {
            if (confirm(`Are you sure you want to delete customer "${name}"? This action cannot be undone.`)) {
                window.location.href = '${pageContext.request.contextPath}/admin/account?action=delete&customerID=' + id;
            }
        }

        // Auto-dismiss alerts after 5 seconds
        document.addEventListener('DOMContentLoaded', function () {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                setTimeout(() => {
                    new bootstrap.Alert(alert).close();
                }, 5000);
            });
        });
    </script>
</body>
</html>