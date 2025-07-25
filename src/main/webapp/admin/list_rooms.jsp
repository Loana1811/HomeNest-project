<%-- 
    Document   : list_rooms
    Created on : Jun 14, 2025, 4:24:44 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Room, model.Block" %>
<%
    List<Block> blocks = (List<Block>) request.getAttribute("blockList");
    String selectedBlockID = (String) request.getAttribute("blockID");
    if (selectedBlockID == null) {
        selectedBlockID = "";
    }
    if (blocks == null) {
        blocks = new ArrayList<>();
    }
    
    String ctx = request.getContextPath();
    
    
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Room Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <style>
            :root {
                --teal: rgb(0, 128, 128);
                --light-bg: #f5f7fa;
                --highlight-bg: #e0f7f7;
            }

            body {
                background-color: var(--light-bg);
                font-family: 'Segoe UI', sans-serif;
            }



            .main {
                margin-left: 270px;
                padding: 2rem;
            }

            .btn-teal {
                background-color: var(--teal);
                color: white;
                border-radius: 30px;
            }

            .btn-teal:hover {
                background-color: #006666;
                color: white;
            }

            .card-header {
                background-color: var(--highlight-bg);
                font-weight: 600;
                font-size: 18px;
            }

            .room-img {
                width: 70px;
                height: 70px;
                object-fit: cover;
                border-radius: 12px;
                border: 2px solid #ddd;
            }

            .badge-available {
                background-color: #17c964;
                font-size: 0.85rem;
                border-radius: 1rem;
                padding: 0.4em 1em;
            }

            .badge-occupied {
                background-color: #adb5bd;
                font-size: 0.85rem;
                border-radius: 1rem;
                padding: 0.4em 1em;
            }

            .table td, .table th {
                vertical-align: middle;
            }

            .action-buttons .btn {
                margin-right: 0.5rem;
            }

            .bi {
                margin-right: 5px;
            }

            @media (max-width: 768px) {
                .main {
                    margin-left: 0;
                    padding: 1rem;
                }

                .sidebar {
                    display: none;
                }
            }
            .sidebar-link {
                display: block;
                padding: 10px 14px;
                color: white;
                text-decoration: none;
                font-weight: 500;
                border-radius: 25px;
                margin: 5px 0;
                transition: background 0.3s;
            }

            .sidebar-link:hover {
                background-color: rgba(255, 255, 255, 0.2);
            }

            .sidebar-link.active {
                background-color: #eaeaea; /* Màu xám nhạt */
                color: #333;               /* Màu chữ xám đậm */
                font-weight: bold;
            }
        </style>
    </head>
    <body>

        <!-- Sidebar -->



        <!-- Main Content -->
        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="text-dark"> Room Management</h2>
                <div>
                    <a href="rooms?action=new" class="btn btn-teal me-2"><i class="bi bi-plus-circle"></i> Add Room</a>
                    <a href="blocks?action=new" class="btn btn-outline-dark"><i class="bi bi-building-add"></i> Add Block</a>
                </div>
            </div>

            <div class="row g-3 align-items-end mb-4">
                <!-- Search Room -->
                <div class="col-md-6">
                    <form method="get" action="rooms" class="bg-white p-3 rounded shadow-sm">
                        <input type="hidden" name="action" value="search" />
                        <div class="row g-2 align-items-end">
                            <div class="col-md-8">

                                <input type="text" name="roomName" class="form-control" placeholder="Enter room number..."
                                       value="<%= request.getParameter("roomName") != null ? request.getParameter("roomName") : ""%>">
                            </div>
                            <div class="col-md-4 d-grid">
                                <button type="submit" class="btn btn-teal">
                                    <i class="bi bi-search"></i> Search
                                </button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- Filter by Block -->
                <div class="col-md-6">
                    <form method="get" action="rooms" class="bg-white p-3 rounded shadow-sm">
                        <input type="hidden" name="action" value="list" />
                        <div class="row g-2 align-items-end">
                            <div class="col-md-8">

                                <select name="blockID" class="form-select">
                                    <option value="">All Blocks</option>
                                    <% for (Block b : blocks) {%>
                                    <option value="<%= b.getBlockID()%>" <%= String.valueOf(b.getBlockID()).equals(selectedBlockID) ? "selected" : ""%>>
                                        <%= b.getBlockName()%>
                                    </option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="col-md-4 d-grid">
                                <button type="submit" class="btn btn-teal">
                                    <i class="bi bi-funnel"></i> Filter
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

          <!-- Room Table -->
<div class="card shadow-sm">
    <div class="card-header">Room List</div>
    <div class="card-body p-0">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th>Image</th>
                    <th><i class="bi bi-door-closed"></i> Room</th>
                    <th><i class="bi bi-cash-stack"></i> Price</th>
                    <th><i class="bi bi-info-circle"></i> Status</th>
                    <th class="text-center"><i class="bi bi-tools"></i> Actions</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    // Récupérer la liste des chambres à partir du request sans la redéclarer
                    List<Room> list = (List<Room>) request.getAttribute("list");  // Récupération de la liste
                    if (list != null && !list.isEmpty()) {
                        for (Room r : list) { 
                %>
                <tr>
                    <td>
                        <% if (r.getImagePath() != null && !r.getImagePath().isEmpty()) { %>
                            <img src="<%= request.getContextPath() + "/" + r.getImagePath() %>" alt="Room Image" class="room-img"
     onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/uploads/default.jpg';"/>

                        <% } else { %>
                            <span class="text-muted">No image</span>
                        <% } %>
                    </td>
                    <td><strong><%= r.getRoomNumber() %></strong></td>
                    <td><%= String.format("%,.0f VND", r.getRentPrice()) %></td>

                    <td>
                        <span class="badge <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "badge-available" : "badge-occupied" %>">
                            <i class="bi <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "bi-check-circle" : "bi-x-circle" %>"></i>
                            <%= r.getRoomStatus() %>
                        </span>
                    </td>
                    <td class="text-center action-buttons">
                        <a href="rooms?action=edit&id=<%= r.getRoomID() %>" class="btn btn-sm btn-outline-primary">Edit</a>
                        <!-- Button trigger modal -->
                        <button type="button" class="btn btn-sm btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteModal<%= r.getRoomID() %>">
                            Delete
                        </button>

                        <!-- Modal -->
                        <div class="modal fade" id="deleteModal<%= r.getRoomID() %>" tabindex="-1" aria-labelledby="deleteModalLabel<%= r.getRoomID() %>" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content">
                                    <div class="modal-header bg-danger text-white">
                                        <h5 class="modal-title" id="deleteModalLabel<%= r.getRoomID() %>">Confirm Delete</h5>
                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        Are you sure you want to delete room <strong><%= r.getRoomNumber() %></strong>?
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <a href="rooms?action=delete&id=<%= r.getRoomID() %>" class="btn btn-danger">Yes, Delete</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <% 
                        }
                    } else { 
                %>
                <tr>
                    <td colspan="6" class="text-center text-muted py-4">No rooms found.</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>



        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
