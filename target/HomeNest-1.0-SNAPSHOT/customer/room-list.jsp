<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Room" %>
<%@ page import="model.Category" %>
<%@ page import="model.Block" %>




<%
    List<Room> roomList = (List<Room>) request.getAttribute("roomList");
    List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
    String selectedStatus = request.getParameter("status") != null ? request.getParameter("status") : "";
    String minPrice = request.getParameter("minPrice") != null ? request.getParameter("minPrice") : "";
    String maxPrice = request.getParameter("maxPrice") != null ? request.getParameter("maxPrice") : "";
    String minArea = request.getParameter("minArea") != null ? request.getParameter("minArea") : "";
    String maxArea = request.getParameter("maxArea") != null ? request.getParameter("maxArea") : "";
    String selectedCategoryId = request.getParameter("category") != null ? request.getParameter("category") : "";
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Room List</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet"/>
        <style>
            body {
                background-color: #f0fdf4;
            }
            .navbar {
                background-color: #ffffff;
                border-bottom: 1px solid #e0e0e0;
                padding: 12px 24px;
            }
            .room-card {
                border: 1px solid #dee2e6;
                border-radius: 12px;
                background-color: #ffffff;
                box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                overflow: hidden;
                transition: transform 0.3s ease;
                display: flex;
                flex-direction: column;
                height: 100%;
            }
            .room-card:hover {
                transform: translateY(-5px);
            }
            .room-img {
                width: 100%;
                height: 200px;
                object-fit: cover;
                border-bottom: 1px solid #ddd;
            }
            .room-info {
                padding: 20px;
                flex-grow: 1;
            }
            .room-info p {
                font-size: 0.95rem;
                margin-bottom: 0.4rem;
                color: #555;
            }
            .status-available {
                color: #28a745;
                font-weight: 600;
            }
            .status-occupied {
                color: #dc3545;
                font-weight: 600;
            }
            .price {
                font-size: 1.2rem;
                font-weight: bold;
                color: #d10000;
            }
            .btn-warning {
                background-color: #ffc107;
                border: none;
            }
            .btn-warning:hover {
                background-color: #e0a800;
            }
            .pagination .page-link {
                color: #198754;
                border-color: #198754;
            }
            .pagination .page-link:hover {
                background-color: #198754;
                color: white;
            }
            .pagination .active .page-link {
                background-color: #198754;
                color: white;
                border-color: #198754;
            }
        </style>
    </head>
    <body style="padding-top: 100px; overflow-x: hidden;">

        <!-- ===== NAVBAR ===== -->
        <nav class="navbar navbar-light bg-white shadow-sm fixed-top px-4 py-2">
            <div class="container-fluid d-flex justify-content-between align-items-center">
                <!-- Logo -->
                <span class="fw-bold fs-4 text-success">HomeNest</span>

                <!-- Dropdown + Filter -->
                <form method="get" action="room-list" class="d-flex align-items-center gap-2 mb-0">
                    <select class="form-select border-success" name="category" onchange="this.form.submit()" style="min-width: 400px;">
                        <option value="">All</option>
                        <%
                            int selectedCategory = -1;
                            try {
                                selectedCategory = Integer.parseInt(selectedCategoryId);
                            } catch (Exception ignored) {}
                            if (categoryList != null) {
                                for (Category c : categoryList) {
                        %>
                        <option value="<%= c.getCategoriesID()%>" <%= (c.getCategoriesID() == selectedCategory) ? "selected" : ""%>>
                            <%= c.getCategoriesName()%>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
                    <button class="btn btn-outline-success btn-sm" type="button" onclick="toggleFilter()">Filter</button>
                </form>

                <!-- Include phần người dùng -->
                <jsp:include page="../WEB-INF/include/header.jsp"/>
            </div>
        </nav>

        <!-- ==== Overlay + Filter Popup bạn có thể giữ nguyên ==== -->
        <!-- (Bỏ qua phần filter popup trong đoạn này nếu bạn không thay đổi) -->

        <!-- ==== Room List ==== -->
        <% if (roomList != null && !roomList.isEmpty()) { %>
        <div class="row g-4 px-4 px-md-5">
            <% for (Room r : roomList) { %>
            <div class="col-md-4">
                <div class="room-card">
                    <img src="<%= request.getContextPath()%>/<%= r.getImagePath() != null ? r.getImagePath() : "uploads/default.jpg"%>"
                         onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/uploads/default.jpg';"
                         class="room-img" alt="Room Image"/>
                    <div class="p-3 room-info">
                        <h5 class="fw-semibold mb-2"><%= r.getRoomNumber() %></h5>
                        <%
                            String blockName = "N/A";
                            List<Block> blockList = (List<Block>) request.getAttribute("blockList");
                            if (blockList != null) {
                                for (Block b : blockList) {
                                    if (b.getBlockID() == r.getBlockID()) {
                                        blockName = b.getBlockName(); break;
                                    }
                                }
                            }
                        %>
                        <p><i class="bi bi-diagram-3"></i> Block: <%= blockName %></p>
                        <p class="text-danger fw-semibold"><i class="bi bi-arrows-fullscreen me-1"></i> <%= r.getArea() %> m²</p>
                        <p><i class="bi bi-geo-alt-fill me-1"></i> Location: <%= r.getLocation() != null ? r.getLocation() : "N/A" %></p>
                        <p>
                            <i class="bi bi-door-closed"></i> Status:
                            <span class="<%= r.getRoomStatus().equalsIgnoreCase("Available") ? "status-available" : "status-occupied" %>">
                                <%= r.getRoomStatus() %>
                            </span>
                        </p>
                        <p class="price">Price: <span><%= String.format("%,.0f", r.getRentPrice()) %> ₫</span> / month</p>
                        <div class="text-end">
                            <a href="room-detail?id=<%= r.getRoomID() %>" class="btn btn-success fw-bold px-4 py-2">View details</a>
                        </div>
                        <%
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                            java.util.Date postedDate = r.getPostedDate();
                        %>
                        <% if (postedDate != null) { %>
                        <p class="text-muted fst-italic small mt-2 mb-0">Post: <%= sdf.format(postedDate) %></p>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        </div>

        <!-- ===== Pagination ===== -->
        <%
            Integer currentPage = (Integer) request.getAttribute("currentPage");
            Integer totalPages = (Integer) request.getAttribute("totalPages");
            if (currentPage == null) currentPage = 1;
            if (totalPages == null) totalPages = 1;
        %>
        <div class="d-flex justify-content-center my-4">
            <nav>
                <ul class="pagination">
                    <% if (currentPage > 1) { %>
                    <li class="page-item">
                        <a class="page-link" href="room-list?page=<%= currentPage - 1 %>">&laquo;</a>
                    </li>
                    <% } %>
                    <% for (int i = 1; i <= totalPages; i++) { %>
                    <li class="page-item <%= (i == currentPage) ? "active" : "" %>">
                        <a class="page-link" href="room-list?page=<%= i %>"><%= i %></a>
                    </li>
                    <% } %>
                    <% if (currentPage < totalPages) { %>
                    <li class="page-item">
                        <a class="page-link" href="room-list?page=<%= currentPage + 1 %>">&raquo;</a>
                    </li>
                    <% } %>
                </ul>
            </nav>
        </div>
        <% } else { %>
        <div class="alert alert-warning mt-4">No rooms available to display.</div>
        <% } %>

        <script>
            function toggleFilter() {
                document.getElementById('filterPopup').classList.toggle('d-none');
                document.getElementById('overlay').classList.toggle('d-none');
            }

            function selectPrice(btn, min, max) {
                document.getElementById("minPrice").value = min !== null ? min : '';
                document.getElementById("maxPrice").value = max !== null ? max : '';

                document.querySelectorAll('#priceGroup .btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
            }

            function clearPrice() {
                document.getElementById("minPrice").value = '';
                document.getElementById("maxPrice").value = '';
                document.querySelectorAll('#priceGroup .btn').forEach(b => b.classList.remove('active'));
            }
            function selectArea(btn, min, max) {
                document.getElementById("minArea").value = min !== null ? min : '';
                document.getElementById("maxArea").value = max !== null ? max : '';
                document.querySelectorAll('#areaGroup .btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
            }

            function clearArea() {
                document.getElementById("minArea").value = '';
                document.getElementById("maxArea").value = '';
                document.querySelectorAll('#areaGroup .btn').forEach(b => b.classList.remove('active'));
            }

            window.onload = function () {
                const min = document.getElementById("minPrice").value;
                const max = document.getElementById("maxPrice").value;
                document.querySelectorAll('#priceGroup .btn').forEach(btn => {
                    const btnMin = btn.getAttribute("data-min");
                    const btnMax = btn.getAttribute("data-max");
                    if (btnMin === min && btnMax === max) {
                        btn.classList.add("active");
                    }
                });

                const minArea = document.getElementById("minArea").value;
                const maxArea = document.getElementById("maxArea").value;
                document.querySelectorAll('#areaGroup .btn').forEach(btn => {
                    const btnMin = btn.getAttribute("data-min");
                    const btnMax = btn.getAttribute("data-max");
                    if (btnMin === minArea && btnMax === maxArea) {
                        btn.classList.add("active");
                    }
                });
            };
        </script>

    </body>
</html>