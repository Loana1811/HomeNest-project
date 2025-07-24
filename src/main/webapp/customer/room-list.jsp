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
        body {
    background-color: #d6e9f8; /* xanh nhạt hơn */
    min-height: 100vh;
    margin: 0;
}

.navbar {
    background-color: #e6f0fa; /* xanh nhạt */
    border-bottom: 1px solid #a1c1e9; /* xanh nhạt */
    padding: 12px 24px;
}

.room-card {
    border: 1px solid #a1c1e9; /* xanh nhạt */
    border-radius: 12px;
    background-color: #f3f8ff; /* xanh cực nhạt */
    box-shadow: 0 4px 12px rgba(61, 131, 250, 0.25); /* bóng xanh */
    overflow: hidden;
    transition: transform 0.3s ease;
    display: flex;
    flex-direction: column;
    height: 100%;
}

.room-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 18px rgba(61, 131, 250, 0.35);
}

.room-img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    border-bottom: 1px solid #a1c1e9; /* xanh nhạt */
}

.room-info {
    padding: 20px;
    flex-grow: 1;
}

.room-info p {
    font-size: 0.95rem;
    margin-bottom: 0.4rem;
    color: #2a4d8f; /* xanh đậm */
}

.status-available {
    color: #28a745; /* giữ xanh lá hoặc đổi xanh dương */
    font-weight: 600;
}

.status-occupied {
    color: #0066cc; /* xanh dương đậm thay cho đỏ */
    font-weight: 600;
}

.price {
    font-size: 1.2rem;
    font-weight: bold;
    color: #004085; /* xanh đậm */
}

.btn-warning {
    background-color: #74b9ff; /* xanh nhạt */
    border: none;
    color: #1d3557;
}

.btn-warning:hover {
    background-color: #4a90e2; /* xanh đậm hơn */
    color: white;
}

.pagination .page-link {
    color: #1d4ed8; /* xanh dương */
    border-color: #1d4ed8;
}

.pagination .page-link:hover {
    background-color: #1d4ed8;
    color: white;
}

.pagination .active .page-link {
    background-color: #1e40af;
    color: white;
    border-color: #1e40af;
}

.highlight-tag {
    background-color: #d0e7ff; /* xanh rất nhạt */
    color: #1e40af; /* xanh đậm */
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
    background: #f0f7ff; /* xanh cực nhạt */
    padding: 30px;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(61, 131, 250, 0.4);
    max-height: 90vh;
    overflow-y: auto;
}

.overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(61, 131, 250, 0.25); /* overlay xanh nhạt */
    z-index: 1040;
}

        </style>
    </head>
    <body style="padding-top: 100px; overflow-x: hidden;">
        <!-- Navbar -->
        <nav class="navbar navbar-light bg-white shadow-sm fixed-top px-4 py-2">
            <div class="container-fluid d-flex justify-content-between align-items-center">
                <!-- Logo -->
              <i class="bi bi-house-door-fill fs-4 text-primary"></i>
            <span class="fs-5 fw-bold text-primary"></span>
                <!-- Dropdown + Filter -->
                <form method="get" action="<%= request.getContextPath() %>/customer/room-list" class="d-flex align-items-center gap-2 mb-0">
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
                        <option value="<%= c.getCategoriesID() %>" <%= c.getCategoriesID() == selectedCategory ? "selected" : "" %>>
                            <%= c.getCategoriesName() %>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
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
                <h5 class="fw-bold mb-3">Room Filters</h5>

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
                        <button type="button" class="btn btn-outline-success rounded" data-min="0" data-max="1000000" onclick="selectPrice(this, 0, 1000000)">Under 1M</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="1000000" data-max="2000000" onclick="selectPrice(this, 1000000, 2000000)">1 - 2M</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="2000000" data-max="3000000" onclick="selectPrice(this, 2000000, 3000000)">2 - 3M</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="3000000" data-max="5000000" onclick="selectPrice(this, 3000000, 5000000)">3 - 5M</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="5000000" data-max="7000000" onclick="selectPrice(this, 5000000, 7000000)">5 - 7M</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="7000000" data-max="10000000" onclick="selectPrice(this, 7000000, 10000000)">7 - 10M</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="10000000" data-max="" onclick="selectPrice(this, 10000000, null)">Above 10M</button>
                        <button type="button" class="btn btn-outline-success rounded" onclick="clearPrice()">Clear</button>
                    </div>
                </div>

                <!-- Area Range -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Area Range (m²)</label><br/>
                    <div class="d-flex flex-wrap gap-2 area-option-group" id="areaGroup">
                        <button type="button" class="btn btn-outline-success rounded" data-min="0" data-max="18" onclick="selectArea(this, 0, 18)">Under 18</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="18" data-max="20" onclick="selectArea(this, 18, 20)">18 - 20</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="20" data-max="25" onclick="selectArea(this, 20, 25)">20 - 25</button>
                        <button type="button" class="btn btn-outline-success rounded" data-min="25" data-max="" onclick="selectArea(this, 25, null)">Above 25</button>
                        <button type="button" class="btn btn-outline-success rounded" onclick="clearArea()">Clear</button>
                    </div>
                </div>

                <!-- Hidden Inputs -->
                <input type="hidden" name="minArea" id="minArea" value="<%= minArea %>">
                <input type="hidden" name="maxArea" id="maxArea" value="<%= maxArea %>">
                <input type="hidden" name="minPrice" id="minPrice" value="<%= minPrice %>">
                <input type="hidden" name="maxPrice" id="maxPrice" value="<%= maxPrice %>">

                <!-- Location Dropdown -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Location</label>
                    <select class="form-select border-success" name="location">
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
                    <button type="submit" class="btn btn-primary">Apply</button>
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
                <img src="<%= request.getContextPath() %>/room-image?id=<%= r.getRoomID() %>" style="max-width: 100%; height: 280px;" alt="Room Image"/>


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
                        <p class="text-danger fw-semibold"><i class="bi bi-arrows-fullscreen me-1"></i> <%= r.getArea() %> m²</p>
                        <p><i class="bi bi-geo-alt-fill me-1"></i> Location: <%= r.getLocation() != null ? r.getLocation() : "N/A" %></p>
                        <p>
                            <i class="bi bi-door-closed"></i> Status:
                            <span class="<%= r.getRoomStatus().equalsIgnoreCase("Available") ? "status-available" : "status-occupied" %>">
                                <%= r.getRoomStatus() %>
                            </span>
                        </p>
                        <p class="price">Price: <span style="font-size: 1.5rem; font-weight: 600; color: red;"><%= String.format("%,.0f", r.getRentPrice()) %> ₫</span> / month</p>
                        <div class="text-end">
                            <a href="<%= request.getContextPath() %>/customer/room-detail?id=<%= r.getRoomID() %>" class="btn btn-success fw-bold px-4 py-2">View details</a>
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
        <div class="alert alert-warning mt-4">No rooms available to display.</div>
        <% } %>

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