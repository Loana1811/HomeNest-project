<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <title>Edit Contract</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .container {
                max-width: 700px;
                margin-top: 50px;
                background-color: #ffffff;
                border-radius: 15px;
                box-shadow: 0 4px 20px rgba(0, 128, 128, 0.2);
                padding: 40px;
            }

            h2 {
                color: rgb(0, 128, 128);
                font-weight: bold;
                text-align: center;
                margin-bottom: 30px;
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

            .text-center .btn {
                min-width: 120px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2>Edit Contract</h2>
            <form action="${pageContext.request.contextPath}/Contracts" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="contractId" value="${contract.contractId}">

                <!-- Tenant (read-only) -->
                <div class="mb-3">
                    <label class="form-label">Tenant ID</label>
                    <input type="text" class="form-control" name="tenantId" value="${contract.tenantId}" readonly>
                </div>

                <!-- Room -->
                <div class="mb-3">
                    <label for="roomId" class="form-label">Room</label>
                    <select class="form-select" name="roomId" id="roomId" required>
                        <c:forEach var="room" items="${rooms}">
                            <option value="${room.roomID}" ${room.roomID == contract.roomId ? "selected" : ""}>
                                Room ${room.roomNumber} - ${room.location}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Start Date -->
                <div class="mb-3">
                    <label for="startDate" class="form-label">Start Date</label>
                    <input type="date" id="startDate" name="startDate" class="form-control" value="${contract.startDate}" required>
                </div>

                <!-- End Date -->
                <div class="mb-3">
                    <label for="endDate" class="form-label">End Date</label>
                    <input type="date" id="endDate" name="endDate" class="form-control" value="${contract.endDate}">
                </div>

                <!-- Status -->
                <div class="mb-3">
                    <label for="status" class="form-label">Status</label>
                    <select name="status" id="status" class="form-select" required>
                        <option value="Active" ${contract.contractstatus == 'Active' ? 'selected' : ''}>Active</option>
                        <option value="Expired" ${contract.contractstatus == 'Expired' ? 'selected' : ''}>Expired</option>
                        <option value="Terminated" ${contract.contractstatus == 'Terminated' ? 'selected' : ''}>Terminated</option>
                    </select>
                </div>

                <!-- Buttons -->
                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-success me-2">Update</button>
                    <a href="${pageContext.request.contextPath}/Contracts" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
