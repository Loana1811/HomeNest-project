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
<div class="alert alert-danger text-center m-5">⚠️ Không có dữ liệu phòng để chỉnh sửa.</div>
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
    <title>Chỉnh sửa phòng</title>
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
    </style>
</head>
<body>

<div class="container">
    <div class="form-container">
        <h3 class="text-center mb-4">Chỉnh sửa thông tin phòng</h3>

        <form action="rooms" method="post" enctype="multipart/form-data">
            <input type="hidden" name="action" value="edit" />
            <input type="hidden" name="id" value="<%= room.getRoomID()%>" />
            <input type="hidden" name="existingImagePath" value="<%= room.getImagePath() != null ? room.getImagePath() : ""%>" />

            <!-- Số phòng -->
            <div class="mb-3">
                <label class="form-label">Số phòng:</label>
                <input type="text" class="form-control" name="roomNumber"
                       value="<%= room.getRoomNumber() != null ? room.getRoomNumber() : ""%>" required />
            </div>

            <!-- Giá thuê -->
            <div class="mb-3">
                <label class="form-label">Giá thuê (VND):</label>
                <input type="number" class="form-control" name="rentPrice" step="1000"
                       value="<%= room.getRentPrice()%>" required />
            </div>

            <!-- Vị trí -->
            <div class="mb-3">
                <label class="form-label">Vị trí:</label>
                <input type="text" class="form-control" name="location"
                       value="<%= room.getLocation() != null ? room.getLocation() : ""%>" required />
            </div>

            <!-- Diện tích -->
            <div class="mb-3">
                <label class="form-label">Diện tích (m²):</label>
                <input type="number" class="form-control" name="area" step="0.1"
                       value="<%= room.getArea()%>" />
            </div>

            <!-- Trạng thái -->
            <div class="mb-3">
                <label class="form-label">Trạng thái:</label>
                <select name="status" class="form-select" required>
                    <option value="Available" <%= "Available".equals(room.getRoomStatus()) ? "selected" : ""%>>Trống</option>
                    <option value="Occupied" <%= "Occupied".equals(room.getRoomStatus()) ? "selected" : ""%>>Đã thuê</option>
                </select>
            </div>

            <!-- Block -->
            <div class="mb-3">
                <label class="form-label">Block:</label>
                <select name="blockID" class="form-select" required>
                    <option value="">-- Chọn block --</option>
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

            <!-- Dịch vụ miễn phí -->
            <div class="mb-3">
                <label class="form-label">Dịch vụ miễn phí:</label><br/>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="isElectricityFree" value="0" <%= room.getIsElectricityFree() == 0 ? "checked" : "" %> />
                    <label class="form-check-label">Điện</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="isWaterFree" value="0" <%= room.getIsWaterFree() == 0 ? "checked" : "" %> />
                    <label class="form-check-label">Nước</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="isWifiFree" value="0" <%= room.getIsWifiFree() == 0 ? "checked" : "" %> />
                    <label class="form-check-label">WiFi</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="checkbox" name="isTrashFree" value="0" <%= room.getIsTrashFree() == 0 ? "checked" : "" %> />
                    <label class="form-check-label">Rác</label>
                </div>
            </div>

            <!-- Hình ảnh -->
            <div class="mb-3">
                <label class="form-label">Ảnh phòng:</label>
                <input type="file" class="form-control" name="image" accept="image/*" id="imageInput" />

                <% if (room.getImagePath() != null && room.getImagePath().length > 0) { %>
                <div class="mt-2">
                    <label class="form-label">Ảnh hiện tại:</label><br/>
                    <img id="imagePreview" 
                         loading="lazy" 
                         src="<%= request.getContextPath() %>/admin/rooms?action=room-image&id=<%= room.getRoomID() %>" 
                         class="img-thumbnail mt-2" style="max-height: 200px;"
                         onerror="this.src='<%= request.getContextPath() %>/uploads/default.jpg'" />
                </div>
                <% } else { %>
                <div class="mt-2">
                    <label class="form-label">Xem trước ảnh:</label><br/>
                    <img id="imagePreview" 
                         loading="lazy" 
                         src="<%= request.getContextPath() %>/uploads/default.jpg" 
                         class="img-thumbnail mt-2" style="max-height: 200px;" />
                </div>
                <% } %>
            </div>

            <!-- Mô tả -->
            <div class="mb-3">
                <label class="form-label">Mô tả:</label>
                <textarea class="form-control" name="description" rows="4"><%= room.getDescription() != null ? room.getDescription() : ""%></textarea>
            </div>

            <!-- Nút -->
            <div class="d-flex justify-content-between gap-2">
                <a href="rooms?action=list" class="btn btn-secondary w-50" style="border-radius: 30px;">
                    ← Quay lại
                </a>
                <button type="submit" class="btn btn-primary w-50" style="border-radius: 30px;">
                    💾 Cập nhật phòng
                </button>
            </div>

            <% if (error != null) {%>
            <div class="alert alert-danger mt-3"><%= error%></div>
            <% }%>
        </form>
    </div>
</div>

<script>
    const imageInput = document.getElementById('imageInput');
    const imagePreview = document.getElementById('imagePreview');

    imageInput.addEventListener('change', function(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result;
            }
            reader.readAsDataURL(file);
        }
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
