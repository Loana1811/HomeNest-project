<%-- 
    Document   : room-detail
    Created on : Jun 14, 2025, 3:45:45 PM
    Author     : ADMIN
--%>

<%@page import="model.Block"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Room" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>

<%
    Room room = (Room) request.getAttribute("room");
    List<Room> featuredRooms = (List<Room>) request.getAttribute("featuredRooms");
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String postDateStr = (room != null && room.getPostedDate() != null)
            ? sdf.format(room.getPostedDate())
            : "N/A";
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Room Detail</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet"/>
        <style>
            .room-image {
                width: 100%;
                max-width: 720px;
                height: auto;
                display: block;
                margin: 0 auto;
                border-radius: 10px;
                box-shadow: 0 4px 16px rgba(0,0,0,0.1);
            }

            .room-card {
                border: none;
                box-shadow: 0 4px 20px rgba(0,0,0,0.15);
                border-radius: 15px;
                overflow: hidden;
            }

            .room-info p {
                display: flex;
                align-items: baseline;
                gap: 0.75rem;
                font-size: 1.3rem;
                margin-bottom: 1rem;
                line-height: 1.6;
            }

            .room-info p strong {
                width: 110px;
                display: inline-block;
                font-weight: 700;
                color: #222;
                font-size: 1.35rem;
            }

            .room-price {
                font-size: 1.8rem;
                color: #ff6600;
                font-weight: bold;
                margin-top: 1rem;
            }

            .room-description {
                font-size: 1.25rem;
                padding: 1rem;
                background-color: #fdfdfd;
                border: 1px solid #ddd;
                border-radius: 8px;
                margin-top: 1rem;
                white-space: pre-line;
            }

            .contact-card, .highlight-card {
                font-size: 1.2rem;
                padding: 2rem;
                border-radius: 16px;
                box-shadow: 0 6px 20px rgba(0,0,0,0.08);
            }

            .contact-card h5,
            .highlight-card h6 {
                font-size: 1.45rem;
                font-weight: 700;
                margin-bottom: 1.5rem;
            }

            .contact-avatar {
                width: 80px;
                height: 80px;
                border-radius: 50%;
                background-color: #bbb;
            }

            .contact-card .btn {
                font-size: 1.15rem;
                padding: 12px;
                font-weight: 600;
            }

            .highlight-card p {
                font-size: 1.15rem;
            }

            .highlight-card small {
                font-size: 1rem;
                color: #555;
            }

            .highlight-tags {
                display: flex;
                flex-wrap: wrap;
                gap: 0.5rem;
                margin-top: 0.5rem;
            }

            .badge-tag {
                display: inline-block;
                background-color: #e6f2ff;
                color: #0066cc;
                border: 1px solid #b3daff;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 0.95rem;
                font-weight: 500;
                transition: 0.3s;
            }

            .badge-tag:hover {
                background-color: #cce6ff;
                color: #004080;
                cursor: default;
            }
        </style>
    </head>
    <body class="d-flex flex-column min-vh-100">

        <main class="container my-5 flex-grow-1">
            <% if (room != null) {%>
            <div class="row">
                <!-- LEFT COLUMN -->
                <div class="col-md-8">
                    <div class="card room-card p-4 mb-4">
                        <div class="text-center mb-4">
                            <img src="<%= request.getContextPath()%>/<%= room.getImagePath() != null ? room.getImagePath() : "images/rooms/room-default.jpg"%>"
                                 alt="Room Image"
                                 class="room-image"
                                 onerror="this.onerror=null;this.src='<%= request.getContextPath()%>/images/rooms/room-default.jpg';" />


                        </div>

                        <div class="room-info">
                            <h3 class="fw-bold text-center mb-4">Room: <%= room.getRoomNumber()%></h3>
                            <p><strong>Area:</strong> <%= room.getArea()%> m²</p>
                            <p><strong>Status:</strong> <%= room.getRoomStatus()%></p>
                            <%
                                List<Block> blockList = (List<Block>) request.getAttribute("blockList");
                                String blockName = "N/A";
                                if (blockList != null) {
                                    for (Block b : blockList) {
                                        if (b.getBlockID() == room.getBlockID()) {
                                            blockName = b.getBlockName();
                                            break;
                                        }
                                    }
                                }
                            %>
                            <p><strong>Block:</strong> <%= blockName%></p>

                            <p><strong>Location:</strong> <%= room.getLocation() != null ? room.getLocation() : "N/A"%></p>

                            <p><strong>Post:</strong> <%= postDateStr%></p>
                            <p class="room-price"><strong>Price:</strong> <%= String.format("%,.0f", room.getRentPrice())%> ₫ / month</p>

                            <p><strong>Highlights</strong>
                                <% if (room.getHighlights() != null && !room.getHighlights().trim().isEmpty()) {
                                        String[] highlights = room.getHighlights().split(",");
                                        for (String h : highlights) {%>
                                <span class="badge-tag"><%= h.trim()%></span>
                                <% }
                                } else { %>
                                <span class="text-muted">No highlights available.</span>
                                <% }%>
                            </p>
                        </div>
                    </div>

                    <p style="font-size: 1.35rem; font-weight: 700;"><strong>Description:</strong></p>
                    <div class="room-description">
                        <%= room.getDescription() != null ? room.getDescription() : "No description available."%>
                    </div>
                </div>

                <!-- RIGHT COLUMN -->
                <div class="col-md-4">
                    <!-- CONTACT -->
                    <div class="card contact-card mb-4">
                        <h5 class="fw-bold mb-3">Contact</h5>
                        <div class="d-flex align-items-center mb-3">
                            <img src="<%= request.getContextPath()%>/images/logo.jpg"
                                 alt="Avatar"
                                 class="contact-avatar me-3"
                                 style="object-fit: cover;">
                            <div>
                                <p class="mb-0 fw-bold">HomeNest</p>
                            </div>
                        </div>
                        <a href="tel:0909123456" class="btn btn-success w-100 mb-2">
                            <i class="bi bi-telephone-fill me-2"></i>0889469948
                        </a>
                    </div>

                    <!-- FEATURED LISTINGS -->
                    <div class="card highlight-card">
                        <h6 class="fw-bold mb-3">Featured Listings</h6>
                        <% if (featuredRooms != null) {
                                for (Room r : featuredRooms) {
                                    String posted = (r.getPostedDate() != null) ? sdf.format(r.getPostedDate()) : "N/A";%>
                        <a href="room-detail?id=<%= r.getRoomID()%>" class="text-decoration-none text-dark">
                            <div class="d-flex justify-content-between mb-3 border-bottom pb-2">
                                <img src="<%= request.getContextPath()%>/<%= r.getImagePath() != null ? r.getImagePath() : "uploads/default.jpg" %>"
                                     alt="thumbnail"
                                     class="rounded"
                                     style="width: 64px; height: 64px; object-fit: cover;" />

                                <div class="flex-grow-1 d-flex flex-column">
                                    <div class="d-flex justify-content-between">
                                        <span class="fw-semibold text-dark">
                                            <%= String.format("%.3f", r.getRentPrice() / 1_000_000)%> mil VND/month
                                        </span>
                                        <small class="text-muted" style="white-space: nowrap; min-width: 90px; text-align: right; font-size: 0.75rem; font-style: italic;">
                                            <%= posted%>
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </a>
                        <% }
                        } else { %>
                        <p class="text-muted">No featured rooms.</p>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } else { %>
            <div class="alert alert-danger mt-5">No room found.</div>
            <% }%>
        </main>

    </body>
</html>
