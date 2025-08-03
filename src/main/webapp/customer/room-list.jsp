<%-- 
    Document   : room-list
    Created on : Jun 14, 2025, 6:55:45 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Room" %>
<%@ page import="model.Category" %>
<%@ page import="model.Block" %>

<%
    List<Room> roomList = (List<Room>) request.getAttribute("roomList");
    List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
    List<Block> blockList = (List<Block>) request.getAttribute("blockList");
    List<String> locationList = (List<String>) request.getAttribute("locationList");
    String selectedStatus = request.getParameter("status") != null ? request.getParameter("status") : "";
    String minPrice = request.getParameter("minPrice") != null ? request.getParameter("minPrice") : "";
    String maxPrice = request.getParameter("maxPrice") != null ? request.getParameter("maxPrice") : "";
    String minArea = request.getParameter("minArea") != null ? request.getParameter("minArea") : "";
    String maxArea = request.getParameter("maxArea") != null ? request.getParameter("maxArea") : "";
    String selectedCategoryId = request.getParameter("category") != null ? request.getParameter("category") : "";
    String selectedBlock = request.getParameter("block") != null ? request.getParameter("block") : "";
    String selectedLocation = request.getParameter("location") != null ? request.getParameter("location") : "";
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Room List</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet"/>
        <style>
            :root {
                --primary-color: #1e3b8a; /* Navy blue */
                --pastel-blue: #b3d4fc; /* Soft pastel blue for gradient */
                --accent-color: #2a4d69;
                --text-color: #111827;
                --white: #ffffff;
                --background-color: #f5f9fc;
                --sidebar-color: #003459;
                --shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                --border-radius: 12px;
                --success: #1e3b8a; /* Navy blue for buttons */
                --warning: #d97706;
                --danger: #b91c1c;
                --light-gray: #f3f4f6;
                --success-bg: #e0f2fe;
                --pending-bg: #ffedd5;
                --active-bg: #e0f2fe;
                --table-header-bg: #1e3b8a;
                --table-header-text: #ffffff;
            }

            body {
                background-color: var(--background-color);
                min-height: 100vh;
                margin: 0;
                font-family: 'Segoe UI', sans-serif;
                color: var(--text-color);
                font-size: 1.1rem;
                padding-top: 100px;
                overflow-x: hidden;
            }

            .navbar {
                background: linear-gradient(to right, var(--primary-color), var(--pastel-blue));
                border-bottom: 1px solid var(--light-gray);
                padding: 12px 24px;
                position: fixed;
                top: 0;
                width: 100%;
                z-index: 1030;
            }

            .room-card {
                border: 1px solid var(--light-gray);
                border-radius: var(--border-radius);
                background-color: var(--white);
                box-shadow: var(--shadow);
                overflow: hidden;
                transition: transform 0.3s ease;
                display: flex;
                flex-direction: column;
                height: 100%;
            }

            .room-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
            }

            .room-img {
                width: 100%;
                height: 280px;
                object-fit: cover;
                border-bottom: 1px solid var(--light-gray);
            }

            .room-info {
                padding: 20px;
                flex-grow: 1;
            }

            .room-info p {
                font-size: 0.95rem;
                margin-bottom: 0.4rem;
                color: var(--text-color);
            }

            .status-available {
                color: var(--success);
                font-weight: 600;
            }

            .status-occupied {
                color: var(--light-gray);
                font-weight: 600;
            }

            .price {
                font-size: 1.2rem;
                font-weight: bold;
                color: var(--primary-color);
            }

            .btn-warning {
                background-color: var(--pastel-blue);
                border: none;
                color: var(--text-color);
                border-radius: 20px;
                font-size: 1rem;
                padding: 0.7rem 1.2rem;
            }

            .btn-warning:hover {
                background-color: var(--accent-color);
                color: var(--white);
            }

            .pagination .page-link {
                color: var(--primary-color);
                border-color: var(--light-gray);
            }

            .pagination .page-link:hover {
                background-color: var(--pastel-blue);
                color: var(--text-color);
            }

            .pagination .active .page-link {
                background-color: var(--primary-color);
                color: var(--white);
                border-color: var(--primary-color);
            }

            .highlight-tag {
                background-color: var(--active-bg);
                color: var(--primary-color);
                padding: 8px 12px;
                border-radius: 15px;
                display: inline-block;
                max-width: 120px;
                word-wrap: break-word;
                text-align: center;
                font-size: 14px;
            }

            .filter-popup {
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                z-index: 1050;
                width: 500px;
                background: var(--white);
                padding: 30px;
                border-radius: var(--border-radius);
                box-shadow: var(--shadow);
                max-height: 90vh;
                overflow-y: auto;
            }

            .overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100vw;
                height: 100vh;
                background: rgba(0, 0, 0, 0.25);
                z-index: 1040;
            }

            .btn-outline-success {
                border-color: var(--success);
                color: var(--success);
                border-radius: 20px;
                font-size: 1rem;
                padding: 0.7rem 4rem;
            }

            .btn-outline-success:hover {
                background-color: var(--success-bg);
                color: var(--success);
            }

            .btn-check:checked + .btn-outline-success {
                background-color: var(--success);
                color: var(--white);
            }

            .form-select {
                border-color: var(--light-gray);
                font-size: 1rem;
            }

            .form-select:focus {
                border-color: var(--primary-color);
                box-shadow: 0 0 0 0.2rem rgba(30, 59, 138, 0.25);
            }

            .btn-navy {
                background-color: var(--primary-color);
                color: var(--white);
                border-radius: 20px;
                font-size: 1rem;
                padding: 0.7rem 1.2rem;
            }

            .btn-navy:hover {
                background-color: var(--accent-color);
                color: var(--white);
            }



            .btn-outline-navy {
                border: 1px solid var(--primary-color);
                color: var(--primary-color);
                background-color: transparent;
                border-radius: 20px;
                padding: 0.6rem 1.2rem;
                font-size: 1rem;
                transition: 0.3s ease;
            }

            .btn-outline-navy:hover {
                background-color: #e6f0ff; /* light navy bg */
                color: var(--primary-color);
            }

            .btn-outline-navy.active,
            .btn-outline-navy:focus,
            .btn-outline-navy:active {
                background-color: var(--primary-color);
                color: white;
                border-color: var(--primary-color);
            }

        </style>
    </head>
    <body>
        <!-- Navbar -->
        <nav class="navbar navbar-light shadow-sm fixed-top px-4 py-2">
            <div class="container-fluid d-flex justify-content-between align-items-center">
                <!-- Logo -->
                <a href="#" onclick="scrollToTop()" title="Go to top">
    <i class="bi bi-house-door-fill"></i>
