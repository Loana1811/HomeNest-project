<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Create New Contract</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .container {
                max-width: 800px;
                margin-top: 40px;
            }
            .room-details {
                display: none;
                margin-top: 10px;
                padding: 15px;
                background-color: #f8f9fa;
                border-radius: 5px;
            }
        </style>
        <script>
            function showRoomDetails(select) {
                const roomDetailsDiv = document.getElementById('roomDetails');
                const selectedOption = select.options[select.selectedIndex];
                if (selectedOption.value) {
                    const details = JSON.parse(selectedOption.getAttribute('data-details'));
                    roomDetailsDiv.innerHTML = `
                        <h5>Room Details</h5>
                        <p><strong>Room Number:</strong> ${details.roomNumber}</p>
                        <p><strong>Rent Price:</strong> ${details.rentPrice}đ</p>
                        <p><strong>Area:</strong> ${details.area}m²</p>
                        <p><strong>Location:</strong> ${details.location}</p>
                        <p><strong>Block ID:</strong> ${details.blockId}</p>
                        <p><strong>Description:</strong> ${details.description || 'None'}</p>
                        <p><strong>Electricity Free:</strong> ${details.isElectricityFree ? 'Yes' : 'No'}</p>
                        <p><strong>Water Free:</strong> ${details.isWaterFree ? 'Yes' : 'No'}</p>
                        <p><strong>Wifi Free:</strong> ${details.isWifiFree ? 'Yes' : 'No'}</p>
                        <p><strong>Trash Free:</strong> ${details.isTrashFree ? 'Yes' : 'No'}</p>
                    `;
                    roomDetailsDiv.style.display = 'block';
                } else {
                    roomDetailsDiv.style.display = 'none';
                }
            }
        </script>
    </head>
    <body>
        <div class="container">
            <h2 class="text-center mb-4">Create New Contract</h2>

            <!-- Hiển thị thông báo lỗi nếu có -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger text-center">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/Contracts" method="post">
                <input type="hidden" name="action" value="create">

                <!-- Chọn người thuê -->
                <div class="mb-3">
                    <label for="tenantId" class="form-label">Tenant</label>
                    <select class="form-select" id="tenantId" name="tenantId" required>
                        <option value="">-- Select Tenant --</option>
                        <c:forEach var="tenant" items="${tenants}">
                            <option value="${tenant.tenantID}">
                                Tenant ID: ${tenant.tenantID}, Name: ${tenant.customerFullName}, CCCD: ${tenant.CCCD}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Chọn phòng -->
                <div class="mb-3">
                    <label for="roomId" class="form-label">Room</label>
                    <select class="form-select" id="roomId" name="roomId" required onchange="showRoomDetails(this)">
                        <option value="">-- Select Room --</option>
                        <c:forEach var="room" items="${rooms}">
                            <option value="${room.roomID}"
                                    data-details='{
                                    "roomNumber": "${room.roomNumber}",
                                    "rentPrice": "${room.rentPrice}",
                                    "area": "${room.area}",
                                    "location": "${room.location}",
                                    "blockId": "${room.blockId}",
                                    "description": "${room.description != null ? room.description : ''}",
                                    "isElectricityFree": ${room.isElectricityFree},
                                    "isWaterFree": ${room.isWaterFree},
                                    "isWifiFree": ${room.isWifiFree},
                                    "isTrashFree": ${room.isTrashFree}
                                    }'>
                                Room ${room.roomNumber} - ${room.location} (Price: ${room.rentPrice}đ, Area: ${room.area}m²)
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Hiển thị chi tiết phòng -->
                <div id="roomDetails" class="room-details"></div>

                <!-- Ngày bắt đầu -->
                <div class="mb-3">
                    <label for="startDate" class="form-label">Start Date</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" required>
                </div>

                <!-- Ngày kết thúc -->
                <div class="mb-3">
                    <label for="endDate" class="form-label">End Date</label>
                    <input type="date" class="form-control" id="endDate" name="endDate" required>
                </div>

                <!-- Nút thao tác -->
                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-success me-2">Create Contract</button>
                    <a href="${pageContext.request.contextPath}/Contracts" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </body>
</html>