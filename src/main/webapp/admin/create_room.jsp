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
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><%= "insert".equals(formAction) ? "Create New Room" : "Update Room"%></title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background: #eafaf9;
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
                color: #007777;
            }
            .btn-primary {
                border-radius: 30px;
                background-color: #007777;
            }
            .btn-primary:hover {
                background-color: #005f5f;
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

                    <!-- Category -->
                    <div class="mb-3">
                        <label class="form-label">Category:</label>
                        <select name="categoryID" class="form-select" required>
                            <option value="">-- Select Category --</option>
                            <% if (categoryList != null) {
                                    for (Category c : categoryList) {%>
                            <option value="<%= c.getCategoriesID()%>" <%= (room.getCategoryID() == c.getCategoriesID()) ? "selected" : ""%>>
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
                                    <input class="form-check-input" type="checkbox" name="highlights"
                                           value="<%= opt%>" <%= currentHighlights.contains(opt) ? "checked" : ""%> />
                                    <label class="form-check-label highlight-label"><%= opt%></label>
                                </div>
                            </div>
                            <% }%>
                        </div>
                    </div>

                    <!-- Room Images -->
                    <div class="mb-3">
                        <label class="form-label">Room Images:</label>
                        <input type="file" class="form-control" name="image" accept="image/*" multiple onchange="previewImages(event)" />

                    </div>

                    <div id="preview-container" class="mt-3 d-flex flex-wrap gap-2"></div>


                    <!-- Description -->
                    <div class="mb-3">
                        <label class="form-label">Description:</label>
                        <textarea class="form-control" name="description" rows="4"><%= room.getDescription() != null ? room.getDescription() : ""%></textarea>
                    </div>

                    <!-- Hiển thị hình ảnh cũ nếu có -->
                    <%
                        if (room.getImagePath() != null) {
                            String[] images = room.getImagePath().split(",");
                            for (String img : images) {
                    %>
                    <div class="mb-2">
                        <img src="<%= request.getContextPath() + "/" + img.trim()%>" class="img-thumbnail" width="200" />
                    </div>
                    <%
                            }
                        }
                    %>
                    <input type="hidden" name="existingImagePath" value="<%= room.getImagePath() != null ? room.getImagePath() : ""%>" />


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
        <script>
            function previewImages(event) {
                const container = document.getElementById("preview-container");
                container.innerHTML = ""; // clear previous previews
                const files = event.target.files;

                if (files) {
                    Array.from(files).forEach(file => {
                        const reader = new FileReader();
                        reader.onload = e => {
                            const img = document.createElement("img");
                            img.src = e.target.result;
                            img.classList.add("img-thumbnail");
                            img.style.width = "150px";
                            img.style.marginRight = "10px";
                            container.appendChild(img);
                        };
                        reader.readAsDataURL(file);
                    });
                }
            }
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
