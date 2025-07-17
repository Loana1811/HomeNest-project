<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.UtilityType, model.IncurredFeeType" %>
<%
    List<UtilityType> systemList = (List<UtilityType>) request.getAttribute("systemList");
    List<IncurredFeeType> feeList = (List<IncurredFeeType>) request.getAttribute("feeList");
  
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manage Utilities & Fees</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <style>
            body {
                padding-top: 56px;
                background-color: #f8f9fa;
                font-family: 'Segoe UI', sans-serif;
            }
            .sidebar {
                width: 240px;
                position: fixed;
                top: 56px;
                left: 0;
                bottom: 0;
                background-color: #ffffff;
                border-right: 1px solid #e0e0e0;
                padding: 1rem;
                box-shadow: 2px 0 6px rgba(0, 0, 0, 0.05);
                z-index: 1030;
            }
            .sidebar h5 {
                font-weight: 700;
                margin-bottom: 1rem;
            }
            .sidebar .nav-link {
                padding: 10px 14px;
                margin-bottom: 6px;
                color: #333;
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.2s;
            }
            .sidebar .nav-link:hover,
            .sidebar .nav-link.active {
                background: #0d6efd;
                color: white;
            }
            main {
                margin-left: 260px;
                padding: 2rem 1rem;
            }
            @media (max-width: 768px) {
                .sidebar {
                    display: none;
                }
                main {
                    margin-left: 0;
                }
            }
            h3 {
                font-weight: bold;
                margin-bottom: 1.5rem;
            }
            .btn-primary, .btn-success {
                font-weight: 500;
                font-size: 16px;
                padding: 10px 20px;
                border-radius: 8px;
            }
            table {
                background: white;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            }
            thead {
                background-color: #0d6efd;
                color: white;
            }
            th, td {
                text-align: center;
                vertical-align: middle !important;
                padding: 12px;
            }
            .btn-outline-primary, .btn-outline-danger {
                margin: 0 2px;
            }
            .alert-info, .alert-danger {
                border-radius: 8px;
                font-size: 14px;
            }
            .modal-title {
                font-weight: bold;
            }

        </style>
    </head>
    <body>
        <!-- NAVBAR -->
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
            <div class="container-fluid">
                <a class="navbar-brand" href="#">🏠 HomeNest</a>
                <button class="navbar-toggler" type="button"
                        data-bs-toggle="collapse" data-bs-target="#navMenu">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navMenu">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="<%= ctx%>/admin/logout">Logout</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="sidebar">
            <h5 class="text-primary">Admin Menu</h5>
            <ul class="nav flex-column">

                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/viewListAccount">Accounts</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/rooms?action=list">Rooms</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/tenant?action=list">Tenants</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/bill?action=list">Bills</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/utility?action=list">Utilities</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/record-reading">Record Usage</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/statistical">Statistical</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/usage">View Usage List</a>
                </li>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/adminReport">Report</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/admin/notification?action=viewNotifications">Notification</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx%>/Contracts">Contract</a>
                </li>


            </ul>
        </div>


        <!-- MAIN CONTENT -->
        <main>
            <h3 class="mb-3">🧾 Manage Utilities & Fees</h3>
            <% if (error != null) { %>
            <div class="alert alert-danger"><%= error %></div>
            <% } %>

            <div class="mb-3 d-flex gap-3">
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addUtilityModal">➕ Add Utility</button>
                <button class="btn btn-sm btn-success" data-bs-toggle="modal" data-bs-target="#addFeeModal">+ Add Fee</button>


            </div>

            <!-- SYSTEM UTILITIES -->
            <h5 class="mt-4">🔐 System-defined Utilities</h5>
            <% if (systemList != null && !systemList.isEmpty()) { %>
            <table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Unit</th>
                        <th>Price (VND)</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (UtilityType u : systemList) { %>
                    <tr>
                        <td><%= u.getUtilityName() %></td>
                        <td><%= u.getUnit() %></td>
                        <td><%= String.format("%,.0f", u.getUnitPrice().doubleValue()) %></td>
                        <td>
                            <a href="#" class="btn btn-sm btn-outline-primary"
                               data-bs-toggle="modal"
                               data-bs-target="#editUtilityModal"
                               onclick="openEditModal(<%= u.getUtilityTypeID() %>)">✏️</a>
                            <span class="text-warning" title="Default utility cannot be deleted.">⚠️</span>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="alert alert-info">No system-defined utilities found.</div>
            <% } %>

            <!-- INCURRED FEES -->
            <h5 class="mt-4">💰 Incurred Fees</h5>
            <% if (feeList != null && !feeList.isEmpty()) { %>
            <table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Default Amount (VND)</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (IncurredFeeType fee : feeList) { %>
                    <tr>
                        <td><%= fee.getFeeName() %></td>
                        <td><%= String.format("%,.0f", fee.getDefaultAmount().doubleValue()) %></td>
                        <td>
                            <a href="#" class="btn btn-sm btn-outline-primary"
                               data-bs-toggle="modal"
                               data-bs-target="#editFeeModal"
                               onclick="openEditFeeModal(<%= fee.getIncurredFeeTypeID() %>)">✏️</a>
                            <button type="button" class="btn btn-sm btn-outline-danger"
                                    data-bs-toggle="modal"
                                    data-bs-target="#confirmDeleteFeeModal"
                                    onclick="setDeleteFeeId(<%= fee.getIncurredFeeTypeID() %>)">🗑️</button>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="alert alert-info">No incurred fees found.</div>
            <% } %>

            <!-- PRICE HISTORY BUTTON -->
            <div class="mt-4">
                <a href="utility?action=history" class="btn btn-outline-secondary">📜 View Price History</a>
            </div>

            <!-- MODALS & SCRIPTS -->

            <!-- MODAL: ADD UTILITY (nếu bạn có modal này riêng thì giữ nguyên hoặc include) -->
            <jsp:include page="create-utilitySystem.jsp" /> <!-- Sửa thành createUtility.jsp nếu là tiện ích -->

            <!-- MODAL: EDIT UTILITY (dynamic load, giống cũ) -->
            <div class="modal fade" id="editUtilityModal" tabindex="-1">
                <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                    <div class="modal-content" id="editModalContent">
                        <!-- Content from server will be loaded here -->
                    </div>
                </div>
            </div>

            <!-- MODAL: ADD FEE -->

            <jsp:include page="incurredFeeType-add.jsp" />


            <!-- MODAL: EDIT FEE (dynamic load) -->
            <div class="modal fade" id="editFeeModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content" id="editFeeModalContent">
                        <!-- Nội dung sẽ được load động -->
                    </div>
                </div>
            </div>

            <!-- MODAL: DELETE FEE -->
            <div class="modal fade" id="confirmDeleteFeeModal" tabindex="-1" aria-labelledby="confirmDeleteFeeModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <form action="<%= request.getContextPath() %>/admin/feeType" method="get">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" id="deleteFeeId">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title text-danger">⚠️ Confirm Delete</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                Are you sure you want to delete this fee?
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-danger">✅ Yes, delete</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">❌ Cancel</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- JS SUPPORT -->
            <script>
                function setDeleteFeeId(id) {
                    document.getElementById('deleteFeeId').value = id;
                }

                function openEditFeeModal(id) {
                    fetch('<%= request.getContextPath()%>/admin/feeType?action=editModal&id=' + id)
                            .then(response => {
                                if (!response.ok)
                                    throw new Error("HTTP error " + response.status);
                                return response.text();
                            })
                            .then(html => {
                                const modalContent = document.getElementById('editFeeModalContent');
                                modalContent.innerHTML = html;
                                if (window.editFeeModalObj) {
                                    window.editFeeModalObj.hide();
                                    window.editFeeModalObj.dispose();
                                }
                                window.editFeeModalObj = new bootstrap.Modal(document.getElementById('editFeeModal'));
                                window.editFeeModalObj.show();
                            })
                            .catch(err => {
                                alert("⚠️ Cannot load edit form for fee.");
                            });
                }
            </script>

            <!-- SCRIPT: OPEN EDIT UTILITY MODAL (giống code cũ của bạn) -->
            <script>
                function openEditModal(id) {
                    fetch('<%= request.getContextPath()%>/admin/utility?action=editModal&id=' + id)
                            .then(response => {
                                if (!response.ok)
                                    throw new Error("HTTP error " + response.status);
                                return response.text();
                            })
                            .then(html => {
                                const modalContent = document.getElementById('editModalContent');
                                modalContent.innerHTML = html;
                                if (window.editModalObj) {
                                    window.editModalObj.hide();
                                    window.editModalObj.dispose();
                                }
                                window.editModalObj = new bootstrap.Modal(document.getElementById('editUtilityModal'));
                                window.editModalObj.show();
                            })
                            .catch(err => {
                                console.error("⚠️ Failed to load modal:", err);
                                alert("⚠️ Cannot load edit form. Check console for details.");
                            });
                }
            </script>

            <div class="mt-4">
                <a href="<%= ctx%>/admin/dashboard" class="btn btn-outline-secondary">
                    ← Back to Dashboard
                </a>
            </div>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
