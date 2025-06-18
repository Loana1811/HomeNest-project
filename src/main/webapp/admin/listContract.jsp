<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>List of Contracts</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f1fdfd;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .container {
                margin-top: 50px;
            }

            h2 {
                color: rgb(0, 128, 128);
                font-weight: bold;
                margin-bottom: 30px;
            }

            .table {
                background-color: #ffffff;
                border-radius: 8px;
                overflow: hidden;
            }

            thead.table-dark th {
                background-color: rgb(0, 128, 128) !important;
                color: #fff;
            }

            .btn-info {
                background-color: rgb(0, 128, 128);
                border-color: rgb(0, 128, 128);
            }

            .btn-info:hover {
                background-color: rgb(0, 100, 100);
                border-color: rgb(0, 100, 100);
            }

            .btn-primary {
                background-color: rgb(0, 128, 128);
                border-color: rgb(0, 128, 128);
            }

            .btn-primary:hover {
                background-color: rgb(0, 100, 100);
                border-color: rgb(0, 100, 100);
            }

            .btn-danger {
                background-color: #dc3545;
            }

            .table th, .table td {
                vertical-align: middle;
                text-align: center;
            }

            .text-danger {
                color: rgb(200, 0, 0);
            }

            .text-center .btn {
                min-width: 130px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2 class="text-center">List of Contracts</h2>

            <c:if test="${not empty contracts}">
                <table class="table table-bordered table-hover shadow-sm">
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
                                <td>${contract.contractstatus}</td>
                                <td>${contract.contractcreatedAt}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/Contracts?action=view&id=${contract.contractId}" class="btn btn-info btn-sm mb-1">
                                        View
                                    </a>
                                    <a href="${pageContext.request.contextPath}/Contracts?action=history&tenantId=${contract.tenantId}" class="btn btn-info btn-sm mb-1">
                                        History
                                    </a>
                                    <form action="${pageContext.request.contextPath}/Contracts" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this contract?');">
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
                <p class="text-center text-danger">No contracts to display</p>
            </c:if>

            <div class="text-center mt-4">
                <a href="${pageContext.request.contextPath}/Contracts?action=create" class="btn btn-primary">Create New Contract</a>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
