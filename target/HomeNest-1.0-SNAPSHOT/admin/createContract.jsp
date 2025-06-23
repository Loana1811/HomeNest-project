<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Create New Contract</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .container {
                max-width: 700px;
                background-color: white;
                border-radius: 15px;
                box-shadow: 0 4px 20px rgba(0, 128, 128, 0.2);
                padding: 40px;
            }

            h2 {
                color: rgb(0, 128, 128);
                font-weight: bold;
            }

            .form-label {
                color: rgb(0, 100, 100);
                font-weight: 500;
            }

            .form-control, .form-select {
                border: 2px solid rgb(0, 128, 128);
                border-radius: 8px;
                transition: border-color 0.3s, box-shadow 0.3s;
            }

            .form-control:focus, .form-select:focus {
                border-color: rgb(0, 100, 100);
                box-shadow: 0 0 0 0.2rem rgba(0, 128, 128, 0.25);
            }

            .btn-success {
                background-color: rgb(0, 128, 128);
                border-color: rgb(0, 128, 128);
            }

            .btn-success:hover {
                background-color: rgb(0, 110, 110);
                border-color: rgb(0, 110, 110);
            }

            .btn-secondary {
                background-color: #6c757d;
                border-color: #6c757d;
            }

            .alert-danger {
                border: 1px solid #f5c6cb;
                background-color: #f8d7da;
                color: #721c24;
                border-radius: 10px;
            }
        </style>
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
                                Room ${room.roomNumber} - ${room.location}
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
