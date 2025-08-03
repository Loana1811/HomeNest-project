<%-- 
    Document   : edit_room.jsp
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Room, model.Block, model.Category, java.util.List" %>

<%
    Room room = (Room) request.getAttribute("room");
    if (room == null) {
%>
<div class="alert alert-danger text-center m-5">⚠️ No room data available to edit.</div>
<%
        return;
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
        <title>Edit Room</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
       <style>
    body {
        background: #f0f4f8;
        font-family: 'Segoe UI', sans-serif;
    }

    .form-container {
        max-width: 750px;
        background: #ffffff;
        margin: 60px auto;
        padding: 35px 40px;
        border-radius: 16px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    }

    h3 {
        text-align: center;
        color: #1e3b8a;
        font-weight: 600;
        margin-bottom: 30px;
    }

    .form-label {
        font-weight: 600;
        color: #1e3b8a;
    }

    .form-control,
    .form-select {
        border-radius: 12px;
        padding: 10px 14px;
        border: 1px solid #ced4da;
        transition: border-color 0.3s ease;
    }

    .form-control:focus,
    .form-select:focus {
        border-color: #1e3b8a;
        box-shadow: 0 0 0 0.2rem rgba(30, 59, 138, 0.25);
    }

    .btn-primary {
        background-color: #1e3b8a;
        border: none;
        border-radius: 30px;
        font-weight: 500;
        transition: background-color 0.3s ease, transform 0.2s ease;
    }

    .btn-primary:hover {
        background-color: #163174;
        transform: scale(1.02);
    }

    .btn-secondary {
        border-radius: 30px;
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

    .room-img {
        width: 100%;
        max-width: 320px;
        border-radius: 8px;
        border: 1px solid #ccc;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
        margin-top: 8px;
    }

    textarea.form-control {
        resize: vertical;
        min-height: 100px;
    }

    .alert-danger {
        border-radius: 10px;
        font-size: 15px;
    }

    .form-check-inline {
        margin-right: 12px;
    }

    .form-check-input:checked {
        background-color: #1e3b8a;
        border-color: #1e3b8a;
    }
</style>

    </head>
    <body>

        <div class="container">
            <div class="form-container">
                <h3 class="text-center mb-4">Edit Room</h3>

                <form action="rooms" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="edit" />
                    <input type="hidden" name="id" value="<%= room.getRoomID()%>" />
                    <input type="hidden" name="existingImagePath" value="<%= room.getImagePath() != null ? room.getImagePath() : ""%>" />

                    <!-- Room Number -->
                    <div class="mb-3">
                        <label class="form-label">Room Number:</label>
                        <input type="text" class="form-control" name="roomNumber"
                               value="<%= room.getRoomNumber() != null ? room.getRoomNumber() : ""%>" required />
                    </div>

                    <!-- Rent Price -->
                    <div class="mb-3">
                        <label class="form-label">Rent Price (VND):</label>
                        <input type="number" class="form-control" name="rentPrice" step="1000"
                               value="<%= room.getRentPrice()%>" required />
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
                               value="<%= room.getArea()%>" />
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
                                    for (Block b : blockList) {
                            %>
                            <option value="<%= b.getBlockID()%>" <%= (room.getBlockID() == b.getBlockID()) ? "selected" : ""%> >
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
                        <input type="file" class="form-control" name="image" accept="image/*" />

                        <%-- Hiển thị hình ảnh cũ nếu có --%>
                        <div class="mt-2">
                            <label class="form-label">Current Image:</label><br/>
                            <!-- Hiển thị ảnh bằng cách kết hợp context path với đường dẫn ảnh -->
                            <img src="<%= request.getContextPath() %>/room-image?id=<%= room.getRoomID() %>" class="room-img" alt="Room Image"/>
                        </div>
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <label class="form-label">Description:</label>
                        <textarea class="form-control" name="description" rows="4"><%= room.getDescription() != null ? room.getDescription() : ""%></textarea>
                    </div>

                    <!-- Submit & Back Buttons -->
                    <div class="d-flex justify-content-between gap-2">
                        <a href="rooms?action=list" class="btn btn-secondary w-50" style="border-radius: 30px;">
                            ← Back
                        </a>
                        <button type="submit" class="btn btn-primary w-50" style="border-radius: 30px;">
                            💾 Update Room
                        </button>
                    </div>

                    <!-- Error Message -->
                    <% if (error != null) {%>
                    <div class="alert alert-danger mt-3"><%= error%></div>
                    <% }%>

                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
