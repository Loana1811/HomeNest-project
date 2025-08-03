<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Create New Contract</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
    :root {
        --primary: #1e3b8a;
        --secondary: #3f5fa6;
        --bg-light: #f9fbfc;
        --text-dark: #1f2937;
    }

    body {
        background-color: var(--bg-light);
        font-family: 'Segoe UI', sans-serif;
    }

    .container {
        max-width: 720px;
        background-color: #fff;
        padding: 30px 40px;
        border-radius: 16px;
        margin-top: 60px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        animation: fadeIn 0.6s ease-in-out;
    }

    h2 {
        color: var(--primary);
        font-weight: bold;
        margin-bottom: 30px;
    }

    label.form-label {
        font-weight: 600;
        color: var(--text-dark);
    }

    select.form-select,
    input.form-control {
        border-radius: 8px;
        padding: 10px 14px;
        border: 1px solid #ccc;
        transition: border-color 0.2s;
    }

    select.form-select:focus,
    input.form-control:focus {
        border-color: var(--primary);
        box-shadow: 0 0 0 0.2rem rgba(30, 59, 138, 0.2);
    }

    .room-details {
        display: none;
        margin-top: 15px;
        padding: 15px 20px;
        background-color: #f1f5f9;
        border-radius: 10px;
        font-size: 0.95rem;
        line-height: 1.6;
        box-shadow: inset 0 0 6px rgba(0,0,0,0.05);
        color: #374151;
    }

    .btn-success {
        background-color: #22c55e;
        border: none;
        border-radius: 8px;
        padding: 10px 20px;
        font-weight: bold;
    }

    .btn-success:hover {
        background-color: #16a34a;
    }

    .btn-secondary {
        border-radius: 8px;
        padding: 10px 20px;
        font-weight: bold;
    }

    .alert {
        border-radius: 8px;
        font-weight: 500;
    }

    @keyframes fadeIn {
        from {opacity: 0; transform: translateY(10px);}
        to {opacity: 1; transform: translateY(0);}
    }
</style>

        <script>
            function showRoomDetails(select) {
                const roomDetailsDiv = document.getElementById('roomDetails');
                const selectedOption = select.options[select.selectedIndex];
                if (selectedOption.value) {
                    const details = JSON.parse(selectedOption.getAttribute('data-details'));
                    roomDetailsDiv.innerHTML = `
                     

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