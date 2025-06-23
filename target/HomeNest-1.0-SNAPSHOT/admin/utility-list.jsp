<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.UtilityType" %>
<%
    List<UtilityType> systemList = (List<UtilityType>) request.getAttribute("systemList");
    List<UtilityType> userList = (List<UtilityType>) request.getAttribute("userList");
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html>
    <head>
        <style>
            body {
                background-color: #f8f9fa;
                font-family: 'Segoe UI', sans-serif;
                color: #343a40;
            }

            h3 {
                font-weight: bold;
                margin-bottom: 1.5rem;
            }

            .btn-primary {
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

            table thead {
                background-color: #0d6efd;
                color: white;
            }

            table th, table td {
                vertical-align: middle !important;
                text-align: center;
            }

            .table td {
                padding: 12px;
            }

            .btn-outline-primary, .btn-outline-danger {
                margin: 0 2px;
            }

            .section-header {
                font-size: 1.25rem;
                font-weight: 600;
                margin-top: 2rem;
                margin-bottom: 0.75rem;
                color: #0d6efd;
            }

            .alert-info, .alert-danger {
                border-radius: 8px;
                font-size: 14px;
            }

            .btn-outline-secondary {
                margin-top: 20px;
                font-weight: 500;
                border-radius: 6px;
            }

            .btn-outline-secondary i {
                margin-right: 5px;
            }

            .modal-title {
                font-weight: bold;
            }
        </style>

        <meta charset="UTF-8">
        <title>Utility List</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </head>
    <body class="container mt-4">

        <h3 class="mb-3">🧾 Manage Utilities</h3>

        <% if (error != null) {%>
        <div class="alert alert-danger"><%= error%></div>
        <% } %>

        <div class="mb-3">
            <a href="utility?action=create" class="btn btn-primary">➕ Add Utility</a>
        </div>

        <h5 class="mt-4">🔐 System-defined Utilities</h5>
        <% if (systemList != null && !systemList.isEmpty()) { %>
        <table class="table table-bordered table-hover">
            <thead class="table-light">
                <tr>
                    <th>Name</th>
                    <th>Unit</th>
                    <th>Price (VND)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (UtilityType u : systemList) {%>
                <tr>
                    <td><%= u.getUtilityName()%></td>
                    <td><%= u.getUnit()%></td>
                    <td><%= String.format("%,.0f", u.getUnitPrice().doubleValue())%></td>
                    <td>
                        <a href="utility?action=edit&id=<%= u.getUtilityTypeID()%>" class="btn btn-sm btn-outline-primary">✏️</a>
                        <span class="text-warning" title="Default utility cannot be deleted.">⚠️</span>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } else { %>
        <div class="alert alert-info">No system-defined utilities found.</div>
        <% } %>

        <h5 class="mt-4">🧑‍💻 User-defined Utilities</h5>
        <% if (userList != null && !userList.isEmpty()) { %>
        <table class="table table-bordered table-hover">
            <thead class="table-light">
                <tr>
                    <th>Name</th>
                    <th>Unit</th>
                    <th>Price (VND)</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (UtilityType u : userList) {%>
                <tr>
                    <td><%= u.getUtilityName()%></td>
                    <td><%= u.getUnit()%></td>
                    <td><%= String.format("%,.0f", u.getUnitPrice().doubleValue())%></td>
                    <td>
                        <a href="utility?action=edit&id=<%= u.getUtilityTypeID()%>" class="btn btn-sm btn-outline-primary">✏️</a>
                        <a href="#" class="btn btn-sm btn-outline-danger"
                           data-bs-toggle="modal"
                           data-bs-target="#confirmDeleteModal"
                           onclick="setDeleteId(<%= u.getUtilityTypeID()%>)">🗑️</a>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } else { %>
        <div class="alert alert-info">No user-defined utilities found.</div>
        <% }%>

        <div class="mt-4">
            <a href="utility?action=history" class="btn btn-outline-secondary">📜 View Price History</a>
        </div>

        <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <form action="utility" method="get">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" id="deleteUtilityId">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title text-danger">⚠️ Confirm Delete</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            Are you sure you want to delete this utility?
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-danger">✅ Yes, delete</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">❌ Cancel</button>
                        </div>

                    </div>
                </form>
            </div>

        </div>

        <script>
            function setDeleteId(id) {
                document.getElementById('deleteUtilityId').value = id;
            }
        </script>
        <div class="mt-4">
            <a href="<%= request.getContextPath()%>/admin/dashboard" class="btn btn-outline-secondary">
                ← Back to Dashboard
            </a>
        </div>

    </body>
</html>
