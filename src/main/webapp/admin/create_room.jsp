<%-- 
    Document   : create_room
    Created on : Jun 16, 2025, 9:08:37 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Room, model.Block, model.Category, java.util.List" %>
<%
    Room room = (Room) request.getAttribute("room");
    if (room == null) {
        room = new Room();
    }

    String formAction = (String) request.getAttribute("action");
    if (formAction == null) {
        formAction = "insert";
    }

    List<Block> blockList = (List<Block>) request.getAttribute("blockList");
    List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
    
    
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><%= "insert".equals(formAction) ? "Create New Room" : "Update Room"%></title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

       <style>
    body {
        background-color: #f9fcff;
        font-family: 'Segoe UI', sans-serif;
        color: #1f2937;
    }

    .form-container {
        max-width: 720px;
        background: #ffffff;
        margin: 50px auto;
        padding: 40px 30px;
        border-radius: 20px;
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
        transition: 0.3s ease-in-out;
    }

    h3.text-center {
        font-weight: 700;
        color: #1e3a8a;
    }

    .form-label {
        font-weight: 600;
        margin-bottom: 6px;
        color: #1e293b;
    }

    .form-control,
    .form-select,
    textarea {
        border-radius: 25px;
        border: 1px solid #cbd5e1;
        padding: 10px 18px;
        font-size: 16px;
        transition: all 0.2s ease;
    }

    .form-control:focus,
    .form-select:focus,
    textarea:focus {
        border-color: #3b82f6;
        box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
    }

    .form-check-label {
        margin-left: 6px;
        color: #374151;
        font-weight: 500;
    }

    .form-check-input {
        border-radius: 50%;
        width: 18px;
        height: 18px;
    }

    .btn-primary {
        background-color: #2563eb;
        border: none;
        border-radius: 30px;
        font-weight: 600;
        padding: 10px;
    }

    .btn-primary:hover {
        background-color: #1e40af;
    }

    .btn-secondary {
        background-color: #6b7280;
        border: none;
        border-radius: 30px;
        font-weight: 600;
        padding: 10px;
    }

    .btn-secondary:hover {
        background-color: #4b5563;
    }

    .alert-danger {
        font-weight: 500;
        font-size: 15px;
        padding: 12px;
        border-radius: 10px;
    }
</style>

    </head>
    <body>
        <div class="container">
            <div class="form-container">
                <h3 class="text-center mb-4"><%= "insert".equals(formAction) ? "Create New Room" : "Update Room"%></h3>

                <form action="rooms" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="<%= formAction%>" />
                    <% if (room.getRoomID() > 0) {%>
                    <input type="hidden" name="id" value="<%= room.getRoomID()%>" />
                    <% }%>

                    <!-- Room Number -->
                    <div class="mb-3">
                        <label class="form-label">Room Number:</label>
                        <input type="text" class="form-control" name="roomNumber"
                               value="<%= room.getRoomNumber() != null ? room.getRoomNumber() : ""%>" required />
                    </div>
                    <% if (error != null) {%>
<div class="alert alert-danger text-center fw-bold mt-3" style="font-size: 16px;">
                        ⚠️ <%= error%>
                    </div>
                    <% }%>

                    <!-- Rent Price -->
                    <div class="mb-3">
                        <label class="form-label">Rent Price (VND):</label>
                        <input type="number" class="form-control" name="rentPrice" step="1000"
                               value="<%= room.getRentPrice() != 0 ? room.getRentPrice() : ""%>" required />
                    </div>

                    <!-- Location -->
                    <div class="mb-3">
                        <label class="form-label">Location:</label>
                        <input type="text" class="form-control" name="location"
                               value="<%= room.getLocation() != null ? room.getLocation() : ""%>" required />
                    </div>

                    <!-- Area -->
                    <div class="mb-3">
                        <label class="form-label">Area (m²):</label>
                        <input type="number" class="form-control" name="area" step="0.1"
                               value="<%= room.getArea() != 0 ? room.getArea() : ""%>" />
                    </div>

                    <!-- Status -->
                    <div class="mb-3">
                        <label class="form-label">Status:</label>
                        <select name="status" class="form-select" required>
                            <option value="Available" <%= "Available".equals(room.getRoomStatus()) ? "selected" : ""%>>Available</option>
                            <option value="Occupied" <%= "Occupied".equals(room.getRoomStatus()) ? "selected" : ""%>>Occupied</option>
                        </select>
                    </div>

                    <!-- Block -->
                    <div class="mb-3">
                        <label class="form-label">Block:</label>
                        <select name="blockID" class="form-select" required>
                            <option value="">-- Select Block --</option>
                            <% if (blockList != null) {
                                    for (Block b : blockList) {%>
                            <option value="<%= b.getBlockID()%>" <%= (room.getBlockID() == b.getBlockID()) ? "selected" : ""%>>
                                <%= b.getBlockName()%>
                            </option>
                            <% }
                                } %>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Free Services:</label><br/>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="checkbox" name="isElectricityFree" value="0" <%= room.getIsElectricityFree() == 0 ? "checked" : "" %> />
<label class="form-check-label">Electricity</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="checkbox" name="isWaterFree" value="0" <%= room.getIsWaterFree() == 0 ? "checked" : "" %> />
                            <label class="form-check-label">Water</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="checkbox" name="isWifiFree" value="0" <%= room.getIsWifiFree() == 0 ? "checked" : "" %> />
                            <label class="form-check-label">WiFi</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="checkbox" name="isTrashFree" value="0" <%= room.getIsTrashFree() == 0 ? "checked" : "" %> />
                            <label class="form-check-label">Trash</label>
                        </div>
                    </div>





                    <!-- Room Image -->
                    <div class="mb-3">
                        <label class="form-label">Room Image:</label>
                        <input type="file" class="form-control" name="${pageContext.request.contextPath}/img/logo.png" alt="Logo" class="qr-code mb-2">
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <label class="form-label">Description:</label>
                        <textarea class="form-control" name="description" rows="4"><%= room.getDescription() != null ? room.getDescription() : ""%></textarea>
                    </div>

                  
                    <!-- Error Message -->
                    <% if (error != null) {%>
                    <div class="alert alert-danger mt-3"><%= error%></div>
                    <% }%>

                    <!-- Submit Buttons -->
                    <div class="d-flex justify-content-between gap-2 mt-4">
                        <a href="rooms?action=list" class="btn btn-secondary w-50" style="border-radius: 30px;">← Back</a>
                        <button type="submit" class="btn btn-primary w-50" style="border-radius: 30px;">
                            <%= "insert".equals(formAction) ? "➕ Create Room" : "💾 Update Room"%>
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
