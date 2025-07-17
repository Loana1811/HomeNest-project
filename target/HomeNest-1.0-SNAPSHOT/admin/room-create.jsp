<%-- 
    Document   : room-create
    Created on : Jun 12, 2025, 12:41:12 AM
    Author     : kloane
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create a New Room</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <style>
        .form-section { margin-bottom: 20px; }
        .form-section h5 { margin-bottom: 10px; }
        .preview-card {
            opacity: 0.4;
            padding: 15px;
            border: 1px solid #ccc;
            border-radius: 8px;
            margin-top: 20px;
            background-color: #f9f9f9;
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <h3 class="mb-4">🛏️ Create a New Room</h3>
    <form action="room-create" method="post" enctype="multipart/form-data">
        <div class="row form-section">
            <div class="col-md-6">
                <label>Room Name</label>
                <input type="text" name="roomName" class="form-control" placeholder="e.g. Room A1" required>
            </div>
            <div class="col-md-6">
                <label>Building / Block</label>
                <select name="buildingId" class="form-control">
                    <option value="">-- Select a block --</option>
                    <c:forEach var="block" items="${blocks}">
                        <option value="${block.id}">${block.name}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="row form-section">
            <div class="col-md-4">
                <label>Room Type</label><br>
                <input type="radio" name="roomType" value="Lofted" checked> Lofted
                <input type="radio" name="roomType" value="Non-lofted"> Non-lofted
            </div>
            <div class="col-md-4">
                <label>Price (VND)</label>
                <input type="number" name="price" class="form-control" required>
            </div>
            <div class="col-md-4">
                <label>Area (m²)</label>
                <input type="number" name="area" class="form-control" required>
            </div>
        </div>

        <div class="form-section">
            <label>Location</label>
            <input type="text" name="location" class="form-control" placeholder="e.g. 574/15/38 SinCo Street, ..." required>
        </div>

        <div class="form-section">
            <label>Description</label>
            <textarea name="description" class="form-control" rows="3" placeholder="Write details about this room..."></textarea>
        </div>

        <div class="form-section">
            <label>Upload Room Images</label>
            <input type="file" name="images" class="form-control" multiple>
        </div>

        <!-- Amenities -->
        <div class="form-section">
            <h5>Amenities</h5>
            <div class="row">
                <div class="col-md-3">
                    <label><input type="checkbox" name="amenities" value="Free drinking water"> Free drinking water</label><br>
                    <label><input type="checkbox" name="amenities" value="Coin laundry"> Coin laundry</label><br>
                    <label><input type="checkbox" name="amenities" value="Motorbike parking"> Motorbike parking</label><br>
                    <label><input type="checkbox" name="amenities" value="Elevator access"> Elevator access</label>
                </div>
                <div class="col-md-3">
                    <label><input type="checkbox" name="amenities" value="Security camera"> Security camera</label><br>
                    <label><input type="checkbox" name="amenities" value="High-speed wifi"> High-speed wifi</label><br>
                    <label><input type="checkbox" name="amenities" value="Drying area"> Drying area</label><br>
                    <label><input type="checkbox" name="amenities" value="Daily cleaning"> Daily cleaning</label>
                </div>
            </div>
        </div>

        <!-- Services -->
        <div class="form-section">
            <h5>Utilities</h5>
            <div class="row">
                <div class="col-md-3">
                    <label><input type="checkbox" name="services" value="Electricity"> Electricity</label>
                    <input type="number" name="electricPrice" class="form-control" placeholder="Price (VND/person)">
                </div>
                <div class="col-md-3">
                    <label><input type="checkbox" name="services" value="Water"> Water</label>
                    <input type="number" name="waterPrice" class="form-control" placeholder="Price (VND/person)">
                </div>
                <div class="col-md-3">
                    <label><input type="checkbox" name="services" value="Trash"> Trash</label>
                    <input type="number" name="trashPrice" class="form-control" placeholder="Price (VND/person)">
                </div>
                <div class="col-md-3">
                    <label><input type="checkbox" name="services" value="Wifi"> Wifi</label>
                    <input type="number" name="wifiPrice" class="form-control" placeholder="Price (VND/person)">
                </div>
            </div>
        </div>

        <div class="form-section">
            <button type="submit" class="btn btn-primary">Create Room</button>
            <a href="room.jsp" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
