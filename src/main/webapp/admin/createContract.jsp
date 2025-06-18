<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Create New Contract</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="container mt-5">
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
                                Tenant ID: ${tenant.tenantID}, Customer ID: ${tenant.customerID}, Join Date: ${tenant.joinDate}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Chọn phòng -->
                <div class="mb-3">
                    <label for="roomId" class="form-label">Room</label>
                    <select class="form-select" id="roomId" name="roomId" required>
                        <option value="">-- Select Room --</option>
                        <c:forEach var="room" items="${rooms}">
                            <option value="${room.roomID}">
                                Room ${room.roomNumber}- ${room.location}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Ngày bắt đầu -->
                <div class="mb-3">
                    <label for="startDate" class="form-label">Start Date</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" required>
                </div>

                <!-- Ngày kết thúc -->
                <div class="mb-3">
                    <label for="endDate" class="form-label">End Date (Optional)</label>
                    <input type="date" class="form-control" id="endDate" name="endDate">
                </div>

                <!-- Nút thao tác -->
                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-success me-2">Create Contract</button>
                    <a href="Contracts" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
