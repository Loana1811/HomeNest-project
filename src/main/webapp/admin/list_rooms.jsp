    <%--  
        Document   : list_rooms  
        Created on : Jun 14, 2025, 4:24:44 PM  
        Author     : Admin  
    --%>  
    <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>  
    <%@ page import="java.util.*, model.Room, model.Block" %>  
    <%  
        List<Block> blocks = (List<Block>) request.getAttribute("blockList");  
        String selectedBlockID = (String) request.getAttribute("blockID");  
        if (selectedBlockID == null) selectedBlockID = "";  
        if (blocks == null) blocks = new ArrayList<>();  
        String ctx = request.getContextPath();  
    %>  
    <%@ include file="/WEB-INF/inclu/header_admin.jsp" %>  

    <!DOCTYPE html>  
    <html lang="en">  
        <head>  
            <meta charset="UTF-8">  
            <title>Room Management</title>  
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">  
            <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">  
            <style>
                :root {
                    --primary-color: #1e3b8a;
                    --secondary-color: #c7d2fe;
                    --accent-color: #2a4d69;
                    --text-color: #111827;
                    --white: #ffffff;
                    --background-color: #f5f9fc;
                    --sidebar-color: #003459;
                    --shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                    --border-radius: 12px;
                    --success: #16a34a;
                    --warning: #d97706;
                    --danger: #b91c1c;
                    --light-gray: #f3f4f6;
                    --success-bg: #d1fae5;
                    --pending-bg: #ffedd5;
                    --active-bg: #e0f2fe;
                    --table-header-bg: #1e3b8a;
                    --table-header-text: #ffffff;
                }
                body {
                    background-color: var(--background-color);
                    font-family: 'Segoe UI', sans-serif;
                    color: var(--text-color);
                    font-size: 1.1rem;
                }
                .main-content {
                    margin-left: 270px;
                    padding: 3rem;
                }
                .btn-teal {
                    background-color: var(--primary-color);
                    color: var(--white);
                    border-radius: 30px;
                    font-size: 0.875rem;
                    padding: 0.6rem 1.2rem;
                }
                .btn-teal:hover {
                    background-color: var(--accent-color);
                }
                .btn-outline-dark {
                    font-size: 1.3rem;
                    padding: 0.6rem 1.2rem;
                    border-radius: 30px;
                }
                .card-header {
                    background-color: var(--secondary-color);
                    font-weight: 400;
                    font-size: 1rem;
                }
                .room-img {
                    width: 120px;
                    height: 120px;
                    object-fit: cover;
                    border-radius: 10px;
                    border: 2px solid var(--light-gray);
                }
                .badge-available {
                    background-color: var(--success);
                    font-size: 1.1rem;
                }
                .badge-occupied {
                    background-color: var(--light-gray);
                    font-size: 1.1rem;
                }
                .sidebar {
                    position: fixed;
                    width: 285px;
                    left: 0;
                    height: 100%;
                    background-color: var(--sidebar-color);
                    z-index: 10;
                }
                .table thead th {
                    background-color: var(--table-header-bg);
                    color: var(--table-header-text);
                    font-size: 1.4rem;
                    padding: 1rem;
                }
                .table tbody td {
                    font-size: 1.1rem;
                }
                h2.text-dark {
                    font-size: 2rem;
                    color: var(--success);
                }
                .btn-sm {
                    font-size: 1.3rem;
                    padding: 0.7rem 1.2rem;
                    border-radius: 20px;
                }
         td .action-buttons {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
}

.btn-icon {
    width: 36px;
    height: 36px;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    font-size: 16px;
    transition: 0.2s ease-in-out;
}

.btn-view {
    background-color: #0dcaf0;
    color: white;
    border: none;
}

.btn-edit {
    background-color: white;
    border: 1px solid #0d6efd;
    color: #0d6efd;
}

.btn-delete {
    background-color: white;
    border: 1px solid #dc3545;
    color: #dc3545;
}

