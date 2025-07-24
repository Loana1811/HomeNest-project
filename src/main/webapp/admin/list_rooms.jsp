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
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Phòng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <style>
            :root {
                --primary-color: #1e3b8a; /* Vibrant navy */
                --secondary-color: #c7d2fe; /* Soft navy pastel */
                --accent-color: #2a4d69; /* Darker navy for depth */
                --text-color: #111827; /* Clean dark gray for text */
                --white: #ffffff; /* Pure white */
                --background-color: #f5f9fc; /* Nền nhạt hơn */
                --sidebar-color: #003459; /* Sidebar đậm hơn */
                --shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                --border-radius: 12px; /* Slightly larger for elegance */
                --success: #16a34a; /* Brighter green for success */
                --warning: #d97706; /* Amber for warning/pending */
                --danger: #b91c1c; /* Red for danger/logout */
                --light-gray: #f3f4f6; /* Clean background for hover */
                --success-bg: #d1fae5; /* Light green for success cards */
                --pending-bg: #ffedd5; /* Light amber for pending cards */
                --active-bg: #e0f2fe; /* Light blue for active cards */
                --table-header-bg: #1e3b8a; /* Màu nền th, dùng primary-color */
                --table-header-text: #ffffff; /* Màu chữ th */
            }
            body {
                background-color: var(--background-color);
                font-family: 'Segoe UI', sans-serif;
                color: var(--text-color);
                font-size: 1.1rem; /* Tăng kích thước chữ cơ bản */
            }
            .main-content {
                margin-left: 270px;
                padding: 5rem 1.5rem 0rem 2rem;
            }
            .btn-teal {
                background-color: var(--primary-color);
                color: var(--white);
                border-radius: 30px;
                font-size: 1.3rem; /* Tăng kích thước chữ nút Thêm Phòng, Tìm, Lọc */
                padding: 0.6rem 1.2rem; /* Tăng padding cho nút to hơn */
            }
            .btn-teal:hover {
                background-color: var(--accent-color);
            }
            .btn-outline-dark {
                font-size: 1.3rem; /* Tăng kích thước chữ nút Thêm Block */
                padding: 0.6rem 1.2rem; /* Tăng padding cho nút to hơn */
                border-radius: 30px;
            }
            .card-header {
                background-color: var(--secondary-color);
                font-weight: 600;
                font-size: 1.3rem; /* Tăng kích thước chữ card header */
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
                font-size: 1.1rem; /* Tăng kích thước chữ badge */
            }
            .badge-occupied {
                background-color: var(--light-gray);
                font-size: 1.1rem; /* Tăng kích thước chữ badge */
            }
            .sidebar {
                position: fixed;
                width: 285px;
/*                top: 5rem;*/
                left: 0;
                height: 100%;
                background-color: var(--sidebar-color);
                z-index: 10;
            }
           
            .table thead th {
                background-color: var(--table-header-bg);
                color: var(--table-header-text);
                font-size: 1.4rem; /* Tăng kích thước chữ th */
                padding: 1rem; /* Tăng padding cho th */
               
            }
            .table tbody td {
                font-size: 1.1rem; /* Tăng kích thước chữ td */
            }
            h2.text-dark {
                font-size: 2rem; /* Tăng kích thước chữ tiêu đề */
                color: var(--success); /* Đổi màu tiêu đề Quản lý Phòng thành xanh */
            }
            .btn-sm {
                font-size: 1.3rem; /* Tăng kích thước chữ cho nút tác vụ */
                padding: 0.7rem 1.2rem; /* Tăng padding để nút to hơn */
                border-radius: 20px; /* Tăng bo tròn cho nút tác vụ */
            }
        </style>
    </head>
    <body>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="text-dark">Quản lý Phòng</h2>
                <div>
                    <a href="rooms?action=new" class="btn btn-teal me-2"><i class="bi bi-plus-circle"></i> Thêm Phòng</a>
                    <a href="blocks?action=new" class="btn btn-outline-dark"><i class="bi bi-building-add"></i> Thêm Block</a>
                </div>
            </div>

            <div class="row g-3 align-items-end mb-4">
                <div class="col-md-6">
                    <form method="get" action="rooms" class="bg-white p-3 rounded shadow-sm">
                        <input type="hidden" name="action" value="search" />
                        <div class="row g-2 align-items-end">
                            <div class="col-md-8">
                                <input type="text" name="roomName" class="form-control" placeholder="Nhập số phòng..."
                                       value="<%= request.getParameter("roomName") != null ? request.getParameter("roomName") : "" %>">
                            </div>
                            <div class="col-md-4 d-grid">
                                <button type="submit" class="btn btn-teal">
                                    <i class="bi bi-search"></i> Tìm
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
                                    <option value="">Tất cả Block</option>
                                    <% for (Block b : blocks) { %>
                                    <option value="<%= b.getBlockID() %>" <%= String.valueOf(b.getBlockID()).equals(selectedBlockID) ? "selected" : "" %>>
                                        <%= b.getBlockName() %>
                                    </option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="col-md-4 d-grid">
                                <button type="submit" class="btn btn-teal">
                                    <i class="bi bi-funnel"></i> Lọc
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
                                <th>Ảnh</th>
                                <th><i class="bi bi-door-closed"></i> Phòng</th>
                                <th><i class="bi bi-cash-stack"></i> Giá</th>
                                <th><i class="bi bi-info-circle"></i> Trạng thái</th>
                                <th class="text-center">
                                    <i class="bi bi-three-dots-vertical"></i>
                                </th>
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
                                         alt="Ảnh phòng" class="room-img"
                                         onerror="this.onerror=null;this.src='<%= ctx %>/Uploads/default.jpg';" />
                                    <% } else { %>
                                    <span class="text-muted">Không có ảnh</span>
                                    <% } %>
                                </td>
                                <td><strong><%= r.getRoomNumber() %></strong></td>
                                <td><%= String.format("%,.0f VNĐ", r.getRentPrice()) %></td>
                                <td>
                                    <span class="badge <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "badge-available" : "badge-occupied" %>">
                                        <i class="bi <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "bi-check-circle" : "bi-x-circle" %>"></i>
                                        <%= "Available".equalsIgnoreCase(r.getRoomStatus()) ? "Trống" : "Đã thuê" %>
                                    </span>
                                </td>
                                <td class="text-center">
                                    <a href="rooms?action=view&id=<%= r.getRoomID() %>" class="btn btn-sm btn-info text-white" title="Xem chi tiết">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    <% if (!"Occupied".equalsIgnoreCase(r.getRoomStatus())) { %>
                                    <a href="rooms?action=edit&id=<%= r.getRoomID() %>" class="btn btn-sm btn-outline-primary" title="Chỉnh sửa">
                                        <i class="bi bi-pencil-square"></i>
                                    </a>
                                    <button type="button" class="btn btn-sm btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteModal<%= r.getRoomID() %>">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                    <div class="modal fade" id="deleteModal<%= r.getRoomID() %>" tabindex="-1" aria-labelledby="deleteModalLabel<%= r.getRoomID() %>" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content">
                                                <div class="modal-header bg-danger text-white">
                                                    <h5 class="modal-title">Xác nhận xóa</h5>
                                                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Đóng"></button>
                                                </div>
                                                <div class="modal-body">
                                                    Bạn có chắc chắn muốn xóa phòng <strong><%= r.getRoomNumber() %></strong> không?
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                    <a href="rooms?action=delete&id=<%= r.getRoomID() %>" class="btn btn-danger">Xác nhận xóa</a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <% } %>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="6" class="text-center text-muted py-4">Không có phòng nào.</td>
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