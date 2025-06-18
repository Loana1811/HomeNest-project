<%-- 
    Document   : room-list
    Created on : Jun 14, 2025, 6:55:45 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>

<%@ page import="model.Room" %>
<%@ page import="model.Category" %>


<%
    List<Room> roomList = (List<Room>) request.getAttribute("roomList");
    List<model.Category> categoryList = (List<model.Category>) request.getAttribute("categoryList");
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





            .room-card {
                border: 1px solid #ddd;
                border-radius: 15px;
                overflow: hidden;
                box-shadow: 0 4px 16px rgba(0,0,0,0.1);
                transition: 0.3s;
                background: #fff;
                min-height: 400px;
            }
            .room-card:hover {
                transform: translateY(-6px);
            }
            .room-img {
                width: 100%;
                height: 220px;
                object-fit: cover;
            }
            .price {
                font-size: 2.4rem;      /* ~36px */
                font-weight: 900;
                color: red;
                margin-top: 0.5rem;
            }
            .room-info p {
                margin-bottom: 3px;
                font-size: 16px;
                color: #444;
            }
            .status-available {
                color: green;
            }
            .status-occupied {
                color: red;
            }

            .filter-popup {
                position: fixed;
                top: 10%;
                left: 50%;
                transform: translateX(-50%);
                width: 90%;
                max-width: 900px;
                background: #fff;
                border-radius: 15px;
                z-index: 1000;
                padding: 20px;
                box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
            }
            .overlay {
                position: fixed;
                inset: 0;
                background: rgba(0, 0, 0, 0.5);
                z-index: 999;
            }
            .d-none {
                display: none !important;
            }
            .price-option-group .btn.active {
                background-color: #495057;
                color: white;
            }
            .area-option-group .btn.active {
                background-color: #495057;
                color: white;
            }
            footer .container-fluid {
                max-width: 100% !important;
            }
            .pagination .page-link {
                color: #198754; /* Bootstrap xanh lá */
                border-color: #198754;
            }

            .pagination .page-link:hover {
                background-color: #198754;
                color: white;
            }

            .pagination .active .page-link {
                background-color: #198754;
                border-color: #198754;
                color: white;
            }



            .select.form-select {
                background-color: white;
                border: 2px solid green;
                border-radius: 5px;
                padding: 6px;
                appearance: none;
                -webkit-appearance: none;
                -moz-appearance: none;
            }
        </style>



    </head>
    <body style="padding-top: 100px; overflow-x: hidden;">



        <nav class="navbar navbar-light bg-white shadow-sm fixed-top px-4 py-2">
            <div class="container-fluid d-flex justify-content-between align-items-center">

                <!-- Logo bên trái -->
                <div class="d-flex align-items-center">
                    <img src="<%= request.getContextPath()%>/images/logo.jpg" alt="HomeNest Logo" style="height: 70px;" class="mb-2 me-2">
                    <span class="fw-bold fs-4 text-success">HomeNest</span>
                </div>

                <!-- Dropdown Category ở giữa -->
                <div class="flex-grow-1 d-flex justify-content-center">
                    <form method="get" action="<%= request.getContextPath()%>/customer/room-list" class="mb-0">

                        <select class="form-select form-select-sm border-success" name="category" onchange="this.form.submit()" style="width: 250px;">


                            <option value="">All</option>
                            <%
                                int selectedCategory = -1;
                                try {
                                    selectedCategory = Integer.parseInt(selectedCategoryId);
                                } catch (Exception ignored) {
                                }
                                if (categoryList != null) {
                                    for (model.Category c : categoryList) {
                            %>
                            <option value="<%= c.getCategoriesID()%>" <%= (c.getCategoriesID() == selectedCategory) ? "selected" : ""%>>
                                <%= c.getCategoriesName()%>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </form>
                </div>

                <!-- Nút Filter bên phải -->
                <div>
                    <button class="btn btn-outline-success" onclick="toggleFilter()">Filter</button>

                </div>

            </div>
        </nav>

        <!-- Overlay -->
        <div id="overlay" class="overlay d-none" onclick="toggleFilter()"></div>

        <!-- Filter Popup -->
        <div id="filterPopup" class="filter-popup d-none">
            <form method="get" action="room-list">

                <h5 class="fw-bold mb-3">Room Filters</h5>
                <!-- Block -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Block</label><br/>
                    <div class="d-flex flex-wrap gap-2">
                        <input type="radio" class="btn-check" name="block" id="blockAll" value="" 
                               <%= request.getParameter("block") == null ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="blockAll">All</label>

                        <input type="radio" class="btn-check" name="block" id="blockA" value="1"
                               <%= "1".equals(request.getParameter("block")) ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="blockA">Block A</label>

                        <input type="radio" class="btn-check" name="block" id="blockB" value="2"
                               <%= "2".equals(request.getParameter("block")) ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="blockB">Block B</label>

                        <input type="radio" class="btn-check" name="block" id="blockC" value="3"
                               <%= "3".equals(request.getParameter("block")) ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="blockC">Block C</label>

                        <input type="radio" class="btn-check" name="block" id="blockD" value="4"
                               <%= "4".equals(request.getParameter("block")) ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="blockD">Block D</label>
                    </div>
                </div>

                <!-- Status -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Status</label><br/>
                    <div class="d-flex flex-wrap gap-2">
                        <input type="radio" class="btn-check" name="status" id="statusAll" value="" <%= selectedStatus.isEmpty() ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="statusAll">All</label>

                        <input type="radio" class="btn-check" name="status" id="statusAvailable" value="Available" <%= selectedStatus.equals("Available") ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="statusAvailable">Available</label>

                        <input type="radio" class="btn-check" name="status" id="statusOccupied" value="Occupied" <%= selectedStatus.equals("Occupied") ? "checked" : ""%>>
                        <label class="btn btn-outline-secondary rounded" for="statusOccupied">Occupied</label>
                    </div>
                </div>

                <!-- Price Range -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Price Range (VND)</label><br/>
                    <div class="d-flex flex-wrap gap-2 price-option-group" id="priceGroup">
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="0" data-max="1000000" onclick="selectPrice(this, 0, 1000000)">Under 1M</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="1000000" data-max="2000000" onclick="selectPrice(this, 1000000, 2000000)">1 - 2M</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="2000000" data-max="3000000" onclick="selectPrice(this, 2000000, 3000000)">2 - 3M</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="3000000" data-max="5000000" onclick="selectPrice(this, 3000000, 5000000)">3 - 5M</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="5000000" data-max="7000000" onclick="selectPrice(this, 5000000, 7000000)">5 - 7M</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="7000000" data-max="10000000" onclick="selectPrice(this, 7000000, 10000000)">7 - 10M</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="10000000" data-max="" onclick="selectPrice(this, 10000000, null)">Above 10M</button>
                        <button type="button" class="btn btn-outline-danger rounded" onclick="clearPrice()">Clear</button>
                    </div>
                </div>

                <!-- Area Range -->
                <div class="mb-3">
                    <label class="fw-semibold mb-2">Area Range (m²)</label><br/>
                    <div class="d-flex flex-wrap gap-2 area-option-group" id="areaGroup">
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="0" data-max="18" onclick="selectArea(this, 0, 18)">Under 18</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="18" data-max="20" onclick="selectArea(this, 18, 20)">18 - 20</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="20" data-max="25" onclick="selectArea(this, 20, 25)">20 - 25</button>
                        <button type="button" class="btn btn-outline-secondary rounded" data-min="25" data-max="" onclick="selectArea(this, 25, null)">Above 25</button>
                        <button type="button" class="btn btn-outline-danger rounded" onclick="clearArea()">Clear</button>
                    </div>
                </div>

                <input type="hidden" name="minArea" id="minArea" value="<%= minArea%>">
                <input type="hidden" name="maxArea" id="maxArea" value="<%= maxArea%>">



                <input type="hidden" name="minPrice" id="minPrice" value="<%= minPrice%>">
                <input type="hidden" name="maxPrice" id="maxPrice" value="<%= maxPrice%>">

                <div class="text-end mt-4">
                    <button type="submit" class="btn btn-primary">Apply</button>
                    <button type="button" class="btn btn-secondary ms-2" onclick="toggleFilter()">Cancel</button>
                </div>
            </form>
        </div>

        <% if (roomList != null && !roomList.isEmpty()) { %>
        <div class="row g-4 px-4 px-md-5">

            <% for (Room r : roomList) {%>
            <div class="col-md-4">
                <div class="room-card">
                    <img src="<%= request.getContextPath()%>/images/rooms/<%= r.getImagePath() != null ? r.getImagePath() : "room-default.jpg"%>"

                         onerror="this.onerror=null;this.src='images/rooms/room-default.jpg';"
                         class="room-img" alt="Room Image">
                    <div class="p-3 room-info">

                        <h5 class="fw-semibold mb-2">
                            <%= r.getRoomNumber()%>
                        </h5>



                        <p><i class="bi bi-diagram-3"></i> Block: <%= r.getBlockID()%></p>
                        <p class="text-danger fw-semibold">
                            <i class="bi bi-arrows-fullscreen me-1"></i> <%= r.getArea()%> m²
                        </p>



                        <p>
                            <i class="bi bi-door-closed"></i> Status:
                            <span class="<%= r.getRoomStatus().equalsIgnoreCase("Available") ? "status-available" : "status-occupied"%>">
                                <%= r.getRoomStatus()%>
                            </span>
                        </p>
                        <p class="price" style="font-size: 1.2rem;">
                            Price:
                            <span style="font-size: 1.5rem; font-weight: 600; color: red;">
                                <%= String.format("%,.0f", r.getRentPrice())%> ₫
                            </span> / month
                        </p>


                        <div class="text-end">
                            <a href="room-detail?id=<%= r.getRoomID()%>" class="btn btn-warning fw-bold px-4 py-2">View details</a>
                        </div>


                        <%
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                            java.util.Date postedDate = r.getPostedDate();
                        %>
                        <% if (postedDate != null) {%>
                        <p class="text-muted fst-italic small mt-2 mb-0">
                            Post: <%= sdf.format(postedDate)%>
                        </p>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        </div> <!-- Kết thúc .row -->

        <!-- ===== PHÂN TRANG ===== -->
        <%
            Integer currentPage = (Integer) request.getAttribute("currentPage");
            Integer totalPages = (Integer) request.getAttribute("totalPages");

            if (currentPage == null) {
                currentPage = 1;
            }
            if (totalPages == null)
                totalPages = 1;
        %>

        <div class="d-flex justify-content-center my-4">
            <nav>
                <ul class="pagination">
                    <% if (currentPage > 1) {%>
                    <li class="page-item">
                        <a class="page-link" href="room-list?page=<%= currentPage - 1%>">&laquo;</a>
                    </li>
                    <% } %>

                    <% for (int i = 1; i <= totalPages; i++) {%>
                    <li class="page-item <%= (i == currentPage) ? "active" : ""%>">
                        <a class="page-link" href="room-list?page=<%= i%>"><%= i%></a>
                    </li>
                    <% } %>

                    <% if (currentPage < totalPages) {%>
                    <li class="page-item">
                        <a class="page-link" href="room-list?page=<%= currentPage + 1%>">&raquo;</a>
                    </li>
                    <% } %>
                </ul>
            </nav>
        </div>

        <% } else { %>
        <div class="alert alert-warning mt-4">No rooms available to display.</div>
        <% }%>



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
        <!-- ====== FOOTER ====== -->
        <jsp:include page="footer.jsp" />
    </body>
</html>