</a>

                <!--                <span class="fs-5 fw-bold" style="color: var(--white);">Room List</span>-->
                <!-- Dropdown + Filter -->
                <form method="get" action="<%= request.getContextPath() %>/customer/room-list" class="d-flex align-items-center gap-2 mb-0">
                    <!--                    <select class="form-select" name="category" onchange="this.form.submit()" style="min-width: 400px;">
                                            <option value="">All</option>
                    <%
                        int selectedCategory = -1;
                        try {
                            selectedCategory = Integer.parseInt(selectedCategoryId);
                        } catch (Exception ignored) {}
                        if (categoryList != null) {
                            for (Category c : categoryList) {
                    %>
                    <option value="<%= c.getCategoriesID() %>" <%= c.getCategoriesID() == selectedCategory ? "selected" : "" %>>
                    <%= c.getCategoriesName() %>
                </option>
                    <%
                            }
                        }
                    %>
                </select>-->
                    <button class="btn btn-outline-success btn-sm" type="button" onclick="toggleFilter()">Filter</button>
                </form>

                <!-- Include User Info -->
                <%@include file="/WEB-INF/include/header_cus.jsp" %>
            </div>
        </nav>

        <!-- Overlay -->
        <div id="overlay" class="overlay d-none" onclick="toggleFilter()"></div>

        <!-- Filter Popup -->
        <div id="filterPopup" class="filter-popup d-none">
            <form method="get" action="<%= request.getContextPath() %>/customer/room-list">
                <h5 class="fw-bold mb-3" style="color: var(--primary-color);">Room Filters</h5>

                <!-- Block Filter -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Block</label><br/>
                    <div class="d-flex flex-wrap gap-2">
                        <input type="radio" class="btn-check" name="block" id="blockAll" value="" <%= selectedBlock.isEmpty() ? "checked" : "" %>>
                        <label class="btn btn-outline-success rounded" for="blockAll">All</label>
                        <%
                            if (blockList != null) {
                                for (Block b : blockList) {
                                    String blockIdStr = String.valueOf(b.getBlockID());
                        %>
                        <input type="radio" class="btn-check" name="block" id="block<%= b.getBlockID() %>" value="<%= blockIdStr %>" <%= blockIdStr.equals(selectedBlock) ? "checked" : "" %>>
                        <label class="btn btn-outline-success rounded" for="block<%= b.getBlockID() %>"><%= b.getBlockName() %></label>
                        <%
                                }
                            }
                        %>
                    </div>
                </div>

                <!-- Status -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Status</label><br/>
                    <div class="d-flex flex-wrap gap-2">
                        <input type="radio" class="btn-check" name="status" id="statusAll" value="" <%= selectedStatus.isEmpty() ? "checked" : "" %>>
                        <label class="btn btn-outline-success rounded" for="statusAll">All</label>
                        <input type="radio" class="btn-check" name="status" id="statusAvailable" value="Available" <%= selectedStatus.equals("Available") ? "checked" : "" %>>
                        <label class="btn btn-outline-success rounded" for="statusAvailable">Available</label>
                        <input type="radio" class="btn-check" name="status" id="statusOccupied" value="Occupied" <%= selectedStatus.equals("Occupied") ? "checked" : "" %>>
                        <label class="btn btn-outline-success rounded" for="statusOccupied">Occupied</label>
                    </div>
                </div>

                <!-- Price Range -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Price Range (VND)</label><br/>
                    <div class="d-flex flex-wrap gap-2 price-option-group" id="priceGroup">
                        <button type="button" class="btn btn-outline-navy rounded" data-min="0" data-max="1000000" onclick="selectPrice(this, 0, 1000000)">Under 1M</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="1000000" data-max="2000000" onclick="selectPrice(this, 1000000, 2000000)">1 - 2M</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="2000000" data-max="3000000" onclick="selectPrice(this, 2000000, 3000000)">2 - 3M</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="3000000" data-max="5000000" onclick="selectPrice(this, 3000000, 5000000)">3 - 5M</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="5000000" data-max="7000000" onclick="selectPrice(this, 5000000, 7000000)">5 - 7M</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="7000000" data-max="10000000" onclick="selectPrice(this, 7000000, 10000000)">7 - 10M</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="10000000" data-max="" onclick="selectPrice(this, 10000000, null)">Above 10M</button>
                        <button type="button" class="btn btn-outline-navy rounded" onclick="clearPrice()">Clear</button>
                    </div>
                </div>

                <!-- Area Range -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Area Range (m²)</label><br/>
                    <div class="d-flex flex-wrap gap-2 area-option-group" id="areaGroup">
                        <button type="button" class="btn btn-outline-navy rounded" data-min="0" data-max="18" onclick="selectArea(this, 0, 18)">Under 18</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="18" data-max="20" onclick="selectArea(this, 18, 20)">18 - 20</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="20" data-max="25" onclick="selectArea(this, 20, 25)">20 - 25</button>
                        <button type="button" class="btn btn-outline-navy rounded" data-min="25" data-max="" onclick="selectArea(this, 25, null)">Above 25</button>
                        <button type="button" class="btn btn-outline-navy rounded" onclick="clearArea()">Clear</button>
                    </div>
                    <input type="hidden" name="minArea" id="minArea">
                    <input type="hidden" name="maxArea" id="maxArea">
                </div>

                <input type="hidden" name="minPrice" id="minPrice" value="<%= minPrice %>">
                <input type="hidden" name="maxPrice" id="maxPrice" value="<%= maxPrice %>">

                <!-- Location Dropdown -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Location</label>
                    <select class="form-select" name="location">
                        <option value="">All</option>
                        <% if (locationList != null) {
                            for (String loc : locationList) {
                        %>
                        <option value="<%= loc %>" <%= loc.equals(selectedLocation) ? "selected" : "" %>>
                            <%= loc %>
                        </option>
                        <% } } %>
                    </select>
                </div>

                <!-- Form Buttons -->
                <div class="text-end mt-4">
                    <button type="submit" class="btn btn-navy">Apply</button>
                    <button type="button" class="btn btn-secondary ms-2" onclick="toggleFilter()">Cancel</button>
                </div>
            </form>
        </div>

        <!-- Room List -->
        <% if (roomList != null && !roomList.isEmpty()) { %>
        <div class="row g-4 px-4 px-md-5">
            <% for (Room r : roomList) { %>
            <div class="col-md-4">
                <div class="room-card">
                    <img src="<%= request.getContextPath() %>/room-image?id=<%= r.getRoomID() %>" class="room-img" alt="Room Image"/>
                    <div class="p-3 room-info">
                        <h5 class="fw-semibold mb-2"><%= r.getRoomNumber() %></h5>
                        <%
                            String blockName = "N/A";
                            if (blockList != null) {
                                for (Block b : blockList) {
                                    if (b.getBlockID() == r.getBlockID()) {
                                        blockName = b.getBlockName();
                                        break;
                                    }
                                }
                            }
                        %>
                        <p><i class="bi bi-diagram-3"></i> Block: <%= blockName %></p>
                        <p style="color: var(--danger);"><i class="bi bi-arrows-fullscreen me-1"></i> <%= r.getArea() %> m²</p>
                        <p><i class="bi bi-geo-alt-fill me-1"></i> Location: <%= r.getLocation() != null ? r.getLocation() : "N/A" %></p>
                        <p>
                            <i class="bi bi-door-closed"></i> Status:
                            <span class="<%= r.getRoomStatus().equalsIgnoreCase("Available") ? "status-available" : "status-occupied" %>">
                                <%= r.getRoomStatus() %>
                            </span>
                        </p>
                        <p class="price">Price: <span style="font-size: 1.5rem; font-weight: 600; color: var(--danger);"><%= String.format("%,.0f", r.getRentPrice()) %> VND</span> / month</p>
                        <div class="text-end">
                            <a href="<%= request.getContextPath() %>/customer/room-detail?id=<%= r.getRoomID() %>" class="btn btn-navy fw-bold px-4 py-2">View details</a>
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

        <!-- Pagination -->
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
                        <a class="page-link" href="<%= request.getContextPath() %>/customer/room-list?page=<%= currentPage - 1 %>">«</a>
                    </li>
                    <% } %>
                    <% for (int i = 1; i <= totalPages; i++) { %>
                    <li class="page-item <%= i == currentPage ? "active" : "" %>">
                        <a class="page-link" href="<%= request.getContextPath() %>/customer/room-list?page=<%= i %>"><%= i %></a>
                    </li>
                    <% } %>
                    <% if (currentPage < totalPages) { %>
                    <li class="page-item">
                        <a class="page-link" href="<%= request.getContextPath() %>/customer/room-list?page=<%= currentPage + 1 %>">»</a>
                    </li>
                    <% } %>
                </ul>
            </nav>
        </div>
        <% } else { %>
        <div class="alert alert-warning mt-4" style="background-color: var(--pending-bg); color: var(--warning);">No rooms available to display.</div>
        <% } %>
        <%@include file = "/WEB-INF/inclu/footer.jsp" %>
        <!-- JavaScript -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
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