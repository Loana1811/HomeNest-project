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
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Edit Room</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background: #f1f9f8;
                font-family: 'Segoe UI', sans-serif;
            }
            .form-container {
                max-width: 700px;
                background: #fff;
                margin: 50px auto;
                padding: 30px;
                border-radius: 1rem;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }
            .form-label {
                font-weight: bold;
                color: #006666;
            }
            .btn-primary {
                border-radius: 30px;
                background-color: #006666;
            }
            .btn-primary:hover {
                background-color: #004c4c;
            }
            .highlight-label {
                font-weight: normal;
                font-size: 14px;
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

                    <!-- Category -->
                    <div class="mb-3">
                        <label class="form-label">Category:</label>
                        <select name="categoryID" class="form-select" required>
                            <option value="">-- Select Category --</option>
                            <% if (categoryList != null) {
                                    for (Category c : categoryList) {
                            %>
                            <option value="<%= c.getCategoriesID()%>" <%= (room.getCategoryID() == c.getCategoriesID()) ? "selected" : ""%> >
                                <%= c.getCategoriesName()%>
                            </option>
                            <% }
                                } %>
                        </select>
                    </div>

                    <!-- Highlights -->
                    <div class="mb-3">
                        <label class="form-label">Highlights:</label>
<div class="row">
                            <%
                                String[] options = {
                                    "Free drinking water", "Coin laundry", "Motorbike parking",
                                    "Elevator access", "Security camera", "High-speed wifi",
                                    "Drying area", "Daily cleaning"
                                };
                                String currentHighlights = room.getHighlights() != null ? room.getHighlights() : "";
                                for (String opt : options) {
                            %>
                            <div class="col-6">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="highlights" value="<%= opt%>"
                                           <%= currentHighlights.contains(opt) ? "checked" : ""%> />
                                    <label class="form-check-label highlight-label"><%= opt%></label>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>

                    <!-- Room Image -->
                    <div class="mb-3">
                        <label class="form-label">Room Image:</label>
                        <input type="file" class="form-control" name="image" accept="image/*" />
                        <% if (room.getImagePath() != null && !room.getImagePath().isEmpty()) {%>
                        <img src="<%= room.getImagePath()%>" class="img-thumbnail mt-2" style="max-height: 200px;" />
                        <% }%>
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
