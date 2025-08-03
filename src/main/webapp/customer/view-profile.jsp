<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Contract" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
        response.sendRedirect(request.getContextPath() + "/Login.jsp");
        return;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String valueBirthDate = "";
    if (request.getAttribute("tempBirthDate") != null) {
        valueBirthDate = (String) request.getAttribute("tempBirthDate");
    } else if (customer.getBirthDate() != null) {
        valueBirthDate = sdf.format(customer.getBirthDate());
    }
    List<Contract> contracts = (List<Contract>) request.getAttribute("contracts");
    String ctx = request.getContextPath();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Customer Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet"/>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <style>
            :root {
                --primary-color: #2c4bff;
                --secondary-color: #4b5ef5;
                --pastel-blue: #e6edff;
                --sidebar-bg: #1a2b5f;
                --white: #ffffff;
                --background-color: #f8fafc;
                --shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
                --border-radius: 16px;
                --light-gray: #e5e7eb;
                --success: #22c55e;
                --danger: #ef4444;
                --warning: #f59e0b;
                --success-bg: #ecfdf5;
                --pending-bg: #fefce8;
                --table-header-bg: #166534;
                --text-primary: #1f2937;
                --text-secondary: #6b7280;
            }

            html, body {
                height: 100%;
                margin: 0;
                font-family: 'Inter', sans-serif;
                font-size: 1rem;
                color: var(--text-primary);
            }

            body {
                background-color: var(--background-color);
                padding-top: 90px;
                display: flex;
                flex-direction: column;
                min-height: 100vh;
            }

            .container-dashboard {
                flex: 1;
                display: flex;
                max-width: 1400px;
                margin: 2rem auto;
                background-color: var(--white);
                border-radius: var(--border-radius);
                box-shadow: var(--shadow);
                width: 90%;
                transition: all 0.3s ease;
                flex-direction: row;
                min-height: 600px; /* Ensure enough height for content */
            }

            .sidebar {
                width: 280px;
                background-color: var(--sidebar-bg);
                padding: 2rem 1.5rem;
                color: var(--white);
                transition: width 0.3s ease;
            }

            .sidebar h2 {
                text-align: center;
                margin-bottom: 2rem;
                font-size: 1.5rem;
                font-weight: 600;
                letter-spacing: 0.5px;
            }

            .nav-link {
                color: var(--white);
                background-color: transparent;
                border: none;
                margin-bottom: 0.75rem;
                border-radius: 10px;
                padding: 0.75rem 1rem;
                font-size: 1.1rem;
                font-weight: 500;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .nav-link:hover,
            .nav-link.active {
                background-color: var(--primary-color);
                transform: translateX(5px);
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                border-radius: 30px;
            }

            .content-area {
                flex: 1;
                padding: 2.5rem;
                background-color: var(--white);
                border-radius: 0 var(--border-radius) var(--border-radius) 0;
                overflow-y: auto; /* Allow scrolling if content overflows */
                min-height: 100%; /* Ensure content area takes full height */
            }

            .form-control,
            .form-select {
                border-radius: 10px;
                padding: 0.75rem 1rem;
                border: 1px solid var(--light-gray);
                transition: border-color 0.3s ease, box-shadow 0.3s ease;
            }

            .form-control:focus,
            .form-select:focus {
                border-color: var(--primary-color);
                box-shadow: 0 0 0 3px rgba(44, 75, 255, 0.1);
                outline: none;
            }

            .form-label {
                font-weight: 500;
                color: var(--text-primary);
                margin-bottom: 0.5rem;
            }

            .btn-navy {
                background-color: var(--primary-color);
                color: var(--white);
                border-radius: 50px;
                padding: 0.75rem 1.5rem;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .btn-navy:hover {
                background-color: #1e3a8a;
                transform: translateY(-2px);
            }

            .btn-success {
                background-color:var(--primary-color);
                border-radius: 50px;
                padding: 0.75rem 1.5rem;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .btn-success:hover {
                background-color: #16a34a;
                transform: translateY(-2px);
            }

            .btn-danger {
                background-color: var(--danger);
                border-radius: 10px;
                padding: 0.75rem 1.5rem;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .btn-danger:hover {
                background-color: #dc2626;
                transform: translateY(-2px);
            }

            .btn-secondary {
                background-color: var(--white);
                color: var(--primary-color);
                border: 2px solid var(--primary-color);
                border-radius: 50px;
                padding: 0.75rem 1.5rem;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .btn-secondary:hover {
                background-color: var(--pastel-blue);
                transform: translateY(-2px);
            }

            .alert {
                font-size: 0.9rem;
                border-radius: 12px;
                padding: 1rem;
                margin-bottom: 1.5rem;
                border: none;
                box-shadow: var(--shadow);
            }

            .alert-danger {
                background-color: #fef2f2;
                color: var(--danger);
                border-left: 5px solid var(--danger);
            }

            .alert-success {
                background-color: var(--success-bg);
                color: var(--success);
                border-left: 5px solid var(--success);
            }

            .table {
                width: 100%;
                border-collapse: separate;
                border-spacing: 0;
                background: var(--white);
                border-radius: var(--border-radius);
                overflow: hidden;
                box-shadow: var(--shadow);
            }

            .table th,
            .table td {
                padding: 1rem 1.5rem;
                text-align: center;
                font-size: 0.95rem;
            }

            .table th {
                background: var(--table-header-bg);
                color: var(--white);
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .table tr {
                transition: background-color 0.3s ease;
            }

            .table tr:nth-child(even) {
                background-color: #f3faf3;
            }

            .table tr:hover {
                background-color: #e6f3e6;
            }

            .no-data {
                font-style: italic;
                color: var(--text-secondary);
                padding: 1.5rem;
                text-align: center;
                font-size: 1rem;
            }

            .badge-status {
                padding: 0.5rem 1rem;
                border-radius: 20px;
                font-size: 0.9rem;
                font-weight: 500;
                background-color: var(--pastel-blue);
                color: var(--primary-color);
            }

            .row.mb-3 {
                padding: 0.75rem 0;
                border-bottom: 1px solid var(--light-gray);
                align-items: center; /* Ensure proper alignment */
            }

            .label {
                font-weight: 600;
                color: var(--text-primary);
            }

            .value {
                color: var(--text-secondary);
                font-weight: 400;
            }

            .navbar {
                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                position: fixed;
                top: 0;
                width: 100%;
                z-index: 1030;
                padding: 0.5rem 2rem;
                box-shadow: var(--shadow);
            }

            .navbar .btn-back,
            .navbar .btn-danger {
                transition: transform 0.2s ease, background-color 0.3s ease;
            }

            .navbar .btn-back:hover,
            .navbar .btn-danger:hover {
                transform: translateY(-2px);
                background-color: #1e40af;
            }

            /* Responsive */
            @media (max-width: 1200px) {
                .container-dashboard {
                    flex-direction: column;
                    width: 95%;
                }

                .sidebar {
                    width: 100%;
                    padding: 1.5rem;
                    text-align: center;
                }

                .content-area {
                    padding: 1.5rem;
                    min-height: auto; /* Adjust for smaller screens */
                }

                .navbar {
                    padding: 0.75rem 1rem;
                }
            }

            @media (max-width: 768px) {
                .table th,
                .table td {
                    font-size: 0.85rem;
                    padding: 0.75rem;
                }

                .btn-navy,
                .btn-success,
                .btn-secondary,
                .btn-danger {
                    padding: 0.5rem 1rem;
                    font-size: 0.9rem;
                }

                .row.mb-3 {
                    flex-direction: column;
                    align-items: flex-start;
                }

                .label, .value {
                    width: 100%;
                    margin-bottom: 0.5rem;
                }
            }
        </style>
    </head>
    <body>
        <!-- Alerts -->
        <c:if test="${not empty success}">
            <div class="alert alert-success text-center m-2">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger text-center m-2">${error}</div>
        </c:if>
        <!-- Navbar -->
        <nav class="navbar navbar-light shadow-sm">
            <div class="container-fluid d-flex justify-content-between align-items-center">
                <a href="<%= ctx %>/customer/room-list" class="btn btn-back text-white">
                    <i class="bi bi-arrow-left-circle"></i> Back
                </a>
                <span class="fs-5 fw-bold text-white">Customer Dashboard</span>
                <a href="<%= ctx %>/Logouts" class="btn btn-danger rounded-pill text-white">
                    <i class="bi bi-box-arrow-right"></i>
                </a>
            </div>
        </nav>

        <!-- Toast Notification -->
        <c:if test="${not empty success}">
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 1055;">
                <div class="toast show align-items-center border-0 alert-success" role="alert">
                    <div class="d-flex">
                        <div class="toast-body">${success}</div>
                        <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Dashboard Content -->
        <div class="container-dashboard">
            <!-- Sidebar -->
            <div class="sidebar">
                <div class="nav flex-column nav-pills" id="dashboardTab" role="tablist" aria-orientation="vertical">
                    <button class="nav-link ${activeTab eq 'view' or empty activeTab ? 'active' : ''}" 
                            id="view-tab" data-bs-toggle="pill" data-bs-target="#view" type="button" role="tab">
                        <i class="bi bi-person-circle"></i> View Profile
                    </button>
                    <button class="nav-link ${activeTab eq 'edit' ? 'active' : ''}" 
                            id="edit-tab" data-bs-toggle="pill" data-bs-target="#edit" type="button" role="tab">
                        <i class="bi bi-pencil-square"></i> Edit Profile
                    </button>
                    <button class="nav-link ${activeTab eq 'change-password' ? 'active' : ''}" 
                            id="change-password-tab" data-bs-toggle="pill" data-bs-target="#change-password" type="button" role="tab">
                        <i class="bi bi-lock"></i> Change Password
                    </button>
                    <button class="nav-link ${activeTab eq 'contract-history' ? 'active' : ''}" 
                            id="contract-history-tab" data-bs-toggle="pill" data-bs-target="#contract-history" type="button" role="tab">
                        <i class="bi bi-file-earmark-text"></i> Contract History
                    </button>
                </div>
            </div>

            <!-- Content Area -->
            <div class="content-area">
                <div class="tab-content" id="dashboardTabContent">
                    <div class="tab-pane fade ${activeTab eq 'view' or empty activeTab ? 'show active' : ''}" id="view" role="tabpanel">
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>Full Name:</strong></div>
                            <div class="col-md-8 value"><%= customer.getCustomerFullName() %></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>Email:</strong></div>
                            <div class="col-md-8 value"><%= customer.getEmail() %></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>Phone:</strong></div>
                            <div class="col-md-8 value"><%= customer.getPhoneNumber() %></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>CCCD:</strong></div>
                            <div class="col-md-8 value">
                                <%= customer.getCCCD() != null ? customer.getCCCD() : "Chưa cập nhật" %>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>Gender:</strong></div>
                            <div class="col-md-8 value">
                                <%= customer.getGender() != null ? customer.getGender() : "Chưa cập nhật" %>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>Birth Date:</strong></div>
                            <div class="col-md-8 value">
                                <%= customer.getBirthDate() != null ? sdf.format(customer.getBirthDate()) : "N/A" %>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>Address:</strong></div>
                            <div class="col-md-8 value">
                                <%= customer.getAddress() != null ? customer.getAddress() : "Chưa cập nhật" %>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4 label"><strong>Status:</strong></div>
                            <div class="col-md-8 value">
                                <span class="badge-status"><%= customer.getCustomerStatus() %></span>
                            </div>
                        </div>
                    </div>

                    <!-- Edit Profile Tab -->
                    <div class="tab-pane fade ${activeTab eq 'edit' ? 'show active' : ''}" id="edit" role="tabpanel">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                ${errorMessage}
                            </div>
                        </c:if>
                        <form action="<%= request.getContextPath() %>/update-profile" method="post">
                            <div class="mb-3">
                                <label for="fullName" class="form-label">Full Name</label>
                                <input type="text" class="form-control" id="fullName" name="fullName"
                                       value="${not empty tempFullName ? tempFullName : customer.customerFullName}" required>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="${not empty tempEmail ? tempEmail : customer.email}" required>
                            </div>
                            <div class="mb-3">
                                <label for="phone" class="form-label">Phone Number</label>
                                <input type="text" class="form-control" id="phone" name="phone"
                                       value="${not empty tempPhone ? tempPhone : customer.phoneNumber}" required>
                            </div>
                            <%
                            String valueCCCD = "";
                            if (request.getAttribute("tempCCCD") != null) {
                                valueCCCD = (String) request.getAttribute("tempCCCD");
                            } else if (customer != null && customer.getCCCD() != null) {
                                valueCCCD = customer.getCCCD();
                            }
                            %>
                            <div class="mb-3">
                                <label for="cccd" class="form-label">CCCD</label>
                                <input type="text" class="form-control" id="cccd" name="cccd"
                                       placeholder="Chưa cập nhật"
                                       value="<%= valueCCCD %>" readonly>
                            </div>
                            <div class="mb-3">
                                <label for="gender" class="form-label">Gender</label>
                                <c:set var="genderValue" value="${not empty tempGender ? tempGender : (customer.gender != null ? customer.gender : 'Khác')}" />
                                <select name="gender" id="gender" class="form-select" required>
                                    <option value="Nam" ${genderValue == 'Nam' ? 'selected' : ''}>Nam</option>
                                    <option value="Nữ" ${genderValue == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                    <option value="Khác" ${genderValue == 'Khác' ? 'selected' : ''}>Khác</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="birthDate" class="form-label">Birth Date</label>
                                <input type="date" class="form-control" id="birthDate" name="birthDate"
                                       value="<%= valueBirthDate %>" required>
                            </div>
                            <div class="mb-3">
                                <label for="address" class="form-label">Address</label>
                                <input type="text" class="form-control" id="address" name="address"
                                       value="${not empty tempAddress ? tempAddress : (customer.address != null ? customer.address : '')}" required>
                            </div>
                            <div class="text-center mt-4">
                                <button type="submit" class="btn btn-success btn-custom me-2">Save</button>
                                <a href="view-profile.jsp" class="btn btn-secondary btn-custom">Cancel</a>
                            </div>
                        </form>
                    </div>

                    <!-- Change Password Tab -->
                    <div class="tab-pane fade ${activeTab eq 'change-password' ? 'show active' : ''}" 
                         id="change-password" role="tabpanel">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                ${success}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        <div id="passwordError" class="alert alert-danger d-none" role="alert"></div>
                        <form action="<%= ctx %>/customer/change-password" method="post" onsubmit="return validatePassword();">
                            <div class="mb-3">
                                <label for="currentPassword" class="form-label">Current Password</label>
                                <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                            </div>
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">New Password</label>
                                <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                            </div>
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                            </div>
                            <div class="d-flex justify-content-between">
                                <button type="submit" class="btn btn-navy">Update Password</button>
                                <button type="button" class="btn btn-secondary" 
                                        data-bs-toggle="pill" data-bs-target="#view">Cancel</button>
                            </div>
                        </form>
                    </div>

                    <!-- Contract History Tab -->
                    <div class="tab-pane fade ${activeTab eq 'contract-history' ? 'show active' : ''}" 
                         id="contract-history" role="tabpanel">
                        <c:if test="${empty contracts}">
                            <div class="no-data">No contract history found.</div>
                        </c:if>
                        <c:if test="${not empty contracts}">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Contract ID</th>
                                        <th>Room</th>
                                        <th>Start Date</th>
                                        <th>End Date</th>
                                        <th>Status</th>
                                        <th>Created At</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${contracts}">
                                        <tr>
                                            <td>${c.contractId}</td>
                                            <td>${c.roomNumber}</td>
                                            <td><fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/></td>
                                            <td><fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy"/></td>
                                            <td>${c.contractStatus}</td>
                                            <td><fmt:formatDate value="${c.contractCreatedAt}" pattern="dd/MM/yyyy"/></td>
                                            <td>
                                                <a href="<%= ctx %>/Contracts?action=viewDetails&id=${c.contractId}" 
                                                   class="btn btn-success btn-sm">View Details</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <%@include file="/WEB-INF/inclu/footer.jsp" %>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" 
                integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" 
                crossorigin="anonymous"></script>
        <script>
            window.onload = function () {
                const toastEl = document.querySelector('.toast');
                if (toastEl) {
                    const bsToast = new bootstrap.Toast(toastEl, {
                        delay: 3000,
                        autohide: true
                    });
                    bsToast.show();
                }
            };

            function validatePassword() {
                const newPassword = document.getElementById("newPassword").value;
                const confirmPassword = document.getElementById("confirmPassword").value;
                const errorDiv = document.getElementById("passwordError");
                const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_])[A-Za-z\d\W_]{8,20}$/;

                errorDiv.classList.add("d-none");
                errorDiv.innerHTML = "";

                if (/\s/.test(newPassword)) {
                    errorDiv.innerHTML = "Password must not contain whitespace.";
                    errorDiv.classList.remove("d-none");
                    return false;
                }

                if (!pattern.test(newPassword)) {
                    errorDiv.innerHTML = "Password must be 8–20 characters, contain uppercase, lowercase, digit, and special character.";
                    errorDiv.classList.remove("d-none");
                    return false;
                }

                if (newPassword !== confirmPassword) {
                    errorDiv.innerHTML = "New password and confirmation do not match.";
                    errorDiv.classList.remove("d-none");
                    return false;
                }

                return true;
            }

            document.addEventListener('DOMContentLoaded', function () {
                const activeTab = '${activeTab}';
                if (activeTab) {
                    const tab = document.querySelector(`#${activeTab}-tab`);
                    if (tab) {
                        const bsTab = new bootstrap.Tab(tab);
                        bsTab.show();
                    }
                }

                const toastEl = document.querySelector('.toast');
                if (toastEl) {
                    const bsToast = new bootstrap.Toast(toastEl, {
                        delay: 3000,
                        autohide: true
                    });
                    bsToast.show();
                }
            });
        </script>
    </body>
</html>