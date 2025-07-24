<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/inclu/header_admin.jsp" %>
<html>

    <head>
        <title>List of Contracts</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .alert-custom {
                max-width: 600px;
                margin: 20px auto;
            }
        </style>
    </head>
    <body>
        <div class="container mt-5">
            <h2 class="text-center">List of Contracts</h2>

            <!-- Thông báo lỗi hiển thị NGAY SAU tiêu đề -->
            <c:if test="${not empty sessionScope.deleteError}">
                <div class="alert alert-danger text-center alert-custom">
                    ${sessionScope.deleteError}
                </div>
                <c:remove var="deleteError" scope="session"/>
            </c:if>

            <!-- Bảng hợp đồng -->
            <c:if test="${not empty contracts}">
                <table class="table table-bordered table-hover mt-3">
                    <thead class="table-dark">
                        <tr>
                            <th>Contract ID</th>
                            <th>Tenant ID</th>
                            <th>Tenant Name</th>
                            <th>Room Number</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="contract" items="${contracts}">
                            <tr>
                                <td>${contract.contractId}</td>
                                <td>${contract.tenantId}</td>
                                <td>${contract.tenantName}</td>
                                <td>${contract.roomNumber}</td>
                                <td>${contract.startDate}</td>
                                <td>${contract.endDate}</td>
                                <td>${contract.contractStatus}</td>
                                <td>${contract.contractCreatedAt}</td>

                        <td>
                            <a href="<%= request.getContextPath()%>/Contracts?action=view&id=${contract.contractId}" class="btn btn-primary btn-sm mb-1">View</a>
                            <a href="<%= request.getContextPath()%>/Contracts?action=history&tenantId=${contract.tenantId}" class="btn btn-secondary btn-sm mb-1">View Contract History</a>
                            <form action="Contracts" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this contract?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="contractId" value="${contract.contractId}">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${empty contracts}">
                <p class="text-center text-danger mt-4">No contracts to display</p>
            </c:if>

            <!-- Nút tạo hợp đồng nằm dưới cùng -->
            <div class="text-center mt-4">
                <a href="<%= request.getContextPath()%>/Contracts?action=create" class="btn btn-primary">Create New Contract</a>
                <a href="<%= request.getContextPath()%>/admin/dashboard" class="btn btn-secondary">
                    ← Back to Dashboard
                </a>
            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