.btn-view:hover,
.btn-edit:hover,
.btn-delete:hover {
    opacity: 0.9;
    transform: scale(1.05);
}



            </style>  
        </head>  
        <body>  

            <div class="main-content">  
                <div class="d-flex justify-content-between align-items-center mb-4">  
                    <h2 class="text-dark">Room Management</h2>  
                    <div>  
                        <a href="rooms?action=new" class="btn btn-teal me-2"><i class="bi bi-plus-circle"></i> Add Room</a>  
                    </div>  
                </div>  

                <div class="row g-3 align-items-end mb-4">  
                    <div class="col-md-6">  
                        <form method="get" action="rooms" class="bg-white p-3 rounded shadow-sm">  
                            <input type="hidden" name="action" value="search" />  
                            <div class="row g-2 align-items-end">  
                                <div class="col-md-8">  
                                    <input type="text" name="roomName" class="form-control" placeholder="Enter room number..."  
                                           value="<%= request.getParameter("roomName") != null ? request.getParameter("roomName") : "" %>">  
                                </div>  
                                <div class="col-md-4 d-grid">  
                                    <button type="submit" class="btn btn-teal">  
                                        <i class="bi bi-search"></i> Search  
                                    </button>  
                                </div>  
                            </div>  
                        </form>  
                    </div>  

                    <div class="col-md-6">  
                        <form method="get" action="rooms" class="bg-white p-3 rounded shadow-sm">  
                            <input type="hidden" name="action" value="list" />  
                            <div class="row g-2 align-items-end">  
                                <div class="col-md-8">  
                                    <select name="blockID" class="form-select">  
                                        <option value="">All Blocks</option>  
                                        <% for (Block b : blocks) { %>  
                                        <option value="<%= b.getBlockID() %>" <%= String.valueOf(b.getBlockID()).equals(selectedBlockID) ? "selected" : "" %>>  
                                            <%= b.getBlockName() %>  
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

                <div class="card shadow-sm">  
                    <div class="card-body p-0">  
                        <table class="table table-hover align-middle mb-0">  
                           <thead>  
    <tr>
        <th style="width: 140px;"><i class="bi bi-image"></i>&nbsp;Image</th>
        <th style="width: 160px;"><i class="bi bi-door-closed"></i>&nbsp;Room</th>
        <th style="width: 160px;"><i class="bi bi-cash-stack"></i>&nbsp;Price</th>
        <th style="width: 160px;"><i class="bi bi-info-circle"></i>&nbsp;Status</th>
        <th class="text-center" style="width: 180px;"><i class="bi bi-gear"></i>&nbsp;Action</th>
    </tr>
</thead>
 
                            <tbody>  
                                <%  
                                    List<Room> list = (List<Room>) request.getAttribute("list");  
                                    if (list != null && !list.isEmpty()) {  
                                        for (Room r : list) {  
                                %>  
                                <tr>  
    <td>  
        <% if (r.getImagePath() != null && r.getImagePath().length > 0) { %>  
        <img src="data:image/jpeg;base64,<%= java.util.Base64.getEncoder().encodeToString(r.getImagePath()) %>"  
             alt="Room Image" class="room-img"  
             onerror="this.onerror=null;this.src='<%= ctx %>/Uploads/default.jpg';" />  
        <% } else { %>  
        <span class="text-muted">No image</span>  
        <% } %>  
    </td>  
    <td><strong><%= r.getRoomNumber() %></strong></td>  
    <td><%= String.format("%,.0f VND", r.getRentPrice()) %></td>  
    <td>  
        <span class="badge <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "badge-available" : "badge-occupied" %>">  
            <i class="bi <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "bi-check-circle" : "bi-x-circle" %>"></i>  
            <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "Available" : "Occupied" %>  
        </span>  
    </td>  
    <td class="text-center">
        <div class="action-buttons">
            <a href="rooms?action=view&id=<%= r.getRoomID() %>" class="btn-icon btn-view" title="View">
                <i class="bi bi-eye"></i>
            </a>
            <% if (!"Occupied".equalsIgnoreCase(r.getRoomStatus())) { %>
            <a href="rooms?action=edit&id=<%= r.getRoomID() %>" class="btn-icon btn-edit" title="Edit">
                <i class="bi bi-pencil"></i>
            </a>
            <button type="button" class="btn-icon btn-delete" data-bs-toggle="modal"
                    data-bs-target="#deleteModal<%= r.getRoomID() %>" title="Delete">
                <i class="bi bi-trash"></i>
            </button>
            <% } %>
        </div>

        <%-- Delete Modal --%>
        <% if (!"Occupied".equalsIgnoreCase(r.getRoomStatus())) { %>
        <div class="modal fade" id="deleteModal<%= r.getRoomID() %>" tabindex="-1" aria-labelledby="deleteModalLabel<%= r.getRoomID() %>" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title">Delete Confirmation</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete room <strong><%= r.getRoomNumber() %></strong>?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <a href="rooms?action=delete&id=<%= r.getRoomID() %>" class="btn btn-danger">Confirm Delete</a>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
    </td>  
</tr>
    
                                <% }} else { %>  
                                <tr>  
                                    <td colspan="6" class="text-center text-muted py-4">No rooms found.</td>  
                                </tr>  
                                <% } %>  
                            </tbody>  
                        </table>  
                    </div>  
                </div>  
            </div>  

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>  
        </body>  
    </html>
