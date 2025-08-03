<%--
    Document   : room-detail
    Created on : Jun 14, 2025, 3:45:45 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Room, model.UtilityType, model.Block" %>
<%@ page import="java.text.SimpleDateFormat, java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>

<%
    Room room = (Room) request.getAttribute("room");
    List<Room> featuredRooms = (List<Room>) request.getAttribute("featuredRooms");
    List<UtilityType> utilityTypes = (List<UtilityType>) request.getAttribute("utilityTypes");
    List<Block> blockList = (List<Block>) request.getAttribute("blockList");
    Boolean alreadyRequested = (Boolean) request.getAttribute("alreadyRequested");
    Boolean hasAnyPendingRequest = (Boolean) request.getAttribute("hasAnyPendingRequest");
    Boolean showSuccess = (Boolean) request.getAttribute("showSuccess");

    if (alreadyRequested == null) alreadyRequested = false;
    if (hasAnyPendingRequest == null) hasAnyPendingRequest = false;
    if (showSuccess == null) showSuccess = false;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String postDateStr = (room != null && room.getPostedDate() != null) ? sdf.format(room.getPostedDate()) : "N/A";
    DecimalFormat formatter = new DecimalFormat("#,###");
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Room Details</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet"/>
        <style>
            :root {
                --primary-color: #1e3b8a; /* Navy blue */
                --pastel-blue: #b3d4fc; /* Soft pastel blue for gradient */
                --accent-color: #2a4d69;
                --text-color: #111827;
                --white: #ffffff;
                --background-color: #f9fafb; /* Light gray-white background */
                --sidebar-color: #003459;
                --shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                --border-radius: 12px;
                --success: #1e3b8a; /* Navy blue for buttons */
                --warning: #d97706;
                --danger: #b91c1c;
                --light-gray: #f3f4f6;
                --pending-bg: #ffedd5;
                --active-bg: #f9fafb; /* Use light gray-white for active elements */
                --table-header-bg: #1e3b8a;
                --table-header-text: #ffffff;
                --divider-color: #d1d5db; /* Light black/gray for divider */
            }

            body {
                background-color: var(--background-color);
                min-height: 100vh;
                margin: 0;
                font-family: 'Segoe UI', sans-serif;
                color: var(--text-color);
                font-size: 1.1rem;
                padding-top: 50px;
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

            .navbar-custom .bi,
            .navbar-custom .nav-link,
            .navbar-custom .navbar-brand {
                color: var(--white) !important;
            }

            .navbar-custom .form-control {
                background-color: var(--white);
                border-color: var(--primary-color);
            }

            .navbar-custom .form-control::placeholder {
                color: #6b7280;
            }

            .room-image {
                width: 100%;
                height: 100%;
                object-fit: cover;
                /*                border-radius: 10px;*/
            }

            .room-card {
                border: none;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                /*                border-radius: 15px;*/
            }

            .card-header-custom {
                background-color: var(--primary-color);
                color: var(--white);
                font-weight: bold;
                font-size: 1.25rem;
                border-top-left-radius: 15px;
                border-top-right-radius: 15px;
            }

            .info-label {
                font-weight: bold;
                color: var(--primary-color);
            }

            .btn-request {
                background-color: var(--accent-color);
                color: var(--white);
                font-weight: bold;
                border: none;
            }

            .btn-request:hover {
                background-color: var(--background-color);
                color: var(--text-color);
                border-color: var(--background-color);
            }

            .utility-icon {
                color: #000000;
                font-size: 1.2rem;
            }

            .btn-custom {
                padding: 0.5rem 0.8rem;
                font-size: 1rem;
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

            .button-group {
                display: flex;
                gap: 1rem;
                flex-wrap: wrap;
            }

            .btn-success {
                background-color: var(--success);
                color: var(--white);
            }

            .btn-contact,
            .btn-request {
                flex: 1;
                min-width: 150px;
                padding: 0.75rem;
                font-size: 1rem;
                font-weight: bold;
                border-radius: 8px;
                text-align: center;
                text-decoration: none;
                transition: background-color 0.3s ease;
            }

            .btn-contact {
                background-color: var(--success);
                color: var(--white);
                border: 2px solid var(--success);
            }

            .btn-contact:hover {
                background-color: var(--background-color);
                color: var(--text-color);
                border-color: var(--background-color);
            }

            .btn-request {
                background-color: var(--accent-color);
                color: var(--white);
                border: 2px solid var(--accent-color);
            }

            .btn-request:hover {
                background-color: var(--background-color);
                color: var(--text-color);
                border-color: var(--background-color);
            }

            .btn-back {
                background-color: transparent;
                color: var(--white);
                font-weight: bold;
                padding: 0.5rem 1rem;
                font-size: 1rem;
                border-radius: 25px;
                text-decoration: none;
                transition: background-color 0.3s ease, color 0.3s ease;
            }

            .btn-back:hover {
                background-color: var(--background-color);
                color: var(--text-color);
                border-color: var(--background-color);
            }

            .btn-back i {
                margin-right: 0.5rem;
            }

            .room-number {
                color: var(--primary-color);
                font-weight: bold;
                font-size: 2rem;
                text-align: center;
                padding-top: 0.5rem;
                padding: 0.8rem;
                margin-top: 1rem;
            }

            .right-content {
                background-color: var(--background-color);
                border-radius: 15px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
                padding: 1.5rem;
                display: flex;
                flex-direction: column;
                gap: 1.5rem;
            }

            .right-content h5,
            .right-content p,
            .right-content .room-description {
                color: var(--primary-color);
            }

            .room-price {
                font-size: 1.8rem !important;
                font-weight: bold;
                color: red !important;
                display: block;
                text-align: center;
            }

            .content-container {
                background-color: var(--white);
                border-radius: 0;
                box-shadow: none;
                padding: 0;
            }

            .content-row {
                display: flex;
                flex-wrap: wrap;
                gap: 0;
            }

            .image-column,
            .info-column {
                flex: 1;
                min-width: 300px;
                padding: 0 0.5rem;
                padding-bottom: 1rem;
            }

            .image-column .room-card {
                margin: 0;
                border: none;
                box-shadow: none;
            }

            .room-image {
                min-height: 300px;
            }

            .container {
                padding: 0;
                width: 100%;
            }

            main {
                margin: 0;
            }

            .info-details {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
                background-color: var(--white);
                padding: 0.8rem;
                border-radius: 8px;
                font-size: 0.9rem;
            }

            .room-info-column,
            .utility-info-column {
                flex: 1;
                min-width: 200px;
                color: #000000;
            }

            .room-info-column h5,
            .utility-info-column h5 {
                font-size: 1.1rem;
                color: #000000;
            }

            .room-info-column p,
            .utility-info-column p {
                color: #000000;
            }

            .room-info-column {
                padding-right: 1rem;
                border-right: 1px solid var(--divider-color);
            }

            .utility-info-column {
                padding-left: 1rem;
            }

            .contact-logo {
                font-size: 2rem;
                color: var(--primary-color);
                margin-bottom: 1rem;
            }

            .contact-avatar {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                object-fit: cover;
                border: 2px solid var(--primary-color);
            }

            footer {
                margin-top: 0;
                padding-top: 0;
                background-color: var(--background-color);
            }

            @media (max-width: 768px) {
                .room-info-column {
                    border-right: none;
                    border-bottom: 1px solid var(--divider-color);
                    padding-right: 0;
                    padding-bottom: 1rem;
                }
                .utility-info-column {
                    padding-left: 0;
                    padding-top: 1rem;
                }
                .content-row {
                    flex-direction: column;
                    padding: 0 1rem;
                }
                .image-column,
                .info-column {
                    padding: 0;
                }
                .room-image {
                    min-height: 200px;
                }
                .info-details {
                    font-size: 0.85rem;
                }
                .room-info-column h5,
                .utility-info-column h5 {
                    font-size: 1rem;
                }
            }
        </style>
    </head>
    <body>

        <nav class="navbar navbar-light shadow-sm fixed-top px-4 py-2">
            <div class="container-fluid d-flex justify-content-between align-items-center">
                <!-- Back Button -->
                <a href="<%= request.getContextPath() %>/customer/room-list" class="btn btn-back">
                    <i class="bi bi-arrow-left-circle"></i> Back
                </a>

                <!-- User info -->
                <%@include file="/WEB-INF/include/header_cus.jsp" %>
            </div>
        </nav>



        <main class="flex-grow-1">
            <% if (room != null) { %>
            <div class="content-container">
                <!-- Room Number -->
                <h3 class="room-number">Room <%= room.getRoomNumber() %></h3>

                <!-- Content Row (Image and Info) -->
                <div class="content-row">
                    <!-- Image Column -->
                    <div class="image-column">
                        <div class="card room-card">
                            <%
                                String imageSrc = (room.getImageBase64() != null)
                                    ? "data:image/jpeg;base64," + room.getImageBase64()
                                    : request.getContextPath() + "/" + (room.getImagePath() != null ? room.getImagePath() : "Uploads/default.jpg");
                            %>
                            <img src="<%= imageSrc %>" alt="Room Image" class="room-image"
                                 onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/Uploads/default.jpg';" />
                        </div>
                    </div>

                    <!-- Info Column -->
                    <div class="info-column">
                        <div class="right-content">
                            <!-- Contact Section -->
                            <div class="contact-section">
                                <div class="d-flex align-items-center mb-3">
                                    <img src="<%= request.getContextPath() %>/Uploads/avatar.jpg" alt="HomeNest Avatar" class="contact-avatar me-3"
                                         onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/Uploads/default.jpg';" />
                                    <div><p class="mb-0 fw-bold">HomeNest</p></div>
                                </div>
                                <div class="button-group mt-4">
                                    <a href="tel:0889469948" class="btn btn-contact"><i class="bi bi-telephone-fill me-2"></i>0889469948</a>
                                    <% if (alreadyRequested) { %>
<!--                                    <div class="alert alert-info mt-4 text-center">
                                        <i class="bi bi-info-circle-fill me-2"></i> Your request is pending approval.
                                    </div>-->
                                    <div class="text-center mt-3">
                                        <button class="btn btn-outline-danger btn-sm" data-bs-toggle="modal" data-bs-target="#confirmCancelModal">
                                            <i class="bi bi-x-circle-fill me-1"></i> Cancel Request
                                        </button>
                                    </div>

                                    <div class="modal fade" id="confirmCancelModal" tabindex="-1" aria-labelledby="confirmCancelLabel">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content border-0" style="background-color: var(--background-color);">
                                                <div class="modal-header border-0">
                                                    <h5 class="modal-title" id="confirmCancelLabel">Confirm Cancellation</h5>
                                                    <button class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body fs-5 text-center">Are you sure you want to cancel this request?</div>
                                                <div class="modal-footer justify-content-center border-0">
                                                    <button class="btn btn-outline-secondary me-2 px-4" data-bs-dismiss="modal">No</button>
                                                    <form method="post" action="<%= request.getContextPath() %>/customer/cancel-request">
                                                        <input type="hidden" name="roomId" value="<%= room.getRoomID() %>"/>
                                                        <button class="btn btn-danger px-4">Yes, cancel</button>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <% } else if (hasAnyPendingRequest) { %>
                                    <div class="alert alert-warning mt-4 text-center">
                                        <i class="bi bi-exclamation-circle-fill me-2"></i> You already have another pending request. Please wait for approval before submitting a new one.
                                    </div>
                                    <% } else { %>
                                    <button class="btn btn-request" data-bs-toggle="modal" data-bs-target="#confirmModal">
                                        <i class="bi bi-send-fill me-2"></i> Submit Rental Request
                                    </button>
                                    <div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content rounded-4 shadow-sm border-0" style="background-color: #ffffff;">
                                                <div class="modal-header border-0">
                                                    <h5 class="modal-title" id="confirmModalLabel">Confirm Submission</h5>
                                                    <button class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body text-center fs-5 px-4 py-3">
                                                    Are you sure you want to send this rental request?
                                                </div>
                                                <div class="modal-footer justify-content-center border-0 pb-4">
                                                    <button class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">No</button>
                                                    <form method="post" action="<%= request.getContextPath() %>/request-room" class="d-inline">
                                                        <input type="hidden" name="roomId" value="<%= room.getRoomID() %>"/>
                                                        <button class="btn btn-success px-4">Submit</button>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>

                                        <% } %>
                                    </div>
                                </div>

                                <!-- Room and Utility Info Section -->
                                <div class="info-details">
                                    <!-- Room Info Column -->
                                    <div class="room-info-column">
                                        <h5 class="fw-bold mb-3">Room Information</h5>
                                        <div class="room-info">
                                            <p><strong>Area:</strong> <%= room.getArea() %> m²</p>
                                            <p><strong>Status:</strong> <%= room.getRoomStatus() %></p>
                                            <p><strong>Block:</strong> <%= blockList != null ? blockList.stream().filter(b -> b.getBlockID() == room.getBlockID()).map(Block::getBlockName).findFirst().orElse("N/A") : "N/A" %></p>
                                            <p><strong>Location:</strong> <%= room.getLocation() != null ? room.getLocation() : "N/A" %></p>
                                            <p><strong>Posted on:</strong> <%= postDateStr %></p>
                                        </div>
                                    </div>

                                    <!-- Utility Info Column -->
                                    <div class="utility-info-column">
                                        <h5 class="fw-bold mb-3"><i class="bi bi-lightning-charge-fill me-2"></i>Utility Information</h5>
                                        <%
                                            UtilityType dien = null, nuoc = null, wifi = null, rac = null;
                                            if (utilityTypes != null) {
                                                for (UtilityType ut : utilityTypes) {
                                                    String name = ut.getUtilityName().toLowerCase();
                                                    if (name.contains("electricity")) dien = ut;
                                                    else if (name.contains("water")) nuoc = ut;
                                                    else if (name.contains("wifi")) wifi = ut;
                                                    else if (name.contains("trash")) rac = ut;
                                                }
                                            }
                                        %>
                                        <p><strong>Electricity:</strong> <%= (room.getIsElectricityFree() == 1 && (dien != null)) ? formatter.format(dien.getUnitPrice()) + " VND / " + dien.getUnit() : "Free" %></p>
                                        <p><strong>Water:</strong> <%= (room.getIsWaterFree() == 1 && (nuoc != null)) ? formatter.format(nuoc.getUnitPrice()) + " VND / " + nuoc.getUnit() : "Free" %></p>
                                        <p><strong>Wifi:</strong> <%= (room.getIsWifiFree() == 1 && (wifi != null)) ? formatter.format(wifi.getUnitPrice()) + " VND / " + wifi.getUnit() : "Free" %></p>
                                        <p><strong>Trash:</strong> <%= (room.getIsTrashFree() == 1 && (rac != null)) ? formatter.format(rac.getUnitPrice()) + " VND / " + rac.getUnit() : "Free" %></p>
                                    </div>
                                </div>
                                <!-- Rent Price Section -->
                                <div class="rent-price-section">
                                    <p class="room-price"><strong><%= formatter.format(room.getRentPrice()) %> VND / month</strong></p>
                                </div>
                                <!-- Description Section -->
                                <div class="description-section">
                                    <p style="font-size: 1.35rem; font-weight: 700;"><strong>Description:</strong></p>
                                    <div class="room-description"><%= room.getDescription() != null ? room.getDescription() : "No description." %></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>  
                <% } else { %>
                <div class="alert alert-danger mt-5">Room not found.</div>
                <% } %>
        </main>
        <%@include file="/WEB-INF/inclu/footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